package ru.burdin.clientbase.lits;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Expenses;

import static java.text.DateFormat.FULL;
import static java.text.DateFormat.getDateInstance;

public class ListExpensesActivity extends AppCompatActivity {

    private TextView textViewTime;
    private EditText editTextNameExpenses;
   private   EditText editTextPriceExpenses;
   private RecyclerView recyclerViewExpenses;
   private Calendar date = Calendar.getInstance();
private  Bd bd;
private Activity activity;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_expenses);
    try {
        bd = Bd.load(getApplicationContext());
    } catch (InterruptedException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (ExecutionException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (TimeoutException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    textViewTime = findViewById(R.id.textViewSetupTimeExpenses);
        editTextNameExpenses = findViewById(R.id.editTextSetupNameExpenses);
        editTextPriceExpenses = findViewById(R.id.editTextSetupPriceExpenses);
        recyclerViewExpenses = findViewById(R.id.listExpenses);
        textViewTime.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date.getTime()) + " Щелкните для выбора ");
updateListExpenses();
activity= this;
}

    public void onClickTextViewSetTimeExpenses(View view) {
        new DatePickerDialog(this, d,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

     private DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
          date.set(i, i1, i2);
          textViewTime.setText(DateFormat.getDateInstance(FULL).format(date.getTime()));
          }
      };
/*
Сохранение расхода
 */
public void onClickButtonSetExpenses(View view) {
            if (check()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Bd.COLUMN_TIME, date.getTimeInMillis());
                contentValues.put(Bd.COLUMN_NAME, editTextNameExpenses.getText().toString());
                contentValues.put(Bd.COLUMN_PRICE, Double.valueOf(editTextPriceExpenses.getText().toString()));
                long result = bd.add(Bd.TABLE_EXPENSES, contentValues);
                if (result > 0) {
                    bd.getExpenses().add(new Expenses(result, date.getTimeInMillis(), editTextNameExpenses.getText().toString(), Double.valueOf(editTextPriceExpenses.getText().toString())));
                    editTextNameExpenses.setText("");
                    editTextPriceExpenses.setText("");
                updateListExpenses();
                Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /*
        устанавливает список расходов
         */
        private  void  updateListExpenses () {
            Consumer <MyAdapter.ViewHolder> consumer = viewHolder -> viewHolder.textView.setText(DateFormat.getDateInstance(FULL).format(new Date(bd.getExpenses().get(MyAdapter.count).getTime())) + " " + bd.getExpenses().get(MyAdapter.count).getName() + " " + bd.getExpenses().get(MyAdapter.count).getPrice() + ". Кликнете, что-бы удалить или удерживайте, что-бы дублировать");
bd.getExpenses().sort(Comparator.reverseOrder());
            MyAdapter myAdapter = new MyAdapter(this, bd.getExpenses(), new MyAdapter.OnUserClickListener<Expenses>() {
            @Override
            public void onUserClick(Expenses o, int position) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setMessage("Вы точно хотите удалить расход" );
    builder.setPositiveButton("Минимизировать расходы всегда приятно. Удалить", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            int res = bd.delete(Bd.TABLE_EXPENSES, bd.getExpenses().get(position).getId());
            if (res > 0) {
                bd.getExpenses().remove(position);
                updateListExpenses();
                Toast.makeText(getApplicationContext(), "Расход удален", Toast.LENGTH_SHORT).show();
            }

        }
    });
    builder.create().show();

}

                @Override
                public void onLongClick(Expenses expenses, int position) {
AlertDialog.Builder builder = new AlertDialog.Builder(activity);
builder.setNegativeButton("Дублировать", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
   editTextNameExpenses.setText(expenses.getName());
   editTextPriceExpenses.setText(StaticClass.priceToString(expenses.getPrice()));

    }
});
            builder.create().show();
            }
            }, consumer);
        recyclerViewExpenses.setAdapter(myAdapter);
        }

        private  boolean check () {
            boolean result = false;
            if (editTextNameExpenses.getText().length() >0 && editTextPriceExpenses.getText().length() > 0) {
                result = true;
            }
            return  result;
        }

}