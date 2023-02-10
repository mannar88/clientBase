package ru.burdin.clientbase.setting.interfase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;

import ru.burdin.clientbase.R;
import ru.burdin.clientbase.setting.Preferences;

public class InterFaseSettingActivity extends AppCompatActivity {

    CheckBox checkBoxStartListActivity;
    InterfaseSetting interfaseSetting ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_fase_setting);
    interfaseSetting = new InterfaseSetting(this);
    checkBoxStartListActivity = findViewById(R.id.checkBoxInterfaseSettingMain);
    checkBoxStartListActivity.setChecked(Preferences.getBoolean(this,Preferences.START_ACTIVITY_LIST_SESSION, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
    interfaseSetting.saveStartMainActivty(checkBoxStartListActivity);
    }
}