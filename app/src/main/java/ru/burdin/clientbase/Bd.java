package ru.burdin.clientbase;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ru.burdin.clientbase.importAndExport.BdImportExport;
import ru.burdin.clientbase.models.Expenses;
import ru.burdin.clientbase.models.Procedure;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

public class Bd {

private  static Bd bd;
public static final String DATABASE_NAME = "clientBase.db";
    public static final int SCHEMA = 8;
    public  static final String TABLE = "users";
    public  static  final String TABLE_PROCEDURE = "procedures";
    public  static  final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PRICE = "price";
    public  static  final String COLUMN_COMMENT = "Comment";
    public  static  final  String COLUMN_TIME_END = "time_end";
    public  static  final  String TABLE_SESSION = "sessions";
    public  static  final  String COLUMN_TIME = "time";
    public  static final  String COLUMN_ID_USER = "id_user";
public  static  final  String COLUMN_PROCEDURE = "procedire";
public  static  final  String COLUMN_EVENT_ID = "event_id";
    public  static  final  String COLUMN_not_notification = "not_notification";
    public  static  final  String COLUMN_ONE_IN_LINE = "one_in_line";
    public  static  final  String COLUMN_PAY ="pay";
    public   DatabaseHelper databaseHelper;
public   SQLiteDatabase sqLiteDatabase;
private  List <User> users;
private  List <Procedure> procedures;
private List<Record> records;
private  List <Expenses> expenses;
private    Context staticContex;
private BdImportExport bdImportExport;

private  Bd (Context context) {
    this.staticContex = context;
    databaseHelper = new DatabaseHelper(context);
    sqLiteDatabase = databaseHelper.getReadableDatabase();
            collectListUsers();
                    collectProcedures();
        collectRecord();
        collectExpenses();
bdImportExport = new BdImportExport(context.getDatabasePath(Bd.DATABASE_NAME).getPath());
}

