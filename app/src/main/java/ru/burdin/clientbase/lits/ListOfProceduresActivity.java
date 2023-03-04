package ru.burdin.clientbase.lits;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.MyAdapter.OnUserClickListener;
import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Procedure;

public class ListOfProceduresActivity extends AppCompatActivity {

    private EditText editTextName;
    private  EditText editTextPrice;
    private  EditText editTextTimeEnd;
    private RecyclerView recyclerViewProcedure;
    private MyAdapter myAdapter;
private Bd bd;
private TextView textView;
public static List<Procedure> processes = new ArrayList<>();
private  Activity activity;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_procedures);
        bd = Bd.load(getApplicationContext());
    recyclerViewProcedure = findViewById(R.id.listProcedures);
    editTextName = findViewById(R.id.editextProcedur);
    editTextPrice = findViewById(R.id.edittextPrice);
    editTextTimeEnd = findViewById(R.id.editTextTimeEnd);
    textView = findViewById(R.id.textTest);
    recyclerViewUpdate();
/*
Проверяем пришли ли мы с записи сеанса, если да, то изменяем текст
  */
if (AddSessionActivity.class.getName().equals(getIntent().getStringExtra(AddSessionActivity.class.getName()))){
    textView.setText("Щелкните по услуге, что бы выбрать");
}
activity = this;
}

/*
Кнопка для сохранение услуги
 */
public  void  buttonAddProcedur (View view) {
    if (check()) {
                ContentValues contentValues = new ContentValues();
        contentValues.put(Bd.COLUMN_NAME, editTextName.getText().toString());
        contentValues.put(Bd.COLUMN_PRICE, Double.valueOf(editTextPrice.getText().toString()));
//      contentValues.put(Bd.COLUMN_TIME_END, Long.valueOf(editTextTimeEnd.getText().toString()) * 60000);
        long t = Long.valueOf(editTextTimeEnd.getText().toString());
        contentValues.put(Bd.COLUMN_TIME_END,  TimeUnit.MINUTES.toMillis(t));
        long id =   bd.add(Bd.TABLE_PROCEDURE, contentValues);
        if (id > 0) {
            bd.getProcedures().add(new Procedure(id, editTextName.getText().toString(), Double.valueOf(editTextPrice.getText().toString()), TimeUnit.MINUTES.toMillis(Long.valueOf(editTextTimeEnd.getText().toString()))));
            hideKeyboard(this);
            editTextName.setText("");
            editTextPrice.setText("");
            editTextTimeEnd.setText("");
            recyclerViewUpdate();
            Toast.makeText(this, "Услуга сохранена", Toast.LENGTH_SHORT).show();
        }
        }

            }

    /*
    Устанавливает список услуг и обработчик выбора
     */
private  void  recyclerViewUpdate () {
    processes = bd.getProcedures();
    if (processes.size() == 0) {
    textView.setText("Список услуг пуст. Добавьте услугу.");
    }else {
        if (!AddSessionActivity.class.getName().equals(getIntent().getStringExtra(AddSessionActivity.class.getName()))) {
            textView.setText("Тапните на услугу, что бы удалить");
        }
        }
    Consumer <MyAdapter.ViewHolder> consumer = viewHolder -> viewHolder.textView.setText(processes.get(MyAdapter.count).getName() + " " + StaticClass.priceToString(processes.get(MyAdapter.count).getPrice()) + " руб, " + Long.toString(TimeUnit.MILLISECONDS.toMinutes(bd.getProcedures().get(MyAdapter.count).getTimeEnd())) + " минут");
    myAdapter = new MyAdapter(this, processes, new OnUserClickListener() {
        @Override
        public void onUserClick(Object o, int position) {
            /*
            Проверяем из какого класса пришли
                    */
            if (AddSessionActivity.class.getName().equals(getIntent().getStringExtra(AddSessionActivity.class.getName()))) {
                Intent intent = new Intent();
                intent.putExtra(ListOfProceduresActivity.class.getName(), position);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("/Точно удалить услугу - " + processes.get(position).getName());
                builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bd.delete(Bd.TABLE_PROCEDURE, processes.get(position).getId());
                        bd.getProcedures().remove(position);
                        recyclerViewUpdate();
                    }
                });
                builder.create().show();

            }
        }
            @Override
        public void onLongClick(Object o, int position) {

        }
    }, consumer);

            recyclerViewProcedure.setAdapter(myAdapter);
}

/*
Проверка валидности данных поступившие от редактора
 */

private  boolean check () {
    boolean result = false;
    if (editTextTimeEnd.getText().length() >0 && editTextName.getText().length() > 0&& editTextPrice.getText().length() >0 ) {
        result = true;
    }
return  result;
}

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String funMinutes(long arg) {
        return String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(arg));
    }


}