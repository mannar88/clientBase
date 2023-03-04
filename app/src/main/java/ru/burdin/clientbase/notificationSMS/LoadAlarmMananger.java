package ru.burdin.clientbase.notificationSMS;

import android.content.Context;
import android.os.AsyncTask;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

public  class LoadAlarmMananger extends AsyncTask {
private Context context;
private  Bd bd;
public  LoadAlarmMananger(Context context) {
    this.context = context;
bd = Bd.load(context);
}

    @Override
    protected Object doInBackground(Object[] objects) {
        switch (Preferences.getInt(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK)){
            case TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK:
                break;
            case TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR:
                SendSMS.multiStartSMSAlarms(context.getApplicationContext(), bd.getRecords());
                break;
            default:
                SendSMS.startAlarm(context.getApplicationContext(), Preferences.getString(context, Preferences.TIME_SMS_NOTIFICATION, "00:00"));
        }

        return null;
    }
}
