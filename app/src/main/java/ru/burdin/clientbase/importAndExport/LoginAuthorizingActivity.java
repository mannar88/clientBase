package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

    String text = "login=" + editTextLogin.getText().toString() + "--" + editTextPass.getText().toString();
TcpLogin tcpLogin = new TcpLogin();
tcpLogin.execute(text);
    }

    private  class  TcpLogin extends  Tcp {
    @Override
    protected void onPostExecute(String s) {
                if (s.equals("false")) {
            Toast.makeText(getApplicationContext(), "Не верный логин или пароль", Toast.LENGTH_SHORT).show();
        }
        if (s.equals("true")) {
            Toast.makeText(getApplicationContext(), "Авторизация прошла успешна", Toast.LENGTH_SHORT).show();
            Preferences.set(getApplicationContext(), Preferences.LOGIN_PASSWORD, editTextLogin.getText().toString() +"--"+editTextPass.getText().toString());
            Intent intent = new Intent(getApplicationContext(), CloudSyncActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
    }