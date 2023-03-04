package ru.burdin.clientbase.notificationSMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.Preferences;

public class SMSHourNotificationReceiver extends BroadcastReceiver {

    private Bd bd;
    @Override
    public void onReceive(Context context, Intent intent) {

            bd = Bd.load(context.getApplicationContext());
        long id  = intent.getLongExtra("id", -1);
    if (id != -1) {
        int index = StaticClass.indexList(id, bd.getRecords());
        if (index > -1) {
            Record record = bd.getRecords().get(index);
        if (record.getNotNotification() == 0l) {
            SendSMS.send(context, Preferences.getString(context, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionSMS);
String userName = bd.getUsers().get(StaticClass.indexList(record.getIdUser(), bd.getUsers())).getSurname() + " " + bd.getUsers().get(StaticClass.indexList(record.getIdUser(), bd.getUsers())).getName();
Toast.makeText(context.getApplicationContext(), "Отправлено напоминание клиенту: " + userName, Toast.LENGTH_SHORT).show();
        }
        }

    }
    }
}