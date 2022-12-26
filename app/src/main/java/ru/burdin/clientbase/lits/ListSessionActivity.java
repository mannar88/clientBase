package ru.burdin.clientbase.lits;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.lits.actionListSassion.BusyTime;
import ru.burdin.clientbase.lits.actionListSassion.DoubleSession;
import ru.burdin.clientbase.lits.actionListSassion.FreeSession;
import ru.burdin.clientbase.lits.actionListSassion.TransferSession;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.cards.CardSessionActivity;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;

import static java.text.DateFormat.FULL;
import static java.text.DateFormat.getDateInstance;

public class ListSessionActivity extends AppCompatActivity {

    private TextView textViewDay;
  private  TextView textViewTime;
    private   Calendar dateAndTime;
  private   ArrayList <Date> dates;
private RecyclerView recyclerViewTime;
private MyAdapter myAdapter;
LinearLayoutManager linearLayoutManager;
private List <Record> recordsEnpty = new ArrayList<>();
private   Intent intent;
public  static  final  String SETTIME = "setTime";
    private  Bd bd;
private  int countUser;
private  double sum;
private  Intent intentCardSession;
private CheckBox checkBoxUsers;
private  boolean checbox = false;
private  HashMap <String, Consumer> consumerHashMap = new HashMap<>();
private CalendarSetting calendarSetting;
private  int indexListRecord;
public  static  final  int CLASS_INDEX = 2;
    private Intent intentTransfer = new Intent();
Activity activity;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_session);
activity = this;
        bd = Bd.load(getApplicationContext());
     calendarSetting = CalendarSetting.load(this);
        intentCardSession = new Intent(this, CardSessionActivity.class);
        if (savedInstanceState == null) {
            dateAndTime =Calendar.getInstance();
        }else  {
        dateAndTime = new GregorianCalendar();
dateAndTime.setTimeInMillis(savedInstanceState.getLong("dateAndTime"));
            }
indexListRecord = getIntent().getIntExtra(StaticClass.POSITION_LIST_RECORDS,-1);
        textViewDay = findViewById(R.id.textViewDate);
                textViewTime = findViewById(R.id.textViewTime);
textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
checkBoxUsers = findViewById(R.id.checkBoxListSessionUsers);
 recyclerViewTime = findViewById(R.id.listTime);
linearLayoutManager = new LinearLayoutManager(this);
recyclerViewTime.setLayoutManager(linearLayoutManager);
intent = new Intent(this, AddSessionActivity.class);
checkBoxUsers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
checbox = b;
        recUpdate();
    }
});
}

/*
Выбор конкретной даты в календаре
 */
public void onClickTextViwDate(View view) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

/*
Обрабатывает нажатие кнопки ок в календаре
 */
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
dateAndTime.set(i, i1, i2);
            String format = getDateInstance(FULL).format(new Date(dateAndTime.getTimeInMillis()));
        textViewDay.setText(format);
        recUpdate();
        }

};

        /*
Устанавливает список времени выбраного дня
 */
    private  void  initListDate () {
        DateFormat dateFormat = new SimpleDateFormat("\"YYYY-MM-d");
        DateFormat time  = new SimpleDateFormat("HH:mm");
        Calendar calendarFinish = Calendar.getInstance();
dateAndTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Preferences.getString(this, Preferences.APP_PREFERENCES_START_WORK_HOUR, "7")));
dateAndTime.set(Calendar.MINUTE, Integer.parseInt(Preferences.getString(this, Preferences.APP_PREFERENCES_START_WORK_MINITS, "0")));
calendarFinish.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Preferences.getString(this, Preferences.APP_PREFERENCES_FINISH_HOUR, "23")));
calendarFinish.set(Calendar.MINUTE, Integer.parseInt(Preferences.getString(this, Preferences.APP_PREFERENCES_FINISH_MINUTES, "0")));
calendarFinish.set(Calendar.DAY_OF_MONTH, dateAndTime.get(Calendar.DAY_OF_MONTH));
recordsEnpty = bd.getRecords().stream()
        .filter(record -> dateFormat.format(dateAndTime.getTime()).equals(dateFormat.format(record.getStartDay())))
