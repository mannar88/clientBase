package ru.burdin.clientbase.importAndExport;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.burdin.clientbase.R;
import ru.burdin.clientbase.setting.Preferences;

public class CloudSyncActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
private List<String> stringList = new ArrayList<>();
private  TcpInfoUser tcpInfoUser;
private  String[] login;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_sync);
    setTitle("Облачная синхронизация");
    listView = findViewById(R.id.listViewCloudSync);
        stringListAdd();
        arrayAdapter = new ArrayAdapter <>(this,
                android.R.layout.simple_list_item_1, stringList
        );
listView.setAdapter(arrayAdapter);
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
    TcpCloudSync tcpCloudSync = new TcpCloudSync(this, TcpCloudSync.EXPORT);
    tcpCloudSync.execute((Void) null);
}
/*
Облачный импорт
 */
    public void onClickButtonCloudSyncImport(View view) {
        TcpCloudSync tcpCloudSync = new TcpCloudSync(this, TcpCloudSync.IMPORT);
        tcpCloudSync.execute((Void) null);
    }

    public void onClickButtonCloudSyncClose(View view) {
    tcpInfoUser.close();
    }

    private  class  TcpInfoUser extends  Tcp {
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
        DateFormat dateFormat1 = new SimpleDateFormat("dd.MM.YYYY, hh:mm:ss");
                if (s != null && !s.isEmpty()) {
String[]result = s.split("--");
    stringList.remove(1);
    stringList.remove(1);
    stringList.add( "Синхронизация доступна до: " + dateFormat.format(Long.valueOf(result[0])));
    if (Long.valueOf(result[1]) > 0) {
        stringList.add("Дата загрузки базы на сервер: " + dateFormat1.format(Long.valueOf(result[1])));
    }else {
        stringList.add("Дата загрузки базы на сервер: да ничё там еще нету");
    }
    arrayAdapter.notifyDataSetChanged();
}
        }
    }
}
