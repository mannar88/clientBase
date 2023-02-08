package ru.burdin.clientbase;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.importAndExport.ImportExportActivity;
import ru.burdin.clientbase.lits.listClient.ListClientActivity;
import ru.burdin.clientbase.lits.ListExpensesActivity;
import ru.burdin.clientbase.lits.ListOfProceduresActivity;
import ru.burdin.clientbase.lits.ListSessionActivity;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.SettingActivity;
import ru.burdin.clientbase.setting.TemplatesActivity;

public class MainActivity extends AppCompatActivity {

    private Bd bd;
private CalendarSetting calendarSetting;
private  Activity activity;
private  AlarmManager alarmManager;


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    try {
        bd = Bd.load(this);
    } catch (InterruptedException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (ExecutionException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (TimeoutException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    if (savedInstanceState == null) {
            switch (Preferences.getInt(this, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK)){
    case TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK:
    break;
    case TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR:
        SendSMS.multiStartSMSAlarms(this, bd.getRecords());
        break;
        default:
        SendSMS.startAlarm(this, Preferences.getString(this, Preferences.TIME_SMS_NOTIFICATION, "00:00"));
}
}

}

    @Override
    protected void onStart() {
        super.onStart();

}

    @Override
    protected void onResume() {
        super.onResume();
        calendarSetting = CalendarSetting.load(this);

            //Установка флажка автоматического экспорта
Preferences.set(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, (permission() && Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false)));
}


/*
Проверка на разрешение файловой системы
 */
private  boolean permission () {
    boolean result = false;
    if (Build.VERSION.SDK_INT < 30) {
        int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            result = true;
        }
    }else {
if (Environment.isExternalStorageManager()){
    result = true;
}
    }
    return  result;
}

public void onClickButtonRecord(View view) {
Intent intent = new Intent(this, ListSessionActivity.class);
startActivity(intent);

}

    public void buttonList(View view) {
        Intent intent = new Intent(this, ListClientActivity.class);
        startActivity(intent);
    }

    public void buttonListP(View view) {
    Intent intent = new Intent(this, ListOfProceduresActivity.class);
    startActivity(intent);
    }

    public void onClickButtonListExpenses(View view) {
    Intent intent = new Intent(this, ListExpensesActivity.class);
    startActivity(intent);
    }

    public void onClickButtonSetting(View view) {
    Intent intent = new Intent(this, SettingActivity.class);
    startActivity(intent);
    }
    /*
    открывает активность статистики
    */
    public void onClickButtonStastick(View view) {
    Intent intent = new Intent(this, StatActivity.class);
    startActivity(intent);
    }
/*
Открывает активность импорта и экспорта
 */
    public void onClickButtonImportExport(View view) {
    Intent intent = new Intent(this, ImportExportActivity.class);
    startActivity(intent);
    }
/*
Инфо
 */
    public void onClickButtonInfo(View view) {

            Intent intent = new Intent(this, InfoActivity.class);
    startActivity(intent);
    }
}