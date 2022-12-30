    package ru.burdin.clientbase.notificationSMS;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
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
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

    public class SMSService extends Service {

        private int check;
        private DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        private List<Record> records = new ArrayList<>();
        private boolean checkBioolean = true;
        private Bd bd;
private  DateFormat dateFormat = new SimpleDateFormat("dd-MM-YY");
private  Context context;

public SMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }


        /*
        Собирает список клиентов следющего дня
         */
        private  void  getRecordNextDay(){
            if (dateFormat2.format(new Date()).equals(Preferences.getString(getApplicationContext(), Preferences.APP_PREFERENSES_TIME_NOTIFICATION_SMS, ""))) {
                Calendar calendar = Calendar.getInstance();
                if (Preferences.getInt(context, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) == TemplatesActivity.RADIO_BUTTON_NOTIFICATION_NEXT_DAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                for (Record record : bd.getRecords()) {
                    if (dateFormat.format(calendar.getTime()).equals(dateFormat.format(record.getStartDay()))) {
                        records.add(record);
                    }
                }
            }
            }
/*
Набор записей от конкретного времени
 */
            private  void  getNotificationHour() {
                long time = Preferences.getLong(context, Preferences.APP_PREFERENCAES_TEMLETES_NOTIFICATION_HOUR, 0);
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-YYYY");
                for (Record record : bd.getRecords()) {
                    if (dateFormat.format(record.getStart() - time).equals(dateFormat.format(new Date()))) {
                        records.add(record);
                    }
                }
            }

            /*
Безконечный цикл сервиса
 */
            private  void  getStart() throws InterruptedException {
                while (Preferences.getInt(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) != 0) {
                    records.clear();
                    switch (Preferences.getInt(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK)) {
                        case TemplatesActivity.RADIO_BUTTON_NOTIFICATION_NEXT_DAY:
                            getRecordNextDay();
                            break;
                        case  TemplatesActivity.RADIO_BUTTON_NOTIFICATION_TODAY:
                    getRecordNextDay();
                            break;
                        case TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR:
                    getNotificationHour();
                            break;
                    }
                    if (records.size() > 0) {
                                                    records.forEach(
                                    record -> SendSMS.send(context, Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionSMS)
                            );
                        Thread.sleep(1000 * 60);
                    }
                }

            }

            @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
bd = Bd.load(getApplicationContext());
        if (Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_START_SERVICE, false)) {
            Toast.makeText(this, "Сервис по отправки SMS запущен", Toast.LENGTH_SHORT).show();
context = getApplicationContext();
            Thread thread = new Thread(() -> {
                try {
                    getStart();
                } catch (InterruptedException e) {
                    Toast.makeText(this,e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
thread.start();
        }
        return Service.START_STICKY;
    }

        @Override
        public void onDestroy() {

        Toast.makeText(this, "Сервис по отправки SMS выключен", Toast.LENGTH_SHORT).show();
            super.onDestroy();
        }
    }