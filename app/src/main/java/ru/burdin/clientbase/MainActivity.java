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

import androidx.appcompat.app.AppCompatActivity;

import ru.burdin.clientbase.importAndExport.ImportExportActivity;
import ru.burdin.clientbase.lits.ListExpensesActivity;
import ru.burdin.clientbase.lits.ListOfProceduresActivity;
import ru.burdin.clientbase.lits.litsSession.ListSessionActivity;
import ru.burdin.clientbase.lits.listClient.ListClientActivity;
import ru.burdin.clientbase.notificationSMS.LoadAlarmMananger;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {

    private Bd bd;
private CalendarSetting calendarSetting;
private  Activity activity;
private  AlarmManager alarmManager;


@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bd = Bd.load(this);
    if (savedInstanceState == null) {
        LoadAlarmMananger loadAlarmMananger = new LoadAlarmMananger(this);
        loadAlarmMananger.execute();
     }

}

    @Override
    protected void onRestart () {
        super.onRestart();
}

    @Override
    protected void onResume() {
        super.onResume();


       calendarSetting  = CalendarSetting.load(this);

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

    @Override
    protected void onStop() {
    super.onStop();
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