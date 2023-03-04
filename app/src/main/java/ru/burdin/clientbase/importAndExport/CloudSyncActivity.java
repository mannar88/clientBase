package ru.burdin.clientbase.importAndExport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
    stringListAdd();
        arrayAdapter = new ArrayAdapter <>(this,
                android.R.layout.simple_list_item_1, stringList
        );
listView.setAdapter(arrayAdapter);
login = Preferences.getString(this, Preferences.LOGIN_PASSWORD, "false").split("--");
tcpInfoUser = new TcpInfoUser();
    tcpInfoUser.execute("dateSync=" + login[0] + "--" + login[1]);
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
Облачный експорт
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
    editor.apply();
    finish();
    }


    @Override
    public void onBackPressed() {
Preferences.set(this,Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, checkBoxAutoExport.isChecked());
        super.onBackPressed();
    }

    private  class  TcpInfoUser extends  Tcp {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
            DateFormat dateFormat1 = new SimpleDateFormat("dd.MM.YYYY, HH:mm:ss");
            if ("Сервер не доступен".equals(s)
|| "Истекло время ожидания".equals(s)
            || "ошибка соединения".equals(s)
            ) {
Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        } else {

                if (s != null && !s.isEmpty()) {
                    String[] result = s.split("--");
                    stringList.remove(1);
                    stringList.remove(1);
                    stringList.add("Синхронизация доступна до: " + dateFormat.format(Long.valueOf(result[0])));
                    if (Long.valueOf(result[1]) > 0) {
                        stringList.add("Дата загрузки базы на сервер: " + dateFormat1.format(Long.valueOf(result[1])));
                    } else {
                        stringList.add("Дата загрузки базы на сервер: да ничё там еще нету");
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            buttonExport.setEnabled(true);
                buttonImport.setEnabled(true);
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
