package ru.burdin.clientbase.cards.cardSession;

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
import android.widget.Button;
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
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import ru.burdin.clientbase.analytics.Analytics;
import ru.burdin.clientbase.cards.cardClient.CardClientActivity;
import ru.burdin.clientbase.lits.actionListSassion.DoubleSession;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.lits.litsSession.ListSessionActivity;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.Preferences;

public class CardSessionActivity extends AppCompatActivity {

     Record record;
    private Bd bd;
    protected TextView textViewDate;
    protected   TextView textViewNameUser;
    protected      TextView textViewProcedure;
    protected   TextView textViewPrice;
   CheckBox checkBoxPay;
protected   TextView textViewTimeEnd;
    protected   TextView textViewComment;
protected   TextView textViewPlaceOnTheList;
protected   CheckBox checkBoxPlaceOnTheList;
protected CheckBox checkBoxNotNotification;
protected  TextView textViewInfoPay;
Button buttonDelete;
private  long recordId = -1;
 CalendarSetting calendarSetting;
public  static  final  String TRANSFER = "transfer";
public  static  final  int TRANSFER_INT = 67;
Activity context;
private  CardSession cardSession;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_card_session);
    setTitle("");
        bd = Bd.load(getApplicationContext());
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
checkBoxPay = findViewById(R.id.checkBoxCardSessionPay);
checkBoxPay.setEnabled((record.getEnd() + record.getStart()) <= new Date().getTime()? true:false);
textViewTimeEnd = findViewById(R.id.textViewCardSessionTimeEnd);
    textViewComment = findViewById(R.id.textViewCardSessionComment);
textViewPlaceOnTheList = findViewById(R.id.textvIewCardSessionPlaceOnTheList);
checkBoxPlaceOnTheList = findViewById(R.id.checkBoxCardSessionPlaceOnTheList);
checkBoxNotNotification = findViewById(R.id.checkBoxCardSessionNotNotification);
buttonDelete = findViewById(R.id.buttonCardSessionDelete);
this.context = this;
cardSession = new CardSession(this, bd);
cardSession.visibilityPay();
}



    @Override
    protected void onStart() {
        super.onStart();
cardSession.setScreenInfo(record);}

    @Override
    protected void onResume() {
        super.onResume();
        checkBoxPlaceOnTheList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ContentValues contentValues = new ContentValues();
    contentValues.put(Bd.COLUMN_ONE_IN_LINE, StaticClass.booleaILong(b));
        int [] arr = Analytics.placeOnTheList(bd.getRecords(), bd.getUsers().get(cardSession.getIndexUser(record)).getId(), record.getId());
    if (arr[2] > 1&&bd.update(bd.TABLE_SESSION, contentValues, record.getId(), Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(context.getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false)) == 1){
record.setOneLine(StaticClass.booleaInInt(b));
        arr = Analytics.placeOnTheList(bd.getRecords(),bd.getUsers().get(cardSession.getIndexUser(record)).getId(), record.getId());
textViewPlaceOnTheList.setText("?????????? ?? ??????????: " + arr[0] + ", ????????: " + arr[1] + ", ??????????: " + arr[2] + " ???? " + arr[3]);
        }
    }
});
        checkBoxNotNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ContentValues contentValues = new ContentValues();
    long res = StaticClass.booleaILong(b);
    contentValues.put(Bd.COLUMN_not_notification, res);
    if (bd.update(Bd.TABLE_SESSION, contentValues, record.getId(), Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(context.getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false)) == 1) {
record.setNotNotification(res);
            }
    }
});
cardSession.setScreenInfo(record);
cardSession.groupDelete();
cardSession.pay(record);
}


    /*
    ?????????????????? ???????????????? ??????????????
     */
    public void onClickTextViewCardSessionNameUser(View view) {
        Intent intent = new Intent(this, CardClientActivity.class);
        intent.putExtra(Bd.TABLE, cardSession.getIndexUser(record));
        startActivity(intent);
     }


    /*
    ???????????????????????? ????????????
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
builder.setPositiveButton("?????????????????????? ????????????????????", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(getApplicationContext(), ListSessionActivity.class);
        intent.putExtra(StaticClass.KEY, StaticClass.DUPLICATION);
        intent.putExtra(StaticClass.POSITION_LIST_RECORDS, StaticClass.indexList(record.getId(), bd.getRecords()));
        intent.putExtra(ListSessionActivity.CALENDAR, record.getStart());
        startActivityForResult(intent,ListSessionActivity.CLASS_INDEX);
    }
});
    builder.setNegativeButton("?????????????????????? ?????????????? ?????? ?????????????????? ??????????????", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
doubPacket(times, longs);
        }
    });
    builder.create().show();
    }

    /*
    ???????????????? ?????????? ?????? ????????????????????????
     */
    private  void  doubPacket (String[] times, long [] longs) {
Set<Long> longHashSet = new TreeSet<>();
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
        builder.setPositiveButton("??????????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (longHashSet.size() > 0) {
                    for (Long l : longHashSet) {
    Consumer<Record> doubleSession = new DoubleSession(getApplicationContext(), StaticClass.indexList(recordId, bd.getRecords()), calendarSetting,false, false);
doubleSession.accept(new Record(l));
}
    Toast.makeText(getApplicationContext(), "???????????? ??????????????????????", Toast.LENGTH_SHORT).show();
                bd.autoExport(1l, Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
                }else {
                    Toast.makeText(getApplicationContext(), "?????????????? ???? ??????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });

    builder.create().show();;}


    /*
???????????????????????????? ????????????
 */
public void onclickButtonCardSessionRead(View view) {
Intent intent = new Intent(this, AddSessionActivity.class);
intent.putExtra(StaticClass.POSITION_LIST_RECORDS, StaticClass.indexList(record.getId(), bd.getRecords()));
startActivityForResult(intent, AddSessionActivity.CLASS_INDEX);
}

    /*
????????????  ?????? ???????????????? ????????????
 */
    public void onClickButtonCardSessionDelete(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("?????????????? ???????????? ???? ?????????????????????");
            builder.setPositiveButton("?????????????? ?????? ??????????????????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
 if (delete()) {
     finish();
 }
                }
            });
            builder.setNegativeButton("?????????????? ?? ?????????????????? ?????????????? ???? SMS", new DialogInterface.OnClickListener() {
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
            builder.setNeutralButton("?????????????? ?? ?????????????????? ???? WHatsApp", new DialogInterface.OnClickListener() {
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
    ?????????? ???????????????? ????????????
     */
    private  boolean   delete () {
        int resultDelete = bd.delete(Bd.TABLE_SESSION, record.getId(), Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(this, Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
        if (resultDelete == 1) {
            long id = bd.getRecords().remove(StaticClass.indexList(record.getId(), bd.getRecords())).getEvent_id();
            if (calendarSetting.delete(id) == 0) {
                Toast.makeText(getApplicationContext(), "???? ?????????????? ?????????????? ???????????? ?? ??????????????????", Toast.LENGTH_SHORT).show();
            }
            SendSMS.deleteHourAlarm(getApplicationContext(), record);
            Toast.makeText(getApplicationContext(), "???????????? ??????????????", Toast.LENGTH_SHORT).show();
        }
        return  resultDelete == 0? false:true;
    }

    /*
  ?????????? ???????????????????? ???? ?????????? ???????????????????? ?????????????????? ?? ?????????? ???????????? ????????????
  **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case  ListSessionActivity.CLASS_INDEX:
                Toast.makeText(this, "???????????? ??????????????????????", Toast.LENGTH_LONG).show();
            break;
                case AddSessionActivity.CLASS_INDEX:
                cardSession.setScreenInfo(record);
                    Toast.makeText(getApplicationContext(), "???????????? ????????????????", Toast.LENGTH_SHORT).show();
            break;
                case TRANSFER_INT:
cardSession.setScreenInfo (record);
Toast.makeText(this, "???????????? ?????????????? ????????????????????", Toast.LENGTH_SHORT).show();
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("?????????????????? ?????????????? ?? ?????????????????");
builder.setPositiveButton("??????, ?????????? ?????????? ??????????????????", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
   dialogInterface.cancel();
    }
});
builder.setNegativeButton("?????????????????? ???? SMS", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (SendSMS.permission(getApplicationContext())){
        SendSMS.send(getApplicationContext(), Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(2), SendSMS.TEMPLETS.get(2)), record, R.id.radioButtonAddSessionSMS);
    }else {
            requestPermissions(new  String[]{Manifest.permission.SEND_SMS}, SendSMS.PERMISSION_SMS);
        }
    }
});
builder.setNeutralButton("?????????????????? ???? WhatsApp", new DialogInterface.OnClickListener() {
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
    ??????????????
     */
    public void onClickButtonCardSessionTransfer(View view) {
    Intent intent = new Intent(this, ListSessionActivity.class);
        intent.putExtra(TRANSFER,record.getId());
        intent.putExtra(StaticClass.KEY, StaticClass.TRANSFER);
        intent.putExtra(StaticClass.POSITION_LIST_RECORDS, StaticClass.indexList(record.getId(), bd.getRecords()));
        intent.putExtra(ListSessionActivity.CALENDAR, record.getStart());
        startActivityForResult(intent,TRANSFER_INT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
if (requestCode == SendSMS.PERMISSION_SMS) {
    if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

    }else {
        StaticClass.getDialog(context, "???? ???????????????? SMS");
    }
}
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
finish();
//        super.onBackPressed();
    }
/*
?????????????????? ???????????? ?? ??????????????
 */
    public void onClickTextViewtextViewCardSessionPay(View view) {
    cardSession.pay(record);
    }


}