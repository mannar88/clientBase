package ru.burdin.clientbase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.logging.Logger;

import ru.burdin.clientbase.importAndExport.ImportExportActivity;
import ru.burdin.clientbase.lits.ListClientActivity;
import ru.burdin.clientbase.lits.ListExpensesActivity;
import ru.burdin.clientbase.lits.ListOfProceduresActivity;
import ru.burdin.clientbase.lits.ListSessionActivity;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {

    private Bd bd;
private CalendarSetting calendarSetting;
private  Activity activity;

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
if (Preferences.getInt(this, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, R.id.radioButtonTempleetsNotificationNotCheck) != R.id.radioButtonTempleetsNotificationNotCheck &&StaticClass.searchSMSServese(this)) {
    startService(new Intent(this, SMSService.class));
}
if (Preferences.getInt(this, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, R.id.radioButtonTempleetsNotificationNotCheck) == R.id.radioButtonTempleetsNotificationNotCheck && !StaticClass.searchSMSServese(this)) {
    startService(new Intent(this, SMSService.class));
}
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