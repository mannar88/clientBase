package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextLogin;
    private  EditText editTextPass;
    private EditText editTextDoublePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    setTitle("Регистрация нового пользователя");
editTextLogin = findViewById(R.id.editTextSignUpLogin);
editTextPass = findViewById(R.id.editTextSignUpPassword);
editTextDoublePass = findViewById(R.id.editTextSignUpDoublePassword);
    editTextLoginFiltr();
    }
/*
Логин только буквы
 */
private  void  editTextLoginFiltr() {
    editTextLogin.setFilters(new InputFilter[] {
            new InputFilter() {
@Override
public CharSequence filter(CharSequence source, int start,
                           int end, Spanned dest, int dstart, int dend) {
if(source.equals("")){ // for backspace
return source;
}
if(source.toString().matches("[a-zA-Zа-яА-Я]+")){
return source;
}
return "";
}
}
});
}

    /*
Регистрирует
 */
    public void onClickButtonSignUpSave(View view) {
if (check()) {
Tcp tcp = new Tcp();
String text ="signUp=" + editTextLogin.getText().toString() + "--" + editTextPass.getText().toString();
tcp.execute(text);    try {
    String result    = tcp.get(2, TimeUnit.SECONDS);
    if ("Login busy".equals(result)) {
            Toast.makeText(this, "Логин занят", Toast.LENGTH_SHORT).show();
        }
if ("Ok".equals(result)) {
    Toast.makeText(this, "Регистрация прошла успешна", Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(getApplicationContext(), LoginAuthorizingActivity.class);
    startActivity(intent);
finish();
}
} catch (ExecutionException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (TimeoutException e) {
        e.printStackTrace();
    }
}
    }

    private  boolean check() {
    boolean result = true;
    if (editTextLogin.length() == 0) {
        Toast.makeText(this, "Логин не может быть пустым", Toast.LENGTH_SHORT).show();
        return  false;
    }
if (editTextPass.length() == 0 || editTextDoublePass.length() ==0) {
    Toast.makeText(this, "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
    return  false;
}
if (!editTextPass.getText().toString().equals(editTextDoublePass.getText().toString())) {
Toast.makeText(this, "Парооли не совпадают", Toast.LENGTH_SHORT).show();
return  false;
}
    return  result;
    }

}