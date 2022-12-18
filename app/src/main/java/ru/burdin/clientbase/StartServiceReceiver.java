package ru.burdin.clientbase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ru.burdin.clientbase.setting.Preferences;

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "Клиентская базаа запускает сервис по отправки SMS", Toast.LENGTH_SHORT).show();
    if (Preferences.getBoolean(context, Preferences.APP_PREFERENSES_CHECK_AUTO_START_SERVICE, false)) {
            context.startService(new Intent(context, SMSService.class));
        }
        }
}