    public List<Expenses> getExpenses() {
        return expenses;
    }
/*
Создание объекта База данных
 */
    public  static  Bd load (Context context)  {
    Supplier <Bd> supplier = new Supplier<Bd>() {
        @Override
        public Bd get() {
if (bd == null || bd.databaseHelper == null || bd.sqLiteDatabase == null
|| bd.users == null || bd.records == null || bd.procedures == null || bd.expenses == null) {
    bd = new Bd(context);
}
            return bd;
        }
    };
    AsyncTaskBd <Bd> asyncTaskBd = new AsyncTaskBd<>();
    asyncTaskBd.execute(supplier);
        Bd result = null;
        try {
            result = asyncTaskBd.get(3, TimeUnit.DAYS.SECONDS);
        } catch (InterruptedException e) {
            Toast.makeText(context.getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context.getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(context.getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

            return  result;
    }

private  Bd getBd () {
    if (bd == null) {
        Consumer <Context> consumer = context ->bd = new Bd(context);
    }
    return  bd;
}

/*
Пересоздание объекта База данных
 */
public void  reStart () throws InterruptedException, ExecutionException, TimeoutException {
    bd = null;
    load(staticContex);
}

    public  List<User> getUsers() {
    return  users;
    }

    public List<Procedure> getProcedures() {
        return procedures;
    }

    public  List<Record> getRecords() {
records.forEach(
        record -> record.setPlaceInLine(0)
);
records.sort(Comparator.naturalOrder());
for (Record record:records){
    if (record.getPlaceInLine() == 0){
        int count = 1;
        for (Record record1:records){
            if (record.getIdUser() == record1.getIdUser()) {
                record1.setPlaceInLine(count++);
            }
            }
        }
    }
return records;

}

    /*
Добавить
 */
    public  long  add (String table, ContentValues contentValues) {
AsyncTaskBd <Long> asyncTaskBd = new AsyncTaskBd();

        long  result = 0;
    Supplier <Long>  supplier = ()-> sqLiteDatabase.insert(table, null, contentValues);
    asyncTaskBd.execute(supplier);
        try {
            result = asyncTaskBd.get(1, TimeUnit.DAYS.SECONDS);
        } catch (ExecutionException e) {
            Toast.makeText(staticContex.getApplicationContext(),  "Не удалось добавить в базу " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | TimeoutException e) {
            Toast.makeText(staticContex.getApplicationContext(), "Не удалось добавить в базу " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        autoExport(result);
                return  result;
}

/*
Автоматическиий экспорт БД на устройство
 */
private  void  autoExport (Long result ) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (result != 0 && Preferences.getBoolean(staticContex, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false)) {
                    try {
                        bdImportExport.exportBd();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
Thread thread = new Thread(runnable);
thread.start();
    }

/*
Сбор в список клиентов
 */
    private  void  collectListUsers () {
users = new ArrayList<>();
    Cursor userCursor = sqLiteDatabase.rawQuery("select * from "+ TABLE, null);
    while (userCursor.moveToNext()) {
    users.add(new User(userCursor.getLong(0), userCursor.getString(1) + "", userCursor.getString(2) + "", userCursor.getString(3) + "", userCursor.getString(4) + ""));
}
userCursor.close();
users.sort(Comparator.naturalOrder());
}

/*
Сбор в список процедуры
 */
private  void  collectProcedures () {
    procedures = new ArrayList<>();
    Cursor procedureCursor = sqLiteDatabase.rawQuery("select * from "+ TABLE_PROCEDURE, null);
    while (procedureCursor.moveToNext()) {
    procedures.add(new Procedure(procedureCursor.getLong(0), procedureCursor.getString(1) + "", procedureCursor.getDouble(2), procedureCursor.getLong(3)));
    }
procedureCursor.close();
    }

/*
Сбор в список сеансы
 */
    private  void  collectRecord () {
    records = new CopyOnWriteArrayList<>();
Cursor cursorRecord = sqLiteDatabase.rawQuery("select * from "+ TABLE_SESSION, null);
while (cursorRecord.moveToNext()) {
records.add(new Record(cursorRecord.getLong(0), cursorRecord.getLong(1), cursorRecord.getLong(2), cursorRecord.getLong(3), cursorRecord.getString(4) + "", cursorRecord.getDouble(5), cursorRecord.getString(6) + "", cursorRecord.getLong(7), cursorRecord.getLong(8), cursorRecord.getInt(9), cursorRecord.getDouble(10)));
    }

cursorRecord.close();
}

/*
Сбор в список расходы
 */
private  void collectExpenses () {
    expenses = new ArrayList<>();
    Cursor cursorExpenses =sqLiteDatabase.rawQuery("select * from "+ TABLE_EXPENSES, null);
while (cursorExpenses.moveToNext()) {
    expenses.add(new Expenses(cursorExpenses.getLong(0), cursorExpenses.getLong(1), cursorExpenses.getString(2) + "", cursorExpenses.getDouble(3)));
}
cursorExpenses.close();
}

public  int delete (String table, long id) {
AsyncTaskBd <Integer> asyncTaskBd = new AsyncTaskBd<>();
    Supplier<Integer> supplier = ()-> sqLiteDatabase.delete(table, "_id = ?", new String[]{String.valueOf(id)});
    int result = 0;
      asyncTaskBd.execute(supplier);
    try {
        result = asyncTaskBd.get(1, TimeUnit.DAYS.SECONDS);
    } catch (ExecutionException e) {
        Toast.makeText(staticContex.getApplicationContext(), "Не удалось удалить из  базы " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (InterruptedException | TimeoutException e) {
        Toast.makeText(staticContex.getApplicationContext(), "Не удалось удалить из  базы " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
    autoExport((long)result);
    return  result;
}

public     int update  (String table, ContentValues contentValues, long id) {
    AsyncTaskBd <Integer> asyncTaskBd = new AsyncTaskBd<>();
    Supplier <Integer> supplier =()-> sqLiteDatabase.update(table, contentValues, COLUMN_ID + "=" + id, null);

    int result = -1;
    asyncTaskBd.execute(supplier);
    try {
        result = asyncTaskBd.get(1, TimeUnit.SECONDS);
    } catch (ExecutionException e) {
        Toast.makeText(staticContex.getApplicationContext(), "Не удалось обновить базу " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (InterruptedException e) {
        Toast.makeText(staticContex.getApplicationContext(), "Не удалось обновить базу " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (TimeoutException e) {
        Toast.makeText(staticContex.getApplicationContext(), "Не удалось обновить базу " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
    autoExport((long)result);
        return result;
}

private  class  DatabaseHelper extends SQLiteOpenHelper {

public  DatabaseHelper (Context context) {
    super(context, DATABASE_NAME, null, SCHEMA);
}
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
sqLiteDatabase.execSQL("CREATE TABLE " + TABLE + "(" + COLUMN_ID
+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
+ " TEXT, " + COLUMN_SURNAME + " TEXT,"
        +COLUMN_PHONE + " TEXT,"
        + COLUMN_COMMENT + " TEXT" + ");");

sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PROCEDURE + "(" + COLUMN_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
                    + " TEXT,"
+ COLUMN_PRICE + " REAL,"
         + COLUMN_TIME_END + " INTEGER" + ");");

            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SESSION + "(" + COLUMN_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIME + " INTEGER,"
                    + COLUMN_TIME_END + " INTEGER,"
                    + COLUMN_ID_USER + " INTEGER,"
+ COLUMN_PROCEDURE + " TEXT,"
                    + COLUMN_PRICE + " REAL,"
                    +COLUMN_COMMENT + " TEXT,"
                            + COLUMN_EVENT_ID + " INTEGER,"
                            + COLUMN_not_notification + " INTEGER,"
                            + COLUMN_ONE_IN_LINE + " INTEGER,"
                    + COLUMN_PAY + " REAL"
                    + ");");

            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_EXPENSES + "(" + COLUMN_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
+ COLUMN_TIME + " INTEGER,"
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_PRICE + " REAL);");

}

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
if (i < 7){
    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SESSION + " ADD "+ COLUMN_not_notification + " INTEGER;");
    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SESSION + " ADD "+ COLUMN_ONE_IN_LINE + " INTEGER;");
}
if (i < 8) {
    addPay(sqLiteDatabase);
}
}
/*
Добавляет колонку оплата
 */
    private void addPay(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SESSION + " ADD "+ COLUMN_PAY + " REAL;");
        List<Record> records = new ArrayList<>();
        Cursor cursorRecord = sqLiteDatabase.rawQuery("select * from "+ TABLE_SESSION, null);
        while (cursorRecord.moveToNext()) {
            records.add(new Record(cursorRecord.getLong(0), cursorRecord.getLong(1), cursorRecord.getLong(2), cursorRecord.getLong(3), cursorRecord.getString(4) + "", cursorRecord.getDouble(5), cursorRecord.getString(6) + "", cursorRecord.getLong(7), cursorRecord.getLong(8), cursorRecord.getInt(9), 0.00));
        }
        cursorRecord.close();
    for (Record record:records) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PAY, record.getPrice());
        sqLiteDatabase.update(TABLE_SESSION, contentValues, COLUMN_ID + "=" + record.getId(), null);
    }
    }
}

   private static class  AsyncTaskBd<T> extends  AsyncTask<Supplier<T>, Void, T> {


       @Override
       protected T doInBackground(Supplier<T>... suppliers) {
           return suppliers[0].get();
       }
   }
    }
