package ru.burdin.clientbase.setting.interfase;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;

import ru.burdin.clientbase.setting.Preferences;

public class InterfaseSetting {

    private Context context;
    public  InterfaseSetting (Context context) {
        this.context = context;
    }

    /*
    Слушатель флажка автостарта Расписание и запись
     */
    public  void  saveStartMainActivty(CheckBox checkBox) {
checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Preferences.set(context.getApplicationContext(), Preferences.START_ACTIVITY_LIST_SESSION, b);
    }
});
        }
}
