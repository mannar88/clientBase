package ru.burdin.clientbase.lits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.add.AddClientActivity;
import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.cards.CardUserActivity;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.models.User;

public class ListClientActivity extends AppCompatActivity {

    private Bd bd;
    private TextView textViewCount;
    private List<User> users;
private EditText editTextSerch;
    private  String addSession = "";
    private  Intent intent;
private  RecyclerView recyclerViewClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);
         intent = new Intent(this, CardUserActivity.class);
        try {
            bd = Bd.load(getApplicationContext());
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            addSession = getIntent().getExtras().getString(AddSessionActivity.class.getName());
        } catch (Exception e) {

        }
 recyclerViewClient = findViewById(R.id.list);
        textViewCount = findViewById(R.id.textCountUsers);
editTextSerch = findViewById(R.id.editTextListClientSearsh);
users = bd.getUsers();
    listenerSearch();
    }


    /*
    Запись нового клиента
     */
    public  void  buttonAddClient (View view) {
SelectAddClient selectAddClient = new SelectAddClient(this);
selectAddClient.getDialog();
    }

    /*
    Слушатель поиска
     */
    private   void listenerSearch () {
    editTextSerch.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
if (charSequence.length() > 0) {
users = new ArrayList<>();
String result = "";
    if (charSequence.charAt(0) == '+' || charSequence.charAt(0) == '8') {
        for (int j = 0; j < charSequence.length(); j++) {
            if ((charSequence.charAt(j) == '+' && j == 0) || Character.isDigit(charSequence.charAt(j))) {
                if (j == 0 && charSequence.charAt(j) == '8') {
                    result = "+7";
                    continue;
                }
                result = result + charSequence.charAt(j);
            }
        }
        if (!result.equals(charSequence.toString())) {
            editTextSerch.setText(result);
        }
        editTextSerch.setSelection(result.length());
    }
    String text = charSequence.toString();
    for (User user : bd.getUsers()){
        if (user.getSurname().toLowerCase().contains(text.toLowerCase()) || user.getName().toLowerCase().contains(text.toLowerCase()) || user.getPhone().contains(charSequence)
        ){
users.add(user);
    }
}
}else  {
    users = bd.getUsers();
}
        updateList();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    });
    }
/*
Выводит список клиентов на экран
 */
    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    /*
        Установка листа клиентов
         */
    public   void  updateList () {
        Consumer <MyAdapter.ViewHolder> consumer = viewHolder ->viewHolder.textView.setText(users.get(MyAdapter.count).getSurname() +  " " + users.get(MyAdapter.count).getName());
MyAdapter.OnUserClickListener <User> onUserClickListener = new MyAdapter.OnUserClickListener<User>() {
    @Override
    public void onUserClick(User user, int position) {
        if (addSession.equals(AddSessionActivity.class.getName())) {
            Intent intentAddSession = new Intent();
            intentAddSession.putExtra(ListClientActivity.class.getName(), StaticClass.indexList(user.getId(), bd.getUsers()));
            setResult(RESULT_OK, intentAddSession);
            finish();
        }else {
            intent.putExtra(Bd.TABLE, StaticClass.indexList(user.getId(), bd.getUsers()));
            startActivity(intent);
        editTextSerch.setText("");
        }
    }

    @Override
    public void onLongClick(User user, int position) {

    }
};
MyAdapter myAdapter = new MyAdapter(this, users , onUserClickListener, consumer);
recyclerViewClient.setAdapter(myAdapter);
textViewCount.setText("Всего клиентов: " + users.size() );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
switch (requestCode ) {
    case SelectAddClient.PERNISSION_LOG_COLL:
if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    SelectAddClient selectAddClient = new SelectAddClient(this);
    selectAddClient.callLog();
}else {
    StaticClass.getDialog(this, "на чтение журнала звонков");
}
break;

case SelectAddClient.PERMISSION_PHONE_BOOK:
    if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        SelectAddClient selectAddClient = new SelectAddClient(this);
        selectAddClient.phoneBooke();
    }else {
        StaticClass.getDialog(this, "на чтение телефоной книги");
    }
    break;

}

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}