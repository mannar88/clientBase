package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
    stringList.add("Авторизованы как - " + login[0]);
stringList.add("Синхронизация доступна до: получение информации от сервера");
}

    public void onClickButtonCloudSyncExport(View view) throws ExecutionException {
TcpCloudSync tcpCloudSync = new TcpCloudSync(this);
tcpCloudSync.execute((Void) null);
    }
private  class  TcpInfoUser extends  Tcp {
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
    stringList.remove(1);
    stringList.add(1, "Синхронизация доступна до: " + dateFormat.format(Long.valueOf(s)));
arrayAdapter.notifyDataSetChanged();
    }
}


}