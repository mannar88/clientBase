package ru.burdin.clientbase;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.burdin.clientbase.importAndExport.ImportExportActivity;
import ru.burdin.clientbase.lits.ListClientActivity;
import ru.burdin.clientbase.lits.ListExpensesActivity;
import ru.burdin.clientbase.lits.ListOfProceduresActivity;
import ru.burdin.clientbase.lits.ListSessionActivity;
import ru.burdin.clientbase.notificationSMS.SMSService;
import ru.burdin.clientbase.notificationSMS.StartServiceReceiver;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.SettingActivity;
import ru.burdin.clientbase.setting.TemplatesActivity;

public class MainActivity extends AppCompatActivity {

    private Bd bd;
private CalendarSetting calendarSetting;
private  Activity activity;
private StartServiceReceiver startServiceReceiver = new StartServiceReceiver();


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

}

    @Override
    protected void onResume() {
        super.onResume();
            bd = Bd.load(this);
            calendarSetting = CalendarSetting.load(this);
                    this.registerReceiver(startServiceReceiver, new IntentFilter(
                "android.intent.action.TIME_TICK"));
if (Preferences.getInt(this, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) >TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK  && StaticClass.searchSMSServese(this)
&& Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_START_SERVICE, false)
) {
    startService(new Intent(this, SMSService.class));

}
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