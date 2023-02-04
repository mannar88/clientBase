package ru.burdin.clientbase.cards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.analytics.Analytics;
import ru.burdin.clientbase.lits.actionListSassion.DoubleSession;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.lits.ListSessionActivity;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;

public class CardSessionActivity extends AppCompatActivity {

    private Record record;
    private User user;
    private Bd bd;
    private TextView textViewDate;
    private  TextView textViewNameUser;
    private     TextView textViewProcedure;
    private  TextView textViewPrice;
    private  TextView textViewTimeEnd;
    private  TextView textViewComment;
private  TextView textViewPlaceOnTheList;
private  CheckBox checkBoxPlaceOnTheList;
private CheckBox checkBoxNotNotification;
    private  int indexUser;
private  long recordId = -1;
private CalendarSetting calendarSetting;
public  static  final  String TRANSFER = "transfer";
public  static  final  int TRANSFER_INT = 67;
Activity context;
DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-YYYY, EEEE");


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_card_session);
    setTitle("");

    try {
        bd = Bd.load(getApplicationContext());
    } catch (InterruptedException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (ExecutionException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (TimeoutException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
    calendarSetting = CalendarSetting.load(this);
    recordId = getIntent().getLongExtra(StaticClass.POSITION_LIST_RECORDS, -1);
    if (recordId != -1) {
        record = bd.getRecords().get(StaticClass.indexList(recordId, bd.getRecords()));
    } else {
        finish();
    }

    textViewDate = findViewById(R.id.textViewCardSessionDate);
    textViewNameUser = findViewById(R.id.textViewCardSessionNameUser);
    textViewProcedure = findViewById(R.id.textViewCardSessionProcedures);
    textViewPrice = findViewById(R.id.textViewCardSessionPrice);
    textViewTimeEnd = findViewById(R.id.textViewCardSessionTimeEnd);
    textViewComment = findViewById(R.id.textViewCardSessionComment);
textViewPlaceOnTheList = findViewById(R.id.textvIewCardSessionPlaceOnTheList);
checkBoxPlaceOnTheList = findViewById(R.id.checkBoxCardSessionPlaceOnTheList);
checkBoxNotNotification = findViewById(R.id.checkBoxCardSessionNotNotification);
    this.context = this;
}

    @Override
    protected void onStart() {
        super.onStart();
        setScreenInfo(record);
}

    @Override
    protected void onResume() {
        super.onResume();
checkBoxPlaceOnTheList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ContentValues contentValues = new ContentValues();
    contentValues.put(Bd.COLUMN_ONE_IN_LINE, StaticClass.booleaILong(b));
        if (bd.update(bd.TABLE_SESSION, contentValues, record.getId()) == 1){
record.setOneLine(StaticClass.booleaInInt(b));
            int [] arr = Analytics.placeOnTheList(bd.getRecords(), user.getId(), record.getId());
textViewPlaceOnTheList.setText("Сеанс в курсе: " + arr[0] + ", курс: " + arr[1] + ", всего: " + arr[2] + " из " + arr[3]);
        }
    }
});
        checkBoxNotNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ContentValues contentValues = new ContentValues();
    long res = StaticClass.booleaILong(b);
    contentValues.put(Bd.COLUMN_not_notification, res);
    if (bd.update(Bd.TABLE_SESSION, contentValues, record.getId()) == 1) {
record.setNotNotification(res);
            }
    }
});
    }

