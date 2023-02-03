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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.notificationSMS.SMSService;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

import static java.lang.Thread.sleep;

public class SMSNotificationReceiver extends BroadcastReceiver {

    private List<Record> recordsCopy;
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-YY");
private Bd bd;
private  List <Record> records = new ArrayList<>();
private  int count;

@Override
    public void onReceive(Context context, Intent intent) {
    Toast.makeText(context.getApplicationContext(), "Начало работы SMS рассылки", Toast.LENGTH_SHORT).show();
    if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
&& Preferences.getInt(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) != TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK
    ) {
    Toast.makeText(context.getApplicationContext(), "SMS уведомления включены", Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(context.getApplicationContext(), SMSNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
               context.getApplicationContext(), TemplatesActivity.RQS_TIME, intent1, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Preferences.getLong(context, Preferences.TIME_SMS_NOTIFICATION, 0),
                TimeUnit.HOURS.toMillis(24),
                pendingIntent);
    }else {
        bd = Bd.load(context);
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