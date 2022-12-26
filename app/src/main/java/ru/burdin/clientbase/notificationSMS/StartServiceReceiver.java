package ru.burdin.clientbase.notificationSMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.notificationSMS.SMSService;
import ru.burdin.clientbase.setting.Preferences;

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Preferences.getInt(context, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, R.id.radioButtonTempleetsNotificationNotCheck) == R.id.radioButtonTempleetsNotificationNotCheck && !StaticClass.searchSMSServese(context)) {
           context.startService(new Intent(context, SMSService.class));

            Toast.makeText(context.getApplicationContext(), "Клиентская базаа запускает сервис по отправки SMS", Toast.LENGTH_SHORT).show();
        }

        }
}