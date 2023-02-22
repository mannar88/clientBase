package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.burdin.clientbase.R;
import ru.burdin.clientbase.setting.Preferences;

public class LoginAuthorizingActivity extends AppCompatActivity {

    private EditText editTextLogin;
    private  EditText editTextPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorizing);
    setTitle("Авторизация");
    editTextLogin = findViewById(R.id.editTextLoginAuthorizingLogin);
    editTextPass = findViewById(R.id.editTextLoginAuthorizingPass);
    }

    public void onClickbuttonLoginAuthorizing(View view) {
String result = "";
    Tcp tcp = new Tcp();
    String text = "login=" + editTextLogin.getText().toString() + "--" + editTextPass.getText().toString();
tcp.execute(text);
try {
    result = tcp.get(2, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
if (result.equals("false")) {
    Toast.makeText(this, "Не верный логин или пароль", Toast.LENGTH_SHORT).show();
}
if (result.equals("true")) {
    Toast.makeText(this, "Авторизация прошла успешна", Toast.LENGTH_SHORT).show();
    Preferences.set(this, Preferences.LOGIN_PASSWORD, editTextLogin.getText().toString() +"--"+editTextPass.getText().toString());
    Intent intent = new Intent(this, CloudSyncActivity.class);
    startActivity(intent);
finish();
}
    }
}