package ru.burdin.clientbase.importAndExport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import ru.burdin.clientbase.notificationSMS.SMSHourNotificationReceiver;
import ru.burdin.clientbase.setting.Preferences;

public class AutoExportSchedule extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
                if (Preferences.getBoolean(context.getApplicationContext(), Preferences.SET_CHECK_BOX_EXPORT_schedule, false)) {
    TcpCloudSync tcpCloudSync = new TcpCloudSync(context.getApplicationContext(), TcpCloudSync.EXPORT);
    tcpCloudSync.execute();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 1);
    calendar.set(Calendar.SECOND,0);
    calendar.set(Calendar.MINUTE, 0);
    long timeLong = calendar.getTimeInMillis();
    Preferences.set(context, Preferences.TIME_NG_EXPORT_CLOUD, timeLong);
    Intent intentNewTime = new Intent(context.getApplicationContext(), AutoExportSchedule.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context.getApplicationContext(), CloudSyncActivity.ALARM_ID, intentNewTime, PendingIntent.FLAG_MUTABLE);
    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeLong,
            pendingIntent);
}
            }
}
