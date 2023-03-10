package ru.burdin.clientbase.lits.actionListSassion;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

public class TransferSession implements Consumer<Record> {

    private Context context;
    private long id;
    private CalendarSetting calendarSetting;
    public static boolean checkTransfer = false;
    private Bd bd;
int index;

public TransferSession(Context context, long id, CalendarSetting calendarSetting) {
        this.context = context;
        this.id = id;
        this.calendarSetting = calendarSetting;
        bd = Bd.load(context);
}


    @Override
    public void accept(Record record) {
 index = StaticClass.indexList(id, bd.getRecords());
Record record1 = bd.getRecords().get(index);
if (record.getId() ==  0) {
record.setEnd(record1.getEnd());
if (!bd.getRecords().contains(record)||record.equals(record1) || Preferences.getBoolean(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, false)) {
ContentValues contentValues = new ContentValues();
contentValues.put(Bd.COLUMN_TIME, record.getStart());
contentValues.put(Bd.COLUMN_TIME_END, record.getEnd());
if (bd.update(Bd.TABLE_SESSION, contentValues, id, Preferences.getBoolean(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(context.getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false)) == 1) {
    record1.setEnd(record.getEnd());
    record1.setStart(record.getStart());
    int indUser = StaticClass.indexList(record1.getIdUser(), bd.getUsers());
    String sur =  bd.getUsers().get(indUser).getSurname() + " " + bd.getUsers().get(indUser).getName();
    if (Preferences.getInt(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) == TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR) {
        SendSMS.startHourAlarmMenedjer(context.getApplicationContext(), record1);
    }
        checkTransfer = true;
 Thread thread = new Thread(() -> calendarSetting.update(record1, sur));
 thread.start();
}else {
    Toast.makeText(context.getApplicationContext(), "???? ?????????????? ??????????????????", Toast.LENGTH_SHORT).show();
}
}else {
    Toast.makeText(context.getApplicationContext(), "?????????? ???????????????????????? ?? ???????????? ??????????????", Toast.LENGTH_SHORT).show();
}
}else {
    Toast.makeText(context.getApplicationContext(), "???????????? ?????? ????????????", Toast.LENGTH_SHORT).show();
}
}

}