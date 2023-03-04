package ru.burdin.clientbase.lits;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.add.AddClientActivity;
import ru.burdin.clientbase.cards.CardUserActivity;
import ru.burdin.clientbase.lits.listClient.ListClientActivity;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;

import static com.parse.Parse.getApplicationContext;

public class SelectAddClient            {

    private  Bd bd;
    private  List<String> resultContact = new ArrayList<>();
    private ListClientActivity activity;
public  static  final  int PERNISSION_LOG_COLL = 5;
public  static  final  int PERMISSION_PHONE_BOOK = 6;
private  static  final  String  CLIENT = " клиент ";
public SelectAddClient(ListClientActivity activity) {
    this.activity =(ListClientActivity)activity;
        bd = Bd.load(activity);
}

    /*
    Вызывает диалог
     */

    public void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setPositiveButton("Заполнить в ручную", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(activity, AddClientActivity.class);
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton("Взять номер из журнала вызовов", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (requestSinglePermission(Manifest.permission.READ_CALL_LOG, PERNISSION_LOG_COLL)) {
                    callLog();
                }
                }
        });
        builder.setNeutralButton("Заполнить из контактов", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            if (requestSinglePermission(Manifest.permission.READ_CONTACTS, PERMISSION_PHONE_BOOK)) {
                phoneBooke();
            }
            }
        });
        builder.create().show();

    }

    /*
    Журнал звонков
     */
    public void callLog() {
        Map<String, Contact> colls = new LinkedHashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-YYYY");
        AlertDialog.Builder builderColl = new AlertDialog.Builder(activity);
        String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        String where = "";
        Cursor cursor = activity.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                where,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
if (!colls.containsKey(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))) || colls.get(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))).date < cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))) {
    colls.put(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)), new Contact(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)), cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)), cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)), cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))));
}
} while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        List<Contact> list = new ArrayList<>(colls.values());
list.sort(Comparator.naturalOrder());
String[] contacts = new  String[list.size()];
        for (int i = 0; i < contacts.length; i++) {
            String client = "";
for (User user: bd.getUsers()) {
    if (user.getPhone().contains(list.get(i).number)) {
        client = CLIENT + user.getSurname() + " " + user.getName();
list.get(i).user_id = user.getId();
    }
}
            contacts[i] =list.get(i).type + " "+ client +  list.get(i).name + " " +  list.get(i).number + " " + dateFormat.format(list.get(i).date);
        }
builderColl.setItems(contacts, new DialogInterface.OnClickListener() {
            @Override
              public void onClick(DialogInterface dialogInterface, int i) {
if (list.get(i).user_id == -1) {
    Intent intent = new Intent(activity, AddClientActivity.class);
    intent.putExtra(StaticClass.NUMBER_PHONE, list.get(i).number);
    activity.startActivity(intent);
}else {
    Intent intent = new Intent(activity, CardUserActivity.class);
    intent.putExtra(Bd.TABLE, StaticClass.indexList(list.get(i).user_id, bd.getUsers()));
activity.startActivity(intent);
}
}
        });

        builderColl.create().show();
    }

/*
Запрос к телефонной книге
 */
  public void phoneBooke () {
      View linearlayout =activity.getLayoutInflater().inflate(R.layout.serch , null);
ListView       listView = linearlayout.findViewById(R.id.listViewSearchContats);
      EditText editText = linearlayout.findViewById(R.id.editTextContactSearsh);
      Button button = linearlayout.findViewById(R.id.buttonContactAdd)
;      Map <String, String> contacts = new TreeMap<>();
      getContacts(contacts);
      List <String[]> result = new ArrayList<>();
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setView(linearlayout);
List <String> list = new ArrayList<>();
contacts.keySet().forEach(s -> list.add(s));
Comparator <String> comparator = (String::compareToIgnoreCase);
list.sort(comparator);
boolean [] checks = new  boolean[contacts.size()];
listScreen(listView, list);
search(editText, list,  listView);
AlertDialog alertDialog = builder.create();
buttonAddConact(button, contacts, alertDialog);
alertDialog.show();

  }

  /*
  Слушатель листа контактов
   */
  private   void   listViewSetOnItemClickListener (ListView listViewt, List <String> nameSurname) {
listViewt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        resultContact.clear();
                SparseBooleanArray resultBoolaen = ((ListView) adapterView).getCheckedItemPositions();
        for (int j = 0; j < resultBoolaen.size(); j++) {
            if (resultBoolaen.valueAt(j)) {
resultContact.add(nameSurname.get(resultBoolaen.keyAt(j)));            }
        }

    }
});
  }

  /*
Кнопка добавления
 */