.filter(record -> record.getId() != getIntent().getLongExtra(CardSessionActivity.TRANSFER, -1))
        .collect(Collectors.toList());
    sum = recordsEnpty.stream()
        .collect(Collectors.summingDouble(Record::getPrice));
        textViewTime.setText("Всего записано: " + recordsEnpty.size() + " клиентов, на общую сумму: " + StaticClass.priceToString(sum) + " руб");

        if (!checbox) {
while (time.format(dateAndTime.getTime()).compareToIgnoreCase(time.format(calendarFinish.getTime())) < 0 && dateAndTime.get(Calendar.HOUR_OF_DAY) != 0) {
            Record record = new Record(dateAndTime.getTimeInMillis());
        if (!recordsEnpty.contains(record)) {
            recordsEnpty.add(record);
        }
        dateAndTime.add(Calendar.MINUTE, Integer.parseInt(Preferences.getString(this, Preferences.APP_PREFERENCES_WORK_INTERVAL, "10")));
    }
}
    if (dateAndTime.get(Calendar.DAY_OF_MONTH) != calendarFinish.get(Calendar.DAY_OF_MONTH)) {
        dateAndTime.set(Calendar.DAY_OF_MONTH, calendarFinish.get(Calendar.DAY_OF_MONTH));
    }
        recordsEnpty.sort(Comparator.naturalOrder());
    }

        /*
Выводит список на экран
 */

    @Override
    protected void onResume() {
        super.onResume();
    recUpdate();
    }

    /*
        Инициализирует список
         */
    public  void  recUpdate () {
        DateFormat dateFormatTime = new SimpleDateFormat("HH:mm");
        initListDate();
        setConsumerHashMap();
        Consumer <MyAdapter.ViewHolder> consumer= new Consumer<MyAdapter.ViewHolder>() {
            @Override
            public void accept(MyAdapter.ViewHolder viewHolder) {
                    if (recordsEnpty.get(MyAdapter.count).getId() != 0) {
                        String name = "";
                        for (User user : bd.getUsers()) {
                            if (recordsEnpty.get(MyAdapter.count).getIdUser() == user.getId()) {
                                name = user.getSurname() + " " + user.getName();
                            }
                        }
                        viewHolder.textView.setText(dateFormatTime.format(recordsEnpty.get(MyAdapter.count).getStartDay()) + "  " + name + " " + recordsEnpty.get(MyAdapter.count).getProcedure() + " Удерживайте для связи с клиентом");
                                        } else {
                        viewHolder.textView.setText(dateFormatTime.format(recordsEnpty.get(MyAdapter.count).getStartDay()));
                    }
                        }
        };
MyAdapter.OnUserClickListener <Record> onUserClickListener = new MyAdapter.OnUserClickListener<Record>() {
    @Override
    public void onLongClick(Record record, int position) {
    if (record.getId() != 0) {
        User user = bd.getUsers().get(StaticClass.indexList(record.getIdUser(), bd.getUsers()));
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton("Позвонить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.call(activity);
            }
        });
        builder.setPositiveButton("Написать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.send(activity);
            }
        });
builder.create().show();
    }
    }

    @Override
    public void onUserClick(Record record, int position) {
        if (getIntent().getStringExtra(StaticClass.KEY) == null) {
            getIntent().putExtra(StaticClass.KEY, StaticClass.NEWRECORD);
        }
        if (record.getId() !=0) {
                getIntent().putExtra(StaticClass.KEY, StaticClass.CARDSESSION);
        }

        String key = getIntent().getStringExtra(StaticClass.KEY);
        consumerHashMap.get(key).accept(record);
    if (key.equals(StaticClass.DUPLICATION) && DoubleSession.checkDouble){
        DoubleSession.checkDouble = false;
        setResult(RESULT_OK);
        finish();
    }
if (key.equals(StaticClass.TRANSFER)&& TransferSession.checkTransfer) {
    TransferSession.checkTransfer = false;
    setResult(RESULT_OK);
finish();
}
    getIntent().removeExtra(StaticClass.KEY);

    }
};
MyAdapter myAdapter = new MyAdapter(this, recordsEnpty, onUserClickListener, consumer);
        recyclerViewTime.setAdapter(myAdapter);

    }

     /*
    Переводит дату на прошлый день
     */
     public void onClickButtonBackDay(View view) {
dateAndTime.setTimeInMillis(dateAndTime.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));
    textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()) + " Щёлкните для установки даты");
    recUpdate();
        view.announceForAccessibility(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
     }

/*
Переводит дату на следущий день
 */
public void onClickButtonNextDay (View view) {
dateAndTime.setTimeInMillis(dateAndTime.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
    textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()) + " Щёлкните для установки даты");
    recUpdate();
    view.announceForAccessibility(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
}

/*
Устанавливает в хэшмапу обработчик нажатия по списку
 */
private  void setConsumerHashMap() {
//Если запись свободна
    consumerHashMap.put(StaticClass.NEWRECORD, new FreeSession(this));
//Если запись уже существует
    consumerHashMap.put(StaticClass.CARDSESSION, new BusyTime(this));
    // Дублирование
    consumerHashMap.put(StaticClass.DUPLICATION, new DoubleSession(this, indexListRecord, calendarSetting));
// Перенос
consumerHashMap.put(StaticClass.TRANSFER, new TransferSession(this, getIntent().getLongExtra(CardSessionActivity.TRANSFER, 1), calendarSetting));
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == User.CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                StaticClass.getDialog(this, "на совершение звонков");
            }
        }
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}