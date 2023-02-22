package ru.burdin.clientbase.importAndExport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.burdin.clientbase.R;

public class CloudSyncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_sync);
    setTitle("Облачная синхронизация");
    }
}