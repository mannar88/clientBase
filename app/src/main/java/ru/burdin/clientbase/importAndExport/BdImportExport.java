package ru.burdin.clientbase.importAndExport;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.setting.SettingActivity;

import static android.os.Environment.getExternalStorageDirectory;

public    class BdImportExport {

    private  File file_Bd;
private  File file_export;
private  File fileImport;
private final  String PATH ="Клиентская база";
public   static  final  int REQUEST_PERMISSIONS = 1;
     public   static  final  int REQUEST_PERMISSIONS_ALL = 2;

public BdImportExport(String path) {
    this.file_Bd = new File(path);
this.file_export = new File(getExternalStorageDirectory(), PATH);
this.fileImport = new File(getExternalStorageDirectory().getAbsolutePath() + "/" + PATH + "/" + Bd.DATABASE_NAME);
    }

/*
Экспорт базы данных
 */
    public  String  exportBd () throws ExecutionException, InterruptedException {
        FileTask fileTask = new FileTask();
            Supplier<String> supplier = new Supplier<String>() {
                @Override
                public String get() {
                    String text = "";
                    if (!file_export.exists()) {
                        file_export.mkdir();
                    }
                    try {
                        Files.copy(file_Bd.toPath(), Paths.get(file_export.getAbsolutePath() + "/" + Bd.DATABASE_NAME), StandardCopyOption.REPLACE_EXISTING);
                        text = "База экспортировалась успешно в папку Клиентская база";
                    } catch (IOException e) {
                        e.printStackTrace();
                        text = e.getLocalizedMessage() + " IOException";
                    }
                        return text;
                    }
                };
                fileTask.execute(supplier);
                return  fileTask.get();
    }

    public  String inport () throws ExecutionException, InterruptedException {
    Supplier<String> supplier = new Supplier<String>() {
        @Override
        public String get() {
String result = "";
if (!fileImport.exists()){
    result = "Файл базы данных отсутствует";
}else {
    try {
        Files.copy(fileImport.toPath(), file_Bd.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
result = e.getLocalizedMessage();    }
    result = "База данных  импортированна успешно";
}
return result;
        }
    };
FileTask fileTask = new FileTask();
fileTask.execute(supplier);
return  fileTask.get();
}

    /*
            Запрос разрешение на файловую систему
             */

    public boolean requestMultiplePermissions(Activity activity) {
        boolean result = false;
        if (Build.VERSION.SDK_INT < 30) {
            String reExternalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
            String writeExternalStoregePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            int haReExternalStoragePermission =activity.checkSelfPermission(reExternalStoragePermission);
            int haWriteExternalStoregePermission = activity.checkSelfPermission(writeExternalStoregePermission);
            List<String> permissions = new ArrayList<>();
            if (haReExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(reExternalStoragePermission);
            }
            if (haWriteExternalStoregePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(writeExternalStoregePermission);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
activity.requestPermissions(params, REQUEST_PERMISSIONS);
            } else {
                result = true;
            }
        }else {
            if ( !(result = Environment.isExternalStorageManager())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
                activity.startActivityForResult(intent , REQUEST_PERMISSIONS_ALL);
            }else  {
                result  = true;
            }
        }
        return  result;
    }

/*
Класс для потока
 */
    class FileTask extends AsyncTask <Supplier<String>, Void, String> {

        @Override
        protected String doInBackground(Supplier<String>... suppliers) {
            return suppliers[0].get();
        }
    }
    }
