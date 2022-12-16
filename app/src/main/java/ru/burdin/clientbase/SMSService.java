    package ru.burdin.clientbase;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

    public class SMSService extends Service {

    private  TimeReceiver timeReceiver = TimeReceiver.load();
    public SMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(timeReceiver, new IntentFilter(                "android.intent.action.TIME_TICK"));
        Toast.makeText(this, "Сервис по отправки SMS запущен", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

        @Override
        public void onDestroy() {
            unregisterReceiver(timeReceiver);
        Toast.makeText(this, "Сервис по отправки SMS выключен", Toast.LENGTH_SHORT).show();
            super.onDestroy();
        }
    }