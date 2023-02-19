package ru.burdin.clientbase.add;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.analytics.Analytics;
import ru.burdin.clientbase.cards.CardUserActivity;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.lits.listClient.ListClientActivity;
import ru.burdin.clientbase.lits.ListOfProceduresActivity;
import ru.burdin.clientbase.models.Procedure;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;
import ru.burdin.clientbase.setting.TemplatesActivity;

public class AddSessionActivity extends AppCompatActivity {

    private TextView textViewSetTime;
private  TextView textViewSetUser;
private EditText editTextSetPrices;
private  EditText editTextSetTimeFinish;
private  EditText editTextSetComment;
private  EditText editTextPay;
private CheckBox checkBoxNotNotification;
private  CheckBox checkBoxOneLine;
private  int index = -1;
private Record record;
    private  static Bd bd;
private  int userIndex = -1;
private static ArrayList <Procedure> procedures;
private Button buttonAddProcedure;
private  int indexListSession;
private  int indexRecord = -1;
private CalendarSetting calendarSetting;
public  static  final  int CLASS_INDEX = 1;
private RadioGroup radioGroupMessange;
RadioButton radioButtonNotChck;
private RadioButton radioButtonSMS;
private RadioButton radioButtonWhatsApp;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
    textViewSetTime = findViewById(R.id.textViewSetupTime);
    textViewSetUser = findViewById(R.id.textViewSetupUser);
    buttonAddProcedure = findViewById(R.id.buttonSetupProcedure);
    editTextSetPrices = findViewById(R.id.editTextSetupPrise);
    editTextSetTimeFinish = findViewById(R.id.editTextSetupTimeFinish);
    editTextSetComment = findViewById(R.id.editTextSetupComment);
editTextPay = findViewById(R.id.editTextAddSessionPay);
    radioGroupMessange = findViewById(R.id.radioBoxAddSessionMessage);
radioButtonNotChck = findViewById(R.id.radioButtonAddSessionNot);
radioButtonSMS = findViewById(R.id.radioButtonAddSessionSMS);
radioButtonWhatsApp = findViewById(R.id.radioButtonAddSessionWAthsApp);
checkBoxOneLine = findViewById(R.id.checkBoxAddSessionOneLine);
checkBoxNotNotification = findViewById(R.id.checkBoxAddSessionNotNotification);
radioGroupMessange.check(radioButtonNotChck.getId());
DateFormat dateFormatTime = new SimpleDateFormat("HH:mm  EEEE dd-MM-YYYY");
calendarSetting = CalendarSetting.load(this);
if (savedInstanceState == null) {
    try {
        bd = Bd.load(this);
    } catch (InterruptedException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (ExecutionException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    } catch (TimeoutException e) {
        Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    procedures = new ArrayList<>();
    }else  {
    userIndex = savedInstanceState.getInt(StaticClass.POSITION_LIST_USERS, -1);
    }

long time = getIntent().getLongExtra(StaticClass.TIMEFREE, -1);
if (time != -1) {
record = new Record(time);
userIndex = getIntent().getIntExtra(StaticClass.POSITION_LIST_USERS, userIndex);
if (userIndex  != -1) {
record.setIdUser(bd.getUsers().get(userIndex).getId());
    textViewSetUser.setText(bd.getUsers().get(userIndex).getSurname() + " " + bd.getUsers().get(userIndex).getName());
}
}else  {
 indexRecord = getIntent().getIntExtra(StaticClass.POSITION_LIST_RECORDS, -1);
if (indexRecord != -1) {
    record = bd.getRecords().get(indexRecord);
    userIndex = StaticClass.indexList(record.getIdUser(), bd.getUsers());
    textViewSetUser.setText(bd.getUsers().get(userIndex).getSurname() + " " + bd.getUsers().get(userIndex).getName());
checkBoxOneLine.setChecked(StaticClass.intInBoolean(record.getOneLine()));
}
}
textViewSetTime.setText(dateFormatTime.format(record.getStartDay()));

    if (procedures.size() > 0) {
        buttonAddProcedure.setText("Ещё добавить услугу");
    }
    updateProcedure();
}

/*
Открыть список клиентов для выбора
 */
public  void  onClickSetUser (View view) {
        Intent intent = new Intent(this, ListClientActivity.class);
        intent.putExtra(AddSessionActivity.class.getName(), AddSessionActivity.class.getName());
        startActivityForResult(intent, StaticClass.LIST_USERS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        radioGroupMessange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButtonAddSessionSMS) {
                    if (!SendSMS.permission(getApplicationContext())) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SendSMS.PERMISSION_SMS);
                    }
                }
            }
        });