private   void  buttonAddConact (Button button, Map <String, String> map, AlertDialog alertDialog) {
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (resultContact.size() >0) {
resultContact.forEach(s -> setContact(s.split(" ", 2), map.get(s)));
                if (resultContact.size() == 1) {
    Toast.makeText(activity, "Контакт добавлен в Клиентскую базу", Toast.LENGTH_SHORT).show();
}else {
    Toast.makeText(activity, "Контакты успешно добавлены в Клиентскую базу", Toast.LENGTH_SHORT).show();
}
                alertDialog.cancel();
            }else{
        Toast.makeText(activity, "Ничего не выбрано", Toast.LENGTH_SHORT).show();
    }
}
    });
}

/*
  Слушатель поиска
   */
private  void  search (EditText editText, List <String> list, ListView listView) {
    editText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            List <String> names = new ArrayList<>();
String string = charSequence.toString();
            for (String name : list){
            if (name.toLowerCase().contains(string.toLowerCase())) {
names.add(name);
            }
        }
        listScreen(listView, names);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    });

                                         }

                                         /*
                                         Создает список контактов на экране
                                          */
    private  void  listScreen (ListView listView, List <String> list) {

        ArrayAdapter<?> arrayAdapter =new   ArrayAdapter (activity,
                android.R.layout.simple_list_item_multiple_choice,
                list);

        listView.setAdapter(arrayAdapter);
try {
    listViewSetOnItemClickListener(listView, list);
}catch ( Exception e) {
    Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
}
    }

    /*
  СВыгружает списо контактов
   */
    private  void  getContacts (Map <String, String> contacts) {
    Cursor cursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null);
    if (cursor != null) {
        while (cursor.moveToNext()) {
            contacts.put(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE)).trim() + "", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim() + "");
        }
    }
    if (cursor != null) {
        cursor.close();
    }

}
    /*
  Добавление контакта вбазу
   */
  private  void  setContact (String[] name, String phone) {
      Bd bd = null;
          bd = Bd.load(activity);
      ContentValues contentValues =new ContentValues();
      for (int i = 0; i < name.length; i++) {
          if (i == 0) {
              contentValues.put(Bd.COLUMN_SURNAME, name[0]);
          }else {
              contentValues.put(Bd.COLUMN_NAME, name[1]);
          }
      }
contentValues.put(Bd.COLUMN_PHONE, phone);
 long id = bd.add(Bd.TABLE, contentValues, Preferences.getBoolean(activity.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
  if (id > 0) {
      User user = new User(id, contentValues.getAsString(Bd.COLUMN_NAME) + "", contentValues.getAsString(Bd.COLUMN_SURNAME), phone, "");
  bd.getUsers().add(user);
  bd.getUsers().sort(Comparator.naturalOrder());
  activity.updateList();
  }
  }
  /*
Разрешение
 */
    private boolean requestSinglePermission(String permission, int request) {
        boolean result = false;
        String callPermission = permission;
        int hasPermission = activity.checkSelfPermission(callPermission);
        String[] permissions = new String[]{callPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
           activity.requestPermissions(permissions, request);
        } else {
            result = true;
        }
        return result;
    }
        class  Contact implements  Comparable {
long id;
            String number;
            long date;
String name;
String type;
 long user_id = -1;
public Contact( String number, long date, String name, int type) {
                this.number = number;
                this.date = date;
            this.name = name;
this.type =type ==1? "Входящий":"Исходящий";
}

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Contact contact = (Contact) o;

                return number.equals(contact.number);
            }

            @Override
            public int hashCode() {
                int result = number.hashCode();
                result = 31 * result + (int) (date ^ (date >>> 32));
                return result;
            }

            @Override
            public int compareTo(Object o) {
                Contact contact = (Contact)o;
return  Long.compare(contact.date, this.date);
            }
        }

}