/*
    Устанавливает информацию на экран
     */
    private  void  setScreenInfo (Record record) {
        indexUser = StaticClass.indexList(record.getIdUser(), bd.getUsers());
        user = bd.getUsers().get(indexUser);
int [] arr = Analytics.placeOnTheList(bd.getRecords(), user.getId(), record.getId());
        textViewDate.setText("Время записи: "  + dateFormat.format(new Date(record.getStart())));
        textViewNameUser.setText("Клиент: " +user.getSurname() + " " + user.getName() + " Нажмите, что бы открыть карточку клиента.");
        textViewProcedure.setText("Услуги: " + record.getProcedure());
        textViewPrice.setText("Стоимость: " + StaticClass.priceToString(record.getPrice()));
        textViewTimeEnd.setText("Продолжительность услуги: " + TimeUnit.MILLISECONDS.toMinutes(record.getEnd()) + " минут");
        textViewComment.setText("Комментарии: "+  record.getComment());
textViewPlaceOnTheList.setText("Сеанс в курсе: " + arr[0] + ", курс: " + arr[1] + ", всего: " + arr[2] + " из " + arr[3]);
checkBoxPlaceOnTheList.setChecked(StaticClass.intInBoolean(record.getOneLine()));
if (arr[2] == 1){
checkBoxPlaceOnTheList.setChecked(true);
    checkBoxPlaceOnTheList.setEnabled(false);
}
checkBoxNotNotification.setChecked(StaticClass.longInBoolean(record.getNotNotification()));
    }

    /*
    Открывает карточку клиента
     */
    public void onClickTextViewCardSessionNameUser(View view) {
        Intent intent = new Intent(this, CardUserActivity.class);
        intent.putExtra(Bd.TABLE,indexUser );
        startActivity(intent);
     }


    /*
    Дублирование записи
     */
    public void onClickButtonCardSessionDooble(View view) {
DateFormat dateFormat = new SimpleDateFormat("EEEE, dd-MM-YYYY HH:mm");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Calendar calendar = new GregorianCalendar();
calendar.setTimeInMillis(record.getStart());
String [] times = new String[30];
long[] longs = new  long[30];
int i =0;
while (times[times.length -1] == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        Record recordTrue = new Record(record);
        recordTrue.setStart(calendar.getTimeInMillis());
        if (!bd.getRecords().contains(recordTrue) || Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, false)) {
            times[i] = dateFormat.format(calendar.getTime());
            longs[i++] = calendar.getTimeInMillis();
        }
        }
builder.setPositiveButton("Дублировать однократно", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(getApplicationContext(), ListSessionActivity.class);
        intent.putExtra(StaticClass.KEY, StaticClass.DUPLICATION);
        intent.putExtra(StaticClass.POSITION_LIST_RECORDS, StaticClass.indexList(record.getId(), bd.getRecords()));
        startActivityForResult(intent,ListSessionActivity.CLASS_INDEX);
    }
});
    builder.setNegativeButton("Дублировать пакетно без изменения времени", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
doubPacket(times, longs);
        }
    });
    builder.create().show();
    }

    /*
    Пакетное время для дублирования
     */
    private  void  doubPacket (String[] times, long [] longs) {
Set<Long> longHashSet = new HashSet<>();
    boolean [] booleans = new  boolean[times.length];
AlertDialog.Builder builder = new AlertDialog.Builder(this);builder.setMultiChoiceItems(times, booleans, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
if (b ) {
    longHashSet.add(longs[i]);
}
if (!b && longHashSet.contains(longs[i])){
    longHashSet.remove(longs[i]);
}

            }
        });
        builder.setPositiveButton("Дублировать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (longHashSet.size() > 0) {
for (Long l : longHashSet) {
    Consumer<Record> doubleSession = new DoubleSession(getApplicationContext(), StaticClass.indexList(recordId, bd.getRecords()), calendarSetting);
doubleSession.accept(new Record(l));
}
    Toast.makeText(getApplicationContext(), "Записи дублированы", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Ниччего не выбрано", Toast.LENGTH_SHORT).show();
                }
            }
        });

    builder.create().show();;}


    /*
Редактирование записи
 */
