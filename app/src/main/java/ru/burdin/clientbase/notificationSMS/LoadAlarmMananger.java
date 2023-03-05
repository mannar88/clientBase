package ru.burdin.clientbase.notificationSMS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.Preference;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.importAndExport.AutoExportSchedule;
import ru.burdin.clientbase.importAndExport.CloudSyncActivity;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

public  class LoadAlarmMananger extends AsyncTask {
private Context context;
private  Bd bd;
private  boolean export = false;

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
        Intent intentNewTime = new Intent(context.getApplicationContext(), AutoExportSchedule.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, CloudSyncActivity.ALARM_ID, intentNewTime, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (Preferences.getBoolean(context.getApplicationContext(), Preferences.SET_CHECK_BOX_EXPORT_schedule, false)) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,Preferences.getLong(context.getApplicationContext(), Preferences.TIME_NG_EXPORT_CLOUD, Calendar.getInstance().getTimeInMillis()),
                    pendingIntent);
        export = true;
        }else {
            alarmManager.cancel(pendingIntent);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
//        if (export) {
//            DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY, HH:mm");
//            Toast.makeText(context.getApplicationContext(), dateFormat.format(Preferences.getLong(context.getApplicationContext(), Preferences.TIME_NG_EXPORT_CLOUD, Calendar.getInstance().getTimeInMillis())), Toast.LENGTH_SHORT).show();
//        }
}
}