//Проверка на первый сеанс
        if (userIndex != -1) {
            List<Record> records = Analytics.listRecords(bd.getRecords(), bd.getUsers().get(userIndex).getId());
            if (records.size() == 0 || record.getStart() <= records.get(records.size() - 1).getStart()) {
                checkBoxOneLine.setEnabled(false);
            }
        editTextPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
if (charSequence.length() == 0 || charSequence.toString().equalsIgnoreCase("ноль")) {
    editTextPay.setText(StaticClass.priceToString(0.0));
}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        })
;        }
//Слушатель долгого нажатия выбора клиента
textViewSetUser.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View view) {
if (userIndex > -1) {
    Intent intent = new Intent(getApplicationContext(), CardUserActivity.class);
    intent.putExtra(ListClientActivity.class.getName(), userIndex);
    startActivity(intent);
}
        return true;
    }
});
if (userIndex > -1) {
    textViewSetUser.setText(bd.getUsers().get(userIndex).getSurname() + " " + bd.getUsers().get(userIndex).getName() + ". Удерживайте, чтобы открыть карточку клиента или просто кликнете, чтобы сменить клиента");
}
}

    /*
    Сохраняет  или редактирует запись в БД и в списке
     */
public void onClickButtonSessionSave(View view) {
    if ( userIndex > -1 && editTextSetTimeFinish.getText().length() > 0 && editTextSetPrices.getText().length() > 0) {
        Record record = new Record(this.record.getStart());
        record.setEnd(TimeUnit.MINUTES.toMillis(Long.valueOf(editTextSetTimeFinish.getText().toString())));
        record.setIdUser(bd.getUsers().get(userIndex).getId());
        String string = "";
        for (Procedure procedure : procedures) {
            string = string +" " + procedure.getName();
        }
        record.setProcedure(string);
        record.setPrice(Double.valueOf(editTextSetPrices.getText().toString()));
        record.setPay(Double.valueOf(editTextPay.getText().toString()));
        record.setComment(editTextSetComment.getText().toString());
        record.setOneLine(StaticClass.booleaInInt(checkBoxOneLine.isChecked()));
        record.setNotNotification(StaticClass.booleaInInt(checkBoxNotNotification.isChecked()));
        if (indexRecord !=-1) {
        record.setEvent_id(bd.getRecords().get(indexRecord).getEvent_id());
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Bd.COLUMN_TIME, record.getStart());
        contentValues.put(Bd.COLUMN_TIME_END, record.getEnd());
        contentValues.put(Bd.COLUMN_ID_USER, record.getIdUser());
        contentValues.put(Bd.COLUMN_PROCEDURE, record.getProcedure());
        contentValues.put(Bd.COLUMN_PRICE, record.getPrice());
        contentValues.put(Bd.COLUMN_COMMENT, record.getComment());
        contentValues.put(Bd.COLUMN_EVENT_ID, record.getEvent_id());
        contentValues.put(Bd.COLUMN_ONE_IN_LINE, record.getOneLine());
        contentValues.put(Bd.COLUMN_not_notification, record.getNotNotification());
        contentValues.put(Bd.COLUMN_PAY, record.getPay());
        if (indexRecord != -1) {
            if (bd.update(Bd.TABLE_SESSION, contentValues, bd.getRecords().get(indexRecord).getId()) == 1) {
    bd.getRecords().get(indexRecord).setStart(record.getStart());
    bd.getRecords().get(indexRecord).setEnd(record.getEnd());
    bd.getRecords().get(indexRecord).setIdUser(record.getIdUser());
    bd.getRecords().get(indexRecord).setProcedure(record.getProcedure());
    bd.getRecords().get(indexRecord).setPrice(record.getPrice());
    bd.getRecords().get(indexRecord).setPay(record.getPay());
    bd.getRecords().get(indexRecord).setComment(record.getComment());
    bd.getRecords().get(indexRecord).setEvent_id(record.getEvent_id());
bd.getRecords().get(indexRecord).setOneLine(record.getOneLine());
bd.getRecords().get(indexRecord).setNotNotification(record.getNotNotification());
SendSMS.startHourAlarmMenedjer(this, bd.getRecords().get(indexRecord));
if (calendarSetting.update(bd.getRecords().get(indexRecord),textViewSetUser.getText().toString()) == 0) {
    Toast.makeText(this, "Не удалось обновить запись в календаре", Toast.LENGTH_SHORT).show();
}
                setResult(RESULT_OK);
SendSMS.send(this, Preferences.getString(this, SendSMS.KEY_PREFERENSES.get(2), SendSMS.TEMPLETS.get(2)),record, radioGroupMessange.getCheckedRadioButtonId());
                finish();
}else {
    Toast.makeText(getApplicationContext(), "Обновить запись не удалось", Toast.LENGTH_SHORT).show();
            }
} else {
            if (!bd.getRecords().contains(record) || Preferences.getBoolean(getApplicationContext(), Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, false)) {
try {
    long evant = calendarSetting.addRecordCalender(record, textViewSetUser.getText().toString());
    if (evant > 0) {
        record.setEvent_id(evant);
    }
    if (evant != -2 && evant < 1) {
        Toast.makeText(this, "Не удалось добавить событие в календарь", Toast.LENGTH_SHORT).show();
    }
}catch (Exception e){
    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
}
contentValues.put(Bd.COLUMN_EVENT_ID, record.getEvent_id());
                long res = bd.add(Bd.TABLE_SESSION, contentValues);
                if (res > -1) {
                    if (bd.getRecords().add(new Record(res, record.getStart(),
                            record.getEnd(),
                            record.getIdUser(),
                            record.getProcedure(),
                            record.getPrice(),
                            record.getComment(),
record.getEvent_id(),
                            record.getNotNotification(),
                            record.getOneLine(),
                    record.getPay()
                            ))) {
                        Toast.makeText(getApplicationContext(), "Запись успешно добавлена.", Toast.LENGTH_SHORT).show();
SendSMS.send(this, Preferences.getString(this, SendSMS.KEY_PREFERENSES.get(0), SendSMS.TEMPLETS.get(0)), record, radioGroupMessange.getCheckedRadioButtonId());
if (Preferences.getInt(this, Preferences.APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1, TemplatesActivity.RADIO_DUTTON_TEMPLETES_NOTIFICATION_NOT_CHECK) == TemplatesActivity.RADIO_BUTTON_TEMPLETES_MOTIFICATION_HOUR) {
    SendSMS.startHourAlarmMenedjer(this, bd.getRecords().get(StaticClass.indexList(res, bd.getRecords())));
}
                    finish();

                }else {
                        Toast.makeText(getApplicationContext(), "Не удается сохранить", Toast.LENGTH_SHORT).show();
                    }
                    }

            } else{

            Toast.makeText(getApplicationContext(), "Запись пересекается с другим клиентом!", Toast.LENGTH_SHORT).show();
        }
    }
    }else  {
        Toast.makeText(getApplicationContext(), "Не все пункты заполнены", Toast.LENGTH_SHORT).show();
    }
}

