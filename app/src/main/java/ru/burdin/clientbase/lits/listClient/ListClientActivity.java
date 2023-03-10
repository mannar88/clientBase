package ru.burdin.clientbase.lits.listClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.cards.cardClient.CardClientActivity;
import ru.burdin.clientbase.MyAdapter;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.models.User;

public class ListClientActivity extends AppCompatActivity {

    private Bd bd;
    private TextView textViewCount;
    private List<User> users;
private EditText editTextSerch;
      String addSession = "";
    private  Intent intent;
private  RecyclerView recyclerViewClient;
Spinner spinnerSort;
private ArrayAdapter <String> adapter;
private  Spinner spinnerFiltr;
private  ArrayAdapter arrayAdapterFiltr;
private  ListClient listClient;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);
         intent = new Intent(this, CardClientActivity.class);
            bd = Bd.load(getApplicationContext());
        try {
            addSession = getIntent().getExtras().getString(AddSessionActivity.class.getName());
        } catch (Exception e) {

        }
 listClient = new ListClient(bd.getUsers(), bd.getRecords(), bd);
        recyclerViewClient = findViewById(R.id.list);
        spinnerSort = findViewById(R.id.spinerListClientSort);
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listClient.getKeysString());
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinnerSort.setAdapter(adapter);
spinnerFiltr = findViewById(R.id.spinerListClientFiltr);
arrayAdapterFiltr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listClient.keysFiltr());
    arrayAdapterFiltr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinnerFiltr.setAdapter(arrayAdapterFiltr);
    textViewCount = findViewById(R.id.textCountUsers);
editTextSerch = findViewById(R.id.editTextListClientSearsh);
users = listClient.getListUsers((String) spinnerSort.getSelectedItem());
listClient.visibilityPay(this, spinnerFiltr);
}


    /*
    ???????????? ???????????? ??????????????
     */
    public  void  buttonAddClient (View view) {
SelectAddClient selectAddClient = new SelectAddClient(this);
selectAddClient.getDialog();
    }

    /*
    ?????????????????? ????????????
     */
    private   void listenerSearch () {
    editTextSerch.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
List <User> usersSerch = new ArrayList<>();
            if (charSequence.length() > 0) {
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
    String text =editTextSerch.getText().toString();

    for (User user : bd.getUsers()){
        if (user.getSurname().toLowerCase().contains(text.toLowerCase()) || user.getName().toLowerCase().contains(text.toLowerCase()) || user.getPhone().contains(text)
        ){
usersSerch.add(user);
    }
}
}else  {
usersSerch = listClient.getListUsers((String) spinnerSort.getSelectedItem());
            }
        users = usersSerch;
updateList();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    });
    }

    /*
?????????????? ???????????? ???????????????? ???? ??????????
 */
    @Override
    protected void onResume() {
        super.onResume();
        updateList();
spinnerSortSetOnItemSelectedListener();
        listenerSearch();
    spinnerFiltrSetOnItemSelectedListener();
    }

    /*
    ?????????????????? ???????????? ????????????????????
     */
    private void spinnerSortSetOnItemSelectedListener() {
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                users = listClient.getListUsers(listClient.getKeysString()[i]);
                editTextSerch.setText("");
                users = listClient.getMapFiltrVelues((String) spinnerFiltr.getSelectedItem(), listClient.getKeysString()[i]);
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
/*
?????????????????? ?????????????? ??????????????
 */
private void spinnerFiltrSetOnItemSelectedListener() {
    spinnerFiltr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
users = listClient.getMapFiltrVelues(listClient.keysFiltr()[i], (String) spinnerSort.getSelectedItem());
updateList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });
                                          }

        /*
        ?????????????????? ?????????? ????????????????
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
MyAdapter myAdapter = new MyAdapter(this, users, onUserClickListener, consumer);
recyclerViewClient.setAdapter(myAdapter);
textViewCount.setText("?????????? ????????????????: " +users.size() );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case  StaticClass.LIST_USERS:
                if (resultCode == RESULT_OK) {
                    int index = StaticClass.indexList(data.getLongExtra(AddSessionActivity.class.getName(), 1l), bd.getUsers());
                    Intent intent = new Intent();
                    intent.putExtra(ListClientActivity.class.getName(), index);
                    setResult(RESULT_OK, intent);
                finish();
                }
        break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
switch (requestCode ) {
    case SelectAddClient.PERNISSION_LOG_COLL:
if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    SelectAddClient selectAddClient = new SelectAddClient(this);
    selectAddClient.callLog();
}else {
    StaticClass.getDialog(this, "???? ???????????? ?????????????? ??????????????");
}
break;

case SelectAddClient.PERMISSION_PHONE_BOOK:
    if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        SelectAddClient selectAddClient = new SelectAddClient(this);
        selectAddClient.phoneBooke();
    }else {
        StaticClass.getDialog(this, "???? ???????????? ?????????????????? ??????????");
    }
    break;

}

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}