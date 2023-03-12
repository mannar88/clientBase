package ru.burdin.clientbase.importAndExport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.notificationSMS.LoadAlarmMananger;
import ru.burdin.clientbase.setting.Preferences;

public class CloudSyncActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
private List<String> stringList = new ArrayList<>();
private  TcpInfoUser tcpInfoUser;
private  String[] login;
private  Bd bd;
private Button buttonImport;
private  Button buttonExport;
private CheckBox checkBoxAutoExport;
private  CheckBox checkBoxExportSchedule;
private Spinner spinnerExportTime;
private  ArrayAdapter arrayAdapterSpinnerTime;
private Map <String, Long> mapTimeExport = new TreeMap<>();
public  final  static  int ALARM_ID = 40;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_sync);
    setTitle("Облачная синхронизация");
        bd = Bd.load(this);
    listView = findViewById(R.id.listViewCloudSync);
    buttonImport = findViewById(R.id.buttonCloudSyncImport);
    buttonExport = findViewById(R.id.buttonCloudSyncExport);
    buttonExport.setEnabled(false);
    buttonImport.setEnabled(false);
    checkBoxAutoExport = findViewById(R.id.checkBoxCloudSyncAutoExport);
    checkBoxAutoExport.setChecked(Preferences.getBoolean(this, Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
    checkBoxExportSchedule = findViewById(R.id.checkBoxCloudSyncExportSchedule);
    checkBoxExportSchedule.setChecked(Preferences.getBoolean(this, Preferences.SET_CHECK_BOX_EXPORT_schedule, false));
spinnerExportTime = findViewById(R.id.spinnerCloudSyncExportTime);
mapPut();
arrayAdapterSpinnerTime= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,mapTimeExport.keySet().toArray(new String[mapTimeExport.size()]));
    arrayAdapterSpinnerTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinnerExportTime.setAdapter(arrayAdapterSpinnerTime);
spinnerExportTime.setEnabled(checkBoxExportSchedule.isChecked());
spinnerExportTime.setSelection(new  ArrayList<String >(mapTimeExport.keySet()).indexOf(Preferences.getString(this, Preferences.TIME_EXPORT_CLOUD, "00:00")));
stringListAdd();
        arrayAdapter = new ArrayAdapter <>(this,
                android.R.layout.simple_list_item_1, stringList);
listView.setAdapter(arrayAdapter);
login = Preferences.getString(this, Preferences.LOGIN_PASSWORD, "false").split("--");
tcpInfoUser = new TcpInfoUser();
    tcpInfoUser.execute("dateSync=" + login[0] + "--" + login[1]);
}

/*
Собирает в мапу время
 */
private  void  mapPut(){
DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    for (int i = 0; i < 24; i++) {
        calendar.add(Calendar.HOUR, 1);
        mapTimeExport.put(dateFormat.format(calendar.getTime()), calendar.getTimeInMillis());
    }

}

    /*
    Заполняет список
     */
private  void  stringListAdd(){
     login = Preferences.getString(this, Preferences.LOGIN_PASSWORD, "false").split("--");
    stringList.add("Авторизованы как - " + login[0]);
stringList.add("Синхронизация доступна до: получение информации от сервера");
stringList.add("Дата загрузки базы на сервер: получение информации от сервера");
}
/*
Облачный экспорт
 */
public void onClickButtonCloudSyncExport(View view) {
    TcpExportDb tcpCloudSync = new TcpExportDb(this, TcpCloudSync.EXPORT);
    tcpCloudSync.execute((Void) null);
}
/*
Облачный импорт
 */
    public void onClickButtonCloudSyncImport(View view) {
        ImportDb tcpCloudSync = new ImportDb(this, TcpCloudSync.IMPORT);
        tcpCloudSync.execute((Void) null);
    }

    public void onClickButtonCloudSyncClose(View view) {
        SharedPreferences.Editor editor = Preferences.getSharedPreferences(this).edit();
    editor.remove(Preferences.LOGIN_PASSWORD);
editor.remove(Preferences.SET_CHECK_BOX_EXPORT_schedule);
editor.remove(Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD);
editor.remove(Preferences.TIME_EXPORT_CLOUD);
editor.remove(Preferences.TIME_NG_EXPORT_CLOUD);
    editor.apply();
    finish();
    }


    @Override
    public void onBackPressed() {
Preferences.set(this,Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, checkBoxAutoExport.isChecked());
Preferences.set(this, Preferences.SET_CHECK_BOX_EXPORT_schedule, checkBoxExportSchedule.isChecked());
Preferences.set(this, Preferences.TIME_EXPORT_CLOUD, (String) spinnerExportTime.getSelectedItem());
Preferences.set(this, Preferences.TIME_NG_EXPORT_CLOUD, mapTimeExport.get(spinnerExportTime.getSelectedItem()));
        Intent intentNewTime = new Intent(this, AutoExportSchedule.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, ALARM_ID, intentNewTime, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
if (checkBoxExportSchedule.isChecked()) {
    alarmManager.setExact(AlarmManager.RTC_WAKEUP,Preferences.getLong(this, Preferences.TIME_NG_EXPORT_CLOUD, mapTimeExport.get("00:00")),
            pendingIntent);
DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY, HH:mm");

}else {
alarmManager.cancel(pendingIntent);
}
super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    checkBoxExportSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            spinnerExportTime.setEnabled(b);
        }
    });
    }

    private  class  TcpInfoUser extends  Tcp {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
            DateFormat dateFormat1 = new SimpleDateFormat("dd.MM.YYYY, HH:mm:ss");
                      String[] result = s.split("--");
                    if (result.length == 2) {
                      stringList.remove(1);
                    stringList.remove(1);
                    stringList.add("Синхронизация доступна до: " + dateFormat.format(Long.valueOf(result[0])));
                    if (Long.valueOf(result[1]) > 0) {
                        stringList.add("Дата загрузки базы на сервер: " + dateFormat1.format(Long.valueOf(result[1])));
                    } else {
                        stringList.add("Дата загрузки базы на сервер: да ничё там еще нету");
                    }
                    arrayAdapter.notifyDataSetChanged();
                buttonExport.setEnabled(true);
                buttonImport.setEnabled(true);
        }else {
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
    }
        }

    private  class  ImportDb extends TcpCloudSync {

        public ImportDb(Context context, String select) {
            super(context, select);
        }

        @Override
        protected void onPostExecute(String s) {
            if ("true".equals(s)) {
Toast.makeText(getApplicationContext(), "База загружена", Toast.LENGTH_SHORT).show();
                try {
                    bd.reStart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                LoadAlarmMananger loadAlarmMananger = new LoadAlarmMananger(getApplicationContext());
                loadAlarmMananger.execute();
            }else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

        }
    }

private  class  TcpExportDb extends  TcpCloudSync {
    public TcpExportDb(Context context, String select) {
        super(context, select);
    }

    @Override
    protected void onPostExecute(String s) {
    if ("true".equals(s)) {
        Toast.makeText(getApplicationContext(),"Красава! База улетела как вазилином смазанная", Toast.LENGTH_SHORT).show();
         tcpInfoUser = new TcpInfoUser();
        tcpInfoUser.execute("dateSync=" + login[0] + "--" + login[1]);
    }else {
        Toast.makeText(getApplicationContext(), "Что-то пошло не так" + " " + s, Toast.LENGTH_SHORT).show();
    }

    }
}
}
