package ru.burdin.clientbase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ru.burdin.clientbase.models.Expenses;
import ru.burdin.clientbase.models.Record;

public class StatActivity extends AppCompatActivity {

    private Spinner spinnerSelectPeriod;
    private Button buttonBack;
    private TextView textViewPeriod;
   private  Button buttonNext;
   private Map <String, Period>  periods = new LinkedHashMap<>();
private ArrayAdapter <?> adapter;
private  Calendar calendar;
private List <String> listPeriods;
private ListView listViewStat;
private  ArrayAdapter <String> adapterListView;
private  List <String> resStat = new ArrayList<>();
private  Bd bd;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
bd = Bd.load(this);
        spinnerSelectPeriod = findViewById(R.id.spinnerStatSelectPeriod);
buttonBack = findViewById(R.id.buttonStatBack);
textViewPeriod = findViewById(R.id.textViewStat);
buttonNext = findViewById(R.id.buttonStatNext);
listViewStat = findViewById(R.id.listViewStat);
mapInit();
listPeriods = new ArrayList<>(periods.keySet());
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPeriods);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinnerSelectPeriod.setAdapter(adapter);
    calendar = Calendar.getInstance();
periods.get(spinnerSelectPeriod.getSelectedItem().toString()).get();;
    resultInit();
    adapterListView = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, resStat
    );
    listViewStat.setAdapter(adapterListView);
}

    @Override
    protected void onResume() {
        super.onResume();
spinnerSelectPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
periods.get(listPeriods.get(i)).get();
    resultInit();
        adapterListView.notifyDataSetChanged();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
});
}
/*
Инициализация списка периода
 */

    private void mapInit() {
    periods.put(("Месяц"),new Period(new SimpleDateFormat("MMMM y"), Calendar.MONTH));
    periods.put("Год", new Period(new SimpleDateFormat("y"), Calendar.YEAR));
    }

    /*
    Прошлый период
     */
    public void onClickButtonStatBack(View view) {
    periods.get(spinnerSelectPeriod.getSelectedItem().toString()).backNext(-1);
    periods.get(spinnerSelectPeriod.getSelectedItem().toString()).get();
    resultInit();
    adapterListView.notifyDataSetChanged();
    }
    /*
    Выбрать конкретный период
     */
    public void onClickTextViewStat(View view) {
    }
/*
Следующий  период
 */
    public void onClickButtonStatNext(View view) {
    periods.get(spinnerSelectPeriod.getSelectedItem().toString()).backNext(1);
    periods.get(spinnerSelectPeriod.getSelectedItem().toString()).get();
        resultInit();
        adapterListView.notifyDataSetChanged();
    }

    /*
    Инициализация списка статистики за выбранный период
     */
private  void resultInit () {
    resStat.clear();
resStat.add("Статистика за выбранный период");
Period period = periods.get(spinnerSelectPeriod.getSelectedItem());
    period.getRecords();
// Подсчет клиентов и записей
    Map <Long, Long> countClient = period.records.stream()
            .collect(Collectors.groupingBy(Record::getIdUser, Collectors.counting()));
    List <Record> oldRecords = new ArrayList<>(bd.getRecords());
    oldRecords.removeAll(period.records);
    int countOldRecord = 0;
    for (long idUser : countClient.keySet()) {
        for (Record record : oldRecords) {
            if (record.getId() == idUser) {
                countOldRecord++;
                break;
            }
        }
    }
    resStat.add("Клиентов: " + countClient.keySet().size());
    resStat.add("Записей: " + period.records.size() + "");
//Подсчет суммы
double sumPrise = period.records.stream()
        .collect(Collectors.summingDouble(Record::getPrice));
double sumDef = period.expenses.stream()
        .collect(Collectors.summingDouble(Expenses::getPrice));
resStat.add("Чистый доход:  " + StaticClass.priceToString(sumPrise - sumDef));
resStat.add("Всего заработанно: " + StaticClass.priceToString(sumPrise) + ", всего расходов: "  + StaticClass.priceToString(sumDef));
// Статистика за всю историю
resStat.add("Статистика за всю историю");
    resStat.add("Всего записей: " + bd.getRecords().size());
 sumPrise = bd.getRecords().stream()
        .collect(Collectors.summingDouble(Record::getPrice));
sumDef = bd.getExpenses().stream()
        .collect(Collectors.summingDouble(Expenses::getPrice));
 resStat.add("Чистый доход: "+ StaticClass.priceToString(sumPrise - sumDef));
    resStat.add("Всего заработанно: " + StaticClass.priceToString(sumPrise) + ", всего расходов: "  + StaticClass.priceToString(sumDef));
}

private  class  Period {
        DateFormat dateFormat;
private  int p;
List <Record> records = new ArrayList<>();
List <Expenses> expenses;

protected  Period (DateFormat dateFormat, int p) {
            this.dateFormat = dateFormat;
        this.p = p;
this.records = new ArrayList<>();
this.expenses = new ArrayList<>();
}

/*
Устанавливает выбранный период на экране
 */
        public   void get() {
            textViewPeriod.setText("Выбран: " +  dateFormat.format(calendar.getTime()));
backNext(-1);
buttonBack.setText(dateFormat.format(calendar.getTime()));
        backNext(2);
        buttonNext.setText(dateFormat.format(calendar.getTime()));
            backNext(-1);
        }

        /*
        Перевод периода
         */
    public  void  backNext (int i) {
        calendar.add(p, i);
    }

    /*
    Собирает список записей  и расходов для текущего периода
     */
private void getRecords() {
records.clear();
expenses.clear();
for ( Record record : bd.getRecords()) {
        if (dateFormat.format(calendar.getTime()).equals(dateFormat.format(record.getStartDay())) &&Calendar.getInstance().getTimeInMillis() > record.getStart() + record.getEnd() ) {
records.add(record);
        }
    }
for (Expenses expensesOne : bd.getExpenses()) {
    if (dateFormat.format(calendar.getTime()).equals(dateFormat.format(expensesOne.getTime()))&& Calendar.getInstance().getTimeInMillis() > expensesOne.getTime()) {
expenses.add(expensesOne);
    }
}
}

}

}