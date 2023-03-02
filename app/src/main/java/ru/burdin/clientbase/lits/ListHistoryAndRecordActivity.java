package ru.burdin.clientbase.lits;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.analytics.Analytics;
import ru.burdin.clientbase.cards.cardSession.CardSessionActivity;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.Preferences;

public class ListHistoryAndRecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
private List<Record> records;
    private      Bd bd;
    private  Intent intentCardSession;
private TextView textViewListHistoryRecord;
private  List <List<Record>> numberOfCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history_and_record);
        try {
            bd = Bd.load(getApplicationContext());
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        textViewListHistoryRecord = findViewById(R.id.textViewListHistoryRecord);
    recyclerView = findViewById(R.id.listHistory);
    intentCardSession = new Intent( this, CardSessionActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
    listSetRcords();
    textViewListHistoryRecord.setText("Записей: " + records.size() + ", Курсов: " + numberOfCourse.size());
        updatelistHistory();

    }

    /*
    Создать запись
     */
    public void onClickButtonListHistoryAndRecordNewRecord(View view) {
    Intent intent = new Intent(this, ListSessionActivity.class);
        intent.putExtra(StaticClass.POSITION_LIST_USERS, StaticClass.indexList(getIntent().getExtras().getLong(Bd.TABLE), bd.getUsers()));
intent.putExtra(StaticClass.KEY, StaticClass.IISTORUNEWRECORD);
        startActivity(intent);
    }
    /*
    Создаем на экране список услуг
     */
    private  void  updatelistHistory () {
DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY HH:mm");
Consumer <MyAdapter.ViewHolder> consumer = viewHolder -> viewHolder.textView.setText(
        Preferences.getBoolean(getApplicationContext(), Preferences.SET_CHECK_BOX_PAY, true)? dateFormat.format(records.get(MyAdapter.count).getStartDay()) + ", " + records.get(MyAdapter.count).getProcedure() + ", " + StaticClass.priceToString(records.get(MyAdapter.count).getPrice()) + ". Оплачено: " + records.get(MyAdapter.count).getPay():dateFormat.format(records.get(MyAdapter.count).getStartDay()) + ", " + records.get(MyAdapter.count).getProcedure() + ", " + StaticClass.priceToString(records.get(MyAdapter.count).getPrice())
        );
MyAdapter myAdapter = new MyAdapter(this, records, onUserClickListener, consumer);
    recyclerView.setAdapter(myAdapter);
    }

    /*
    Собираем в список все записи, которые принадлежат клиенту
     */
    private  void  listSetRcords () {
        long id = getIntent().getExtras().getLong(Bd.TABLE);
records = Analytics.listRecords(bd.getRecords(), id);
numberOfCourse = Analytics.numberOfCourses(records);
        records.sort(Comparator.reverseOrder());
    }

    //Определяем де функционал при нажатии
  MyAdapter.OnUserClickListener <Record> onUserClickListener = new MyAdapter.OnUserClickListener<Record>() {
    @Override
    public void onUserClick(Record record, int position) {
intentCardSession.putExtra(StaticClass.POSITION_LIST_RECORDS, record.getId());
    startActivity(intentCardSession);
    }

    @Override
    public void onLongClick(Record record, int position) {

    }
};

}