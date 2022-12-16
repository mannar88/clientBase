package ru.burdin.clientbase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import ru.burdin.clientbase.lits.ListClientActivity;

import static ru.burdin.clientbase.BuildConfig.VERSION_CODE;

public class InfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
 private List <String> list;
private MyAdapter myAdapter;
private  MyAdapter.OnUserClickListener <String> onUserClickListener;
Consumer<MyAdapter.ViewHolder> consumer= viewHolder -> viewHolder.textView.setText(list.get(MyAdapter.count));
private Activity activity;
private TextView textViewInfoVersionApp;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        recyclerView = findViewById(R.id.recyclerViewInfoe);
textViewInfoVersionApp = findViewById(R.id.textViewInfoVersionAPP);
    String versionName = BuildConfig.VERSION_NAME;
textViewInfoVersionApp.setText("Версия приложения: " + versionName);
    list = Arrays.asList(getResources().getStringArray(R.array.info_donate));
select();
myAdapter = new MyAdapter(this, list, onUserClickListener, consumer);
recyclerView.setAdapter(myAdapter);
activity = this;
}

private  void  select (){
    onUserClickListener = new MyAdapter.OnUserClickListener<String>() {
        @Override
        public void onUserClick(String s, int position) {
switch (position) {
    case 1:
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        String [] card = s.split(" ",2);
ClipData clipData = ClipData.newPlainText("number_card", card[0]);
clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(activity, "Номер карты скопирован", Toast.LENGTH_SHORT).show();
break;
    case 3:
        String  number ="https://t.me/ViktorBurdin";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(number));
        startActivity(intent);
        break;
case 5:
    String  cannal ="https://t.me/ClientB_ase";
    Intent intentCannal = new Intent(Intent.ACTION_VIEW, Uri.parse(cannal));
    startActivity(intentCannal);
break;
case 7:
String group = "https://t.me/+LRWaSmfAmahkYmVi";
    Intent intentGroup = new Intent(Intent.ACTION_VIEW, Uri.parse(group));
    startActivity(intentGroup);
    break;
}
        }

        @Override
        public void onLongClick(String s, int position) {

        }
    };
}

}