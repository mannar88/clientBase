package ru.burdin.clientbase.notificationSMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.notificationSMS.SMSService;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Preferences.getInt(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) >TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK  && StaticClass.searchSMSServese(context.getApplicationContext())
                && Preferences.getBoolean(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_START_SERVICE, false)
        ) {

            context.startService(new Intent(context, SMSService.class));

            Toast.makeText(context.getApplicationContext(), "Клиентская базаа запускает сервис по отправки SMS", Toast.LENGTH_SHORT).show();
        }

        }
}