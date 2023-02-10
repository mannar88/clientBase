package ru.burdin.clientbase.setting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.lits.ListSessionActivity;
import ru.burdin.clientbase.setting.interfase.InterFaseSettingActivity;

public class SettingActivity extends AppCompatActivity {

    private Spinner spinnerGetCalendar;
    private CheckBox checkBoxCalender;
   private ArrayAdapter <?> arrayAdapter;
    private CalendarSetting calendars;
    public static final int REQUEST_PERMISSIONS = 101;
private RadioGroup radioGroup;
private  CheckBox checkBoxSettingIntersectionRecod;
    private Bd bd;
private  WorkScheduleSetting workScheduleSetting;
private  static List<String> nameCalendars;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        setTitle("Настройки");
    try {
        bd = Bd.load(this);
    } catch (InterruptedException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (ExecutionException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (TimeoutException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
    checkBoxCalender = findViewById(R.id.checkBoxSettingCalender);
        spinnerGetCalendar = findViewById(R.id.spinerSettingCalendar);
checkBoxSettingIntersectionRecod = findViewById(R.id.checkBoxSettingIntersectionRecods);
        workScheduleSetting = new WorkScheduleSetting(this);
        calendars = CalendarSetting.load(this);
 nameCalendars = new ArrayList<>(calendars.getNameCalendar());
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nameCalendars);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerGetCalendar.setAdapter(arrayAdapter);

}

    @Override
    protected void onStart() {
        super.onStart();
workScheduleSetting.setOnItemSelectedListener();
spinnerGetCalendar.setSelection(calendars.indexSave(nameCalendars));
        checkBoxCalender.setChecked(calendars.getCheckBox());
        spinnerGetCalendar.setEnabled(calendars.getCheckBox());
        calendars.listenChexBox(checkBoxCalender, spinnerGetCalendar, this);
checkBoxSettingIntersectionRecod.setChecked(Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, false));
}

    @Override
    protected void onResume() {
        super.onResume();
calendars.listenCSpinner(spinnerGetCalendar, nameCalendars);

}

    @Override
    public void onBackPressed() {
Preferences.set(getApplicationContext(), Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, checkBoxSettingIntersectionRecod.isChecked());
        super.onBackPressed();
    }

    /*
        Ответна разрешение
         */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode) {
            case CalendarSetting.Calender_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
checkBoxCalender.setChecked(false);
                } else {
                    spinnerGetCalendar.setEnabled(false);
                    StaticClass.getDialog(this, "чтение и запись календаря");
                }
                break;
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
//                    StaticClass.getDialog(this, "чтение и запись файловой системы");
                String string = "";
                    for (int i = 0; i < permissions.length; i++) {
                        string = string + " " + permissions[i] + " " + grantResults[i] + " ";
                    }
                checkBoxCalender.setText(string);

                }
    break;
                default:
                            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static List<String> getNameCalendars() {
        return nameCalendars;
    }


    public void onClickButtonSettingTemplets(View view) {
    Intent intent = new Intent(this, TemplatesActivity.class);
    startActivity(intent);
    }

    /*
    Открывает окно настрой интерфейса
     */
    public void onClickButtonSettingInterfase(View view) {
    Intent intent = new Intent(this, InterFaseSettingActivity.class);
    startActivity(intent);

    }


}