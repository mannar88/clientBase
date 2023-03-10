package ru.burdin.clientbase.add;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Comparator;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.lits.litsSession.ListSessionActivity;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;

public class AddClientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextPhone;
    private EditText editTextComment;
    private Bd bd;
    private User user;
    private int index = -1;
private CheckBox checkBoxRecord;
private  String addSession = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        editTextName = findViewById(R.id.editTextName);
        editTextName.setText("");
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextSurname.setText("");
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPhone.setText("");
        editTextComment = findViewById(R.id.editTextComment);
        editTextComment.setText("");
checkBoxRecord = findViewById(R.id.checkboxAddClientRecord);
//Проверяем, пришли с записи сеанса
addSession = getIntent().getExtras().getString(AddSessionActivity.class.getName(), "");
checkBoxRecord.setEnabled(addSession.equals(AddSessionActivity.class.getName())? false:true);
bd = Bd.load(getApplicationContext());
        index = getIntent().getIntExtra(Bd.TABLE, -1);
        getIntent().removeExtra(Bd.TABLE);
        if (index > -1) {
            user = bd.getUsers().get(index);
            editTextName.setText(user.getName());
            editTextName.setSelection(editTextName.getText().length());
            editTextSurname.setText(user.getSurname());
            editTextSurname.setSelection(editTextPhone.getText().length());
            editTextPhone.setText(user.getPhone());
            editTextPhone.setSelection(editTextPhone.getText().length());
            editTextComment.setText(user.getComment());
            editTextComment.setSelection(editTextComment.getText().length());
        }else {
            editTextPhone.setText(getIntent().getStringExtra(StaticClass.NUMBER_PHONE));
        }
        saveContactIntent();

    }

    /*
Сохраняет контакт, пришедший из Intent
 */
    private void saveContactIntent() {
        if (getIntent().getAction() != null && (getIntent().getAction().equals(Intent.ACTION_INSERT) || getIntent().getAction().equals(Intent.ACTION_INSERT_OR_EDIT))) {
            Bundle bundle = getIntent().getExtras();
            editTextName.setText(bundle.getString("name"));
            editTextPhone.setText(bundle.getString("phone"));
editTextPhone.setSelection(editTextPhone.length());
editTextName.setSelection(editTextName.length());
        }

        }

    public void buttonSaveC(View view) {
        long id = 0;
        if (editTextPhone.getText().length() > 0 || editTextSurname.getText().length() > 0 || editTextName.getText().length() > 0) {
            if (check()) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(Bd.COLUMN_NAME, notNull(editTextName));
                contentValues.put(Bd.COLUMN_SURNAME, notNull(editTextSurname));
                contentValues.put(Bd.COLUMN_PHONE, notNull(editTextPhone));
                contentValues.put(Bd.COLUMN_COMMENT, notNull(editTextComment));
                if (index == -1) {
 id = bd.add(Bd.TABLE, contentValues, Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(this, Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
                    if (id > 0) {
                        if (bd.getUsers().add(new User(id, notNull(editTextName), notNull(editTextSurname), notNull(editTextPhone), notNull(editTextComment)))) {
                            bd.getUsers().sort(Comparator.naturalOrder());
                        }
                        Toast.makeText(getApplicationContext(), "Клиент успешно добавлен", Toast.LENGTH_SHORT).show();
                        if (checkBoxRecord.isChecked()) {
                            Intent intent = new Intent(this, ListSessionActivity.class);
                            intent.putExtra(StaticClass.POSITION_LIST_USERS, StaticClass.indexList(id, bd.getUsers()));
                            intent.putExtra(StaticClass.KEY, StaticClass.IISTORUNEWRECORD);
                            startActivity(intent);
                        }
                    }
                } else {
                    if (bd.update(Bd.TABLE, contentValues, user.getId(), Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(this, Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false)) == 1) {
                        bd.getUsers().get(index).setName(editTextName.getText().toString());
                        bd.getUsers().get(index).setSurname(editTextSurname.getText().toString());
                        bd.getUsers().get(index).setPhone(editTextPhone.getText().toString());
                        bd.getUsers().get(index).setComment(editTextComment.getText().toString());
                    }
                    Toast.makeText(getApplicationContext(), "Карточка клиента успешна обновлена", Toast.LENGTH_SHORT).show();
                }
                //Если пришли с записи сеанса, передаем id юзера
                if (addSession.equals(AddSessionActivity.class.getName())) {
Intent intent = new Intent();
intent.putExtra(AddSessionActivity.class.getName(), id);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }else {
                Toast.makeText(getApplicationContext(), "Проверте правильно ли написан номер", Toast.LENGTH_SHORT).show();
            }
            } else {
                    Toast.makeText(getApplicationContext(), "Хоть одно поле должно быть заполнено", Toast.LENGTH_SHORT).show();
                }
            }

    /*
   Проверка на длину
     */
private  String notNull ( EditText editText) {
    return  editText.getText().length() == 0? " ":editText.getText().toString();
}

    private boolean check() {
        boolean result = false;
if (editTextPhone.getText().length() > 9 || editTextPhone.getText().length() == 0){
            result = true;
        }
        return result;
    }
/*
Поменять местами имя и фамилия
 */
    public void onClickButtonAddClientExchange(View view) {
                    String exchange = editTextSurname.getText().toString();
    editTextSurname.setText(editTextName.getText().toString());
    editTextName.setText(exchange);
    }
}
