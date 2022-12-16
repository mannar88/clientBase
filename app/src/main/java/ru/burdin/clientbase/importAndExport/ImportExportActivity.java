package ru.burdin.clientbase.importAndExport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.setting.Preferences;

public class ImportExportActivity extends AppCompatActivity {

private BdImportExport bdExportImport;
private  Bd bd;
private CheckBox checkBoxAutoExport;
private Activity activity;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
bdExportImport = new BdImportExport(getDatabasePath(Bd.DATABASE_NAME).getPath());
bd = Bd.load(this);
checkBoxAutoExport = findViewById(R.id.checkBoxImportExportAutoExport);
checkBoxAutoExport.setChecked(Preferences.getBoolean(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false));
activity = this;
}


    @Override
    protected void onResume() {
        super.onResume();
checkBoxAutoExport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            bdExportImport.requestMultiplePermissions(activity);
        }
        Preferences.set(getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, b);
    }
});
}

    /*
    Кнопка импорта БД
     */
    public void onClickButtonImport(View view) {
        if (bdExportImport.requestMultiplePermissions(this)) {
            try {
                Toast.makeText(getApplicationContext(), bdExportImport.inport(), Toast.LENGTH_SHORT).show();
                bd.reStart();
            } catch (ExecutionException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + " ExecutionException", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
            Toast.makeText(this, e.getLocalizedMessage() + " InterruptedException", Toast.LENGTH_SHORT).show();
            }
        }
    }

/*
Кнопка экспорта БД
 */
    public void onClickButtonExport(View view) {
        if (bdExportImport.requestMultiplePermissions(this)) {
            try {
                Toast.makeText(getApplicationContext(), bdExportImport.exportBd(), Toast.LENGTH_SHORT).show();
            } catch (ExecutionException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + " ExecutionException", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + " InterruptedException", Toast.LENGTH_SHORT).show();
            }
        }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
switch (requestCode) {
    case BdImportExport.REQUEST_PERMISSIONS:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED  && grantResults[1] == PackageManager.PERMISSION_DENIED) {
            StaticClass.getDialog(this, "на чтение и запись файловой системы");
        }
break;
}
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
switch (requestCode) {
    case  BdImportExport.REQUEST_PERMISSIONS_ALL:
        if ( !Environment.isExternalStorageManager()) {
    StaticClass.getDialog(this, "на чтение и запись файловой системы");
        checkBoxAutoExport.setChecked(false);
        Preferences.set(this, Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false);
        }
break;
}
    }


}