public void onclickButtonCardSessionRead(View view) {
Intent intent = new Intent(this, AddSessionActivity.class);
intent.putExtra(StaticClass.POSITION_LIST_RECORDS, StaticClass.indexList(record.getId(), bd.getRecords()));
startActivityForResult(intent, AddSessionActivity.CLASS_INDEX);
}

    /*
Кнопка  для удаления записи
 */
    public void onClickButtonCardSessionDelete(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Удалить запись из расписания?");
            builder.setPositiveButton("Удалить без уведомления", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
 if (delete()) {
     finish();
 }
                }
            });
            builder.setNegativeButton("Удалить и уведомить клиента по SMS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (SendSMS.permission(getApplicationContext())) {
                    if (delete()) {

           Record recordNotification = new Record(record);
           SendSMS.send(context, Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(3), SendSMS.TEMPLETS.get(3)), recordNotification, R.id.radioButtonAddSessionSMS);
                        finish();
       }
   }else {
                        requestPermissions(new  String[] {
                            Manifest.permission.SEND_SMS }, SendSMS.PERMISSION_SMS);
                    }
                                    }
            });
            builder.setNeutralButton("Удалить и уведомить по WHatsApp", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Record recordNotification = new Record(record);
                    if (delete()) {
                        SendSMS.send(context, Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(3), SendSMS.TEMPLETS.get(3)), recordNotification, R.id.radioButtonAddSessionWAthsApp);
                        finish();
                    }
                }
            });
            builder.create().show();
        }


    /*
    метод удаления сеанса
     */
    private  boolean   delete () {
        int resultDelete = bd.delete(Bd.TABLE_SESSION, record.getId());
        if (resultDelete == 1) {
            long id = bd.getRecords().remove(StaticClass.indexList(record.getId(), bd.getRecords())).getEvent_id();
            if (calendarSetting.delete(id) == 0) {
                Toast.makeText(getApplicationContext(), "Не удалось удалить запись в календаре", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), "Запись удалена", Toast.LENGTH_SHORT).show();
        }
        return  resultDelete == 0? false:true;
    }

    /*
  Метод определяет из какой активности вернулись и какие данные пришли
  **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case  ListSessionActivity.CLASS_INDEX:
                Toast.makeText(this, "Запись дублирована", Toast.LENGTH_LONG).show();
            break;
                case AddSessionActivity.CLASS_INDEX:
                setScreenInfo(record);
                    Toast.makeText(getApplicationContext(), "Запись изменена", Toast.LENGTH_SHORT).show();
            break;
                case TRANSFER_INT:
            setScreenInfo(record);
Toast.makeText(this, "Запись успешно перенесена", Toast.LENGTH_SHORT).show();
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("Уведомить клиента о переносе?");
builder.setPositiveButton("Нет, пусть будет нежданчик", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
   dialogInterface.cancel();
    }
});
builder.setNegativeButton("Уведомить по SMS", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (SendSMS.permission(getApplicationContext())){
        SendSMS.send(getApplicationContext(), Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(2), SendSMS.TEMPLETS.get(2)), record, R.id.radioButtonAddSessionSMS);
    }else {
            requestPermissions(new  String[]{Manifest.permission.SEND_SMS}, SendSMS.PERMISSION_SMS);
        }
    }
});
builder.setNeutralButton("Уведомить по WhatsApp", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
   SendSMS.send(context, Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(2), SendSMS.TEMPLETS.get(2)), record, R.id.radioButtonAddSessionWAthsApp);
    }
});
builder.create().show();
break;
      }
        }
    }

    /*
    Перенос
     */
    public void onClickButtonCardSessionTransfer(View view) {
    Intent intent = new Intent(this, ListSessionActivity.class);
        intent.putExtra(TRANSFER,record.getId());
        intent.putExtra(StaticClass.KEY, StaticClass.TRANSFER);
        intent.putExtra(StaticClass.POSITION_LIST_RECORDS, StaticClass.indexList(record.getId(), bd.getRecords()));
        startActivityForResult(intent,TRANSFER_INT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
if (requestCode == SendSMS.PERMISSION_SMS) {
    if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

    }else {
        StaticClass.getDialog(context, "на отправку SMS");
    }
}
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}