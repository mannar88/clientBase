package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_sync);
    setTitle("Облачная синхронизация");
    listView = findViewById(R.id.listViewCloudSync);
        arrayAdapter = new ArrayAdapter <>(this,
                android.R.layout.simple_list_item_1, stringList
        );
listView.setAdapter(arrayAdapter);
    stringListAdd();
    }

    /*
    Заполняет список
     */
private  void  stringListAdd(){
    String [] login = Preferences.getString(this, Preferences.LOGIN_PASSWORD, "false").split("--");
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
    stringList.add("Авторизованы как - " + login[0]);
Tcp tcp = new Tcp();
tcp.execute("dateSync="+login[0]+"--"+login[1]);
long date = 0l;
try {
         date = Long.valueOf(tcp.get(2, TimeUnit.SECONDS));
    } catch (ExecutionException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (TimeoutException e) {
        e.printStackTrace();
    }
stringList.add("Синхронизация доступна до: "+ dateFormat.format(date));
}

}