/*
Кнопка вызова списка процедур
 */
    public void onClickButtonSetProcedure(View view) {
    Intent intent = new Intent(this, ListOfProceduresActivity.class);
    intent.putExtra(AddSessionActivity.class.getName(), AddSessionActivity.class.getName());
    startActivityForResult(intent, StaticClass.LIST_PROCEDURES);
    }

/*
 Метод определяет из какой активности вернулись и какие данные пришли
 **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        if (requestCode == StaticClass.LIST_USERS) {
             userIndex = data.getIntExtra(ListClientActivity.class.getName(), -1);

        }

        if (requestCode == StaticClass.LIST_PROCEDURES) {
        procedures.add(bd.getProcedures().get(data.getExtras().getInt(ListOfProceduresActivity.class.getName())));
    updateProcedure();
        buttonAddProcedure.setText("Ещё добавить услугу");
    sumPrices();
    }
        }
    }

    /*
    устанавливает список выбранных процедур
     */
private  void  updateProcedure () {
    RecyclerView recyclerView = findViewById(R.id.listSetupProcedure);
    Consumer <MyAdapter.ViewHolder> consumer = viewHolder -> viewHolder.textView.setText(procedures.get(MyAdapter.count).getName());
MyAdapter myAdapter = new MyAdapter(this, procedures, new MyAdapter.OnUserClickListener<Procedure>() {
    @Override
    public void onUserClick(Procedure procedure, int position) {
procedures.remove(position);
    updateProcedure();
    sumPrices();
    }

    @Override
    public void onLongClick(Procedure procedure, int position) {

    }
}, consumer);
recyclerView.setAdapter(myAdapter);
}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
outState.putInt(StaticClass.POSITION_LIST_USERS, userIndex);
}
/*
Подсчитывается общая сумма выбранных услуг
 */
    private  void  sumPrices () {
double result = 0.0;
long resultTime = 0l;
for (Procedure procedure : procedures) {
        result = result + procedure.getPrice();
resultTime = resultTime + procedure.getTimeEnd();
}
    editTextSetPrices.setText(Double.toString(result));
    editTextSetTimeFinish.setText(Long.toString(TimeUnit.MILLISECONDS.toMinutes(resultTime)));
    editTextPay.setText(Double.toString(result));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
if (requestCode == SendSMS.PERMISSION_SMS) {
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

    }else {
        radioGroupMessange.check(radioButtonNotChck.getId());
        StaticClass.getDialog(this, "отправку SMS");
    }
}
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}