package ru.burdin.clientbase.lits.litsSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.burdin.clientbase.MainActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.lits.actionListSassion.BusyTime;
import ru.burdin.clientbase.lits.actionListSassion.DoubleSession;
import ru.burdin.clientbase.lits.actionListSassion.FreeSession;
import ru.burdin.clientbase.lits.actionListSassion.HistoryNewSession;
import ru.burdin.clientbase.lits.actionListSassion.TransferSession;
import ru.burdin.clientbase.notificationSMS.LoadAlarmMananger;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.cards.cardSession.CardSessionActivity;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

import static java.text.DateFormat.FULL;
import static java.text.DateFormat.getDateInstance;

public class ListSessionActivity extends AppCompatActivity {

    //?????????? ?????? ??????????????????
    public static final String CALENDAR ="calendar";
    private TextView textViewDay;
    TextView textViewTime;
    private   Calendar dateAndTime;
  private   ArrayList <Date> dates;
private RecyclerView recyclerViewTime;
private MyAdapter myAdapter;
 List <Record> recordsEnpty = new ArrayList<>();
public  static  final  String SETTIME = "setTime";
      Bd bd;
private  int countUser;
private  double sum;
private CheckBox checkBoxUsers;
private  boolean checbox = false;
private  HashMap <String, Consumer> consumerHashMap = new HashMap<>();
 CalendarSetting calendarSetting;
private  int indexListRecord;
public  static  final  int CLASS_INDEX = 2;
private  Activity activity;
private  boolean focus;
ListSession listSession;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_session);
        setTitle("");
        activity = this;
        bd = Bd.load(getApplicationContext());
    listSession = new ListSession(this);
        calendarSetting = CalendarSetting.load(this);
    if (savedInstanceState == null) {
        LoadAlarmMananger loadAlarmMananger = new LoadAlarmMananger(this);
        loadAlarmMananger.execute();
    }
    dateAndTime = new GregorianCalendar();
    dateAndTime.setTimeInMillis(getIntent().getLongExtra(CALENDAR, new  Date().getTime()));
getIntent().removeExtra(CALENDAR);
    indexListRecord = getIntent().getIntExtra(StaticClass.POSITION_LIST_RECORDS,-1);
        textViewDay = findViewById(R.id.textViewDate);
                textViewTime = findViewById(R.id.textViewTime);
textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
checkBoxUsers = findViewById(R.id.checkBoxListSessionUsers);
 recyclerViewTime = findViewById(R.id.listTime);
checkBoxUsers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
checbox = b;
        recUpdate();
    }
});
}

/*
?????????? ???????????????????? ???????? ?? ??????????????????
 */
public void onClickTextViwDate(View view) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

/*
???????????????????????? ?????????????? ???????????? ???? ?? ??????????????????
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
?????????????????????????? ???????????? ?????????????? ?????????????????? ??????
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
        textViewTime.setText("?????????? ????????????????: " + recordsEnpty.size() + " ????????????????, ???? ?????????? ??????????: " + StaticClass.priceToString(sum) + " ??????. ??????????????????????, ?????? ???? ?????????????? ???????? ????????????????????.");

        if (!checbox) {
while (time.format(dateAndTime.getTime()).compareToIgnoreCase(time.format(calendarFinish.getTime())) < 0 && dateAndTime.get(Calendar.HOUR_OF_DAY) != 0) {
            Record record = new Record(dateAndTime.getTimeInMillis());
        if (!recordsEnpty.contains(record) || Preferences.getBoolean(getApplicationContext(),Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, false)) {
            recordsEnpty.add(record);
        }
        dateAndTime.add(Calendar.MINUTE, Integer.parseInt(Preferences.getString(this, Preferences.APP_PREFERENCES_WORK_INTERVAL, "10")));
    }
}
    if (dateAndTime.get(Calendar.DAY_OF_MONTH) != calendarFinish.get(Calendar.DAY_OF_MONTH)) {
//        dateAndTime.set(Calendar.DAY_OF_MONTH, calendarFinish.get(Calendar.DATE));
    }
        recordsEnpty.sort(Comparator.naturalOrder());
    }

        /*
?????????????? ???????????? ???? ??????????
 */

    @Override
    protected void onResume() {
        super.onResume();
    recUpdate();
    listSession.textViewMenu();
    }

    /*
        ???????????????????????????? ????????????
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
                                name = user.getSurname() + " " + user.getName() + "";
                            }
                        }
                        viewHolder.textView.setText(dateFormatTime.format(recordsEnpty.get(MyAdapter.count).getStartDay()) + "  " + name + " " + recordsEnpty.get(MyAdapter.count).getProcedure()
                                + " ?????????????????????? ?????? ?????????? ?? ????????????????");
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
        builder.setNegativeButton("??????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.call(activity);
            }
        });
            builder.setPositiveButton("????????????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                user.send(activity);
                }
            });

builder.setNeutralButton("?????????????????? ??????????????????????", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setPositiveButton("WhatsApp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

try {
        SendSMS.send(activity, Preferences.getString(activity, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionWAthsApp);
    }catch (Exception e) {
    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
}
    }
        });
        builder1.setNegativeButton("SMS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SendSMS.send(getApplicationContext(), Preferences.getString(getApplicationContext(), SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)),record, R.id.radioButtonAddSessionSMS);
Toast.makeText(getApplicationContext(), "?????????????????????? ???????????????????? ???? SMS, ??????????????: " + user.getSurname() + " " + user.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    builder.setCancelable(true);
        builder1.create().show();
    }
});
            builder.create().show();

        }
    }

    @Override
    public void onUserClick(Record record, int position) {
        if (record.getId() !=0 && getIntent().getStringExtra(StaticClass.KEY) == null) {
                getIntent().putExtra(StaticClass.KEY, StaticClass.CARDSESSION);
        }

        if (getIntent().getStringExtra(StaticClass.KEY) == null){
            getIntent().putExtra(StaticClass.KEY, StaticClass.NEWRECORD);
        }
        String key = getIntent().getStringExtra(StaticClass.KEY);
        try {
            consumerHashMap.get(key).accept(record);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (key.equals(StaticClass.DUPLICATION) && DoubleSession.checkDouble){
        DoubleSession.checkDouble = false;
        getIntent().removeExtra(StaticClass.KEY);
        setResult(RESULT_OK);
        finish();
    }
if (key.equals(StaticClass.TRANSFER)&& TransferSession.checkTransfer) {
    getIntent().removeExtra(StaticClass.KEY);
    TransferSession.checkTransfer = false;
    setResult(RESULT_OK);
finish();
}

if (getIntent().getStringExtra(StaticClass.KEY) == StaticClass.NEWRECORD || getIntent().getStringExtra(StaticClass.KEY) == StaticClass.CARDSESSION){
    getIntent().removeExtra(StaticClass.KEY);
}
    }
};
MyAdapter myAdapter = new MyAdapter(this, recordsEnpty, onUserClickListener, consumer);
        recyclerViewTime.setAdapter(myAdapter);

    }

     /*
    ?????????????????? ???????? ???? ?????????????? ????????
     */
     public void onClickButtonBackDay(View view) {
//dateAndTime.setTimeInMillis(dateAndTime.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));
    dateAndTime.add(Calendar.DATE, -1);
textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()) + " ???????????????? ?????? ?????????????????? ????????");
    recUpdate();
        view.announceForAccessibility(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
     }

/*
?????????????????? ???????? ???? ???????????????? ????????
 */
public void onClickButtonNextDay (View view) {
dateAndTime.setTimeInMillis(dateAndTime.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
    textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()) + " ???????????????? ?????? ?????????????????? ????????");
    recUpdate();
    view.announceForAccessibility(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
}

/*
?????????????????????????? ?? ?????????????? ???????????????????? ?????????????? ???? ????????????
 */
private  void setConsumerHashMap() {
//???????? ???????????? ????????????????
    consumerHashMap.put(StaticClass.NEWRECORD, new FreeSession(this));
//???????? ???????????? ?????? ????????????????????
    consumerHashMap.put(StaticClass.CARDSESSION, new BusyTime(this));
    // ????????????????????????
    consumerHashMap.put(StaticClass.DUPLICATION, new DoubleSession(getApplicationContext(), indexListRecord, calendarSetting, Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(this, Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false)));
// ??????????????
consumerHashMap.put(StaticClass.TRANSFER, new TransferSession(getApplicationContext(), getIntent().getLongExtra(CardSessionActivity.TRANSFER, 1), calendarSetting));
//?????????? ???????????? ???? ??????????????
    consumerHashMap.put(StaticClass.IISTORUNEWRECORD,new HistoryNewSession(this, getIntent().getIntExtra (StaticClass.POSITION_LIST_USERS, -0), activity));
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == User.CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                StaticClass.getDialog(this, "???? ???????????????????? ??????????????");
            }
        }
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        dateAndTime.setTimeInMillis(new Date().getTime());
        textViewDay.setText(DateFormat.getDateInstance(FULL).format(dateAndTime.getTime()));
    super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
focus = hasFocus;
}

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
Intent intent = new Intent(this, MainActivity.class);
startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}