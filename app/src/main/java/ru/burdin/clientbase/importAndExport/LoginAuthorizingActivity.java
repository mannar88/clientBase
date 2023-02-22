package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import ru.burdin.clientbase.R;

public class LoginAuthorizingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorizing);
    setTitle("Авторизация");
    }

    public void onClickbuttonLoginAuthorizing(View view) {
    }
}