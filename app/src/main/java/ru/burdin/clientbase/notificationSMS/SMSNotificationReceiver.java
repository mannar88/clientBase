package ru.burdin.clientbase.notificationSMS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.importAndExport.AutoExportSchedule;
import ru.burdin.clientbase.importAndExport.CloudSyncActivity;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

import static java.lang.Thread.sleep;

public class SMSNotificationReceiver extends BroadcastReceiver {

    private List<Record> recordsCopy;
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-YY");
    private DateFormat dateFormatStartSMS = new SimpleDateFormat("HH:mm");
    private Bd bd;
private  List <Record> records = new ArrayList<>();

@Override
    public void onReceive(Context context, Intent intent) {
    if (Preferences.getInt(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) != TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK
    ) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context.getApplicationContext(), "SMS уведомления включены", Toast.LENGTH_SHORT).show();
if (Preferences.getInt(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK)== TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR) {
        bd = Bd.load(context.getApplicationContext());
    SendSMS.multiStartSMSAlarms(context.getApplicationContext(), bd.getRecords());
    }else {
    SendSMS.startAlarm(context.getApplicationContext(), Preferences.getString(context.getApplicationContext(), Preferences.TIME_SMS_NOTIFICATION, "00:"));
    }
            Intent intentNewTime = new Intent(context.getApplicationContext(), AutoExportSchedule.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, CloudSyncActivity.ALARM_ID, intentNewTime, PendingIntent.FLAG_MUTABLE);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            if (Preferences.getBoolean(context.getApplicationContext(), Preferences.SET_CHECK_BOX_EXPORT_schedule, false)) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,Preferences.getLong(context.getApplicationContext(), Preferences.TIME_NG_EXPORT_CLOUD, Calendar.getInstance().getTimeInMillis()),
                        pendingIntent);

            }else {
                alarmManager.cancel(pendingIntent);
            }

        } else {
Toast.makeText(context.getApplicationContext(), "Начало отправки SMS уведомлений", Toast.LENGTH_SHORT).show();
            SendSMS.startAlarm(context.getApplicationContext(), Preferences.getString(context.getApplicationContext(), Preferences.TIME_SMS_NOTIFICATION, "00:"));

                    bd = Bd.load(context.getApplicationContext());
                Toast.makeText(context.getApplicationContext(), "База загружена", Toast.LENGTH_SHORT).show();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        recordsCopy = List.copyOf(bd.getRecords());
                        getRecord(context.getApplicationContext());
                        try {
    sendSMS(context.getApplicationContext());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            Toast.makeText(context.getApplicationContext(), "Отправка SMS", Toast.LENGTH_SHORT).show();
            }
        }
}

/*
Отправка SMS
 */
    private  void  sendSMS (Context context) throws InterruptedException {
    if (records.size() > 0){
        for (Record record:records) {
            SendSMS.send(context, Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionSMS);
        sleep(TimeUnit.MINUTES.toMillis(Preferences.getInt(context, Preferences.PAUSE_SMS, 0)));
        }
        records.clear();
    }
    }

    /*
        Набор сеансов для уведомления
         */
        private   void  getRecord(Context context){
                Calendar calendar = Calendar.getInstance();
                if (Preferences.getInt(context, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) == TemplatesActivity.RADIO_BUTTON_NOTIFICATION_NEXT_DAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                for (Record record :recordsCopy) {
                    if (dateFormat.format(calendar.getTime()).equals(dateFormat.format(record.getStartDay())) && record.getNotNotification() == 0) {
records.add(record);
                    }
            }
        }

}