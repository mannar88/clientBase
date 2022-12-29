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

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

    public class SMSService extends Service {

        private int check;
        private DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        private List<Record> records = new ArrayList<>();
        private boolean checkBioolean = true;
        private Bd bd;

        public SMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }

        /*
        собирает список сеансов
         */
        private void setListSession(Context context) {
            records.clear();

            Calendar calendar = Calendar.getInstance();
            if (check != TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR) {
                if (dateFormat2.format(new Date()).equalsIgnoreCase(Preferences.getString(context.getApplicationContext(), Preferences.APP_PREFERENSES_TIME_NOTIFICATION_SMS, ""))) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-YY");
                    if (check == TemplatesActivity.RADIO_BUTTON_NOTIFICATION_NEXT_DAY) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    for (Record record : bd.getRecords()) {
                        if (dateFormat.format(record.getStartDay()).equals(dateFormat.format(calendar.getTime()))) {
                            records.add(record);
                        }
                    }
                }
            } else {
                long time = Preferences.getLong(context, Preferences.APP_PREFERENCAES_TEMLETES_NOTIFICATION_HOUR, 0);
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-YYYY");
                for (Record record : bd.getRecords()) {
                    if (dateFormat.format(record.getStart() - time).equals(dateFormat.format(new Date()))) {
                        records.add(record);
                    }
                }

            }
        }


        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        registerReceiver(timeReceiver, new IntentFilter(                "android.intent.action.TIME_TICK"));
        if (Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_START_SERVICE, false)) {
            Toast.makeText(this, "Сервис по отправки SMS запущен", Toast.LENGTH_SHORT).show();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    while (check != TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) {
                        Context context = getApplicationContext();
                        check = Preferences.getInt(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK);
                        bd = Bd.load(getApplicationContext());
                        setListSession(getApplication().getApplicationContext());
                        if (records.size() > 0 && checkBioolean) {
                            records.forEach(
                                    record -> SendSMS.send(getApplicationContext(), Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionSMS)

                            );

                            checkBioolean = false;
                        }
                        if (records.size() == 0) {
                            checkBioolean = true;
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
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