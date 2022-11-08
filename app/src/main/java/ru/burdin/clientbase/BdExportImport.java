package ru.burdin.clientbase;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class BdExportImport {

    private  File file_Bd;
private  File file_export;
public  BdExportImport ( String path) {
    this.file_Bd = new File(path);
this.file_export = new File(getExternalStorageDirectory(), "Клиентская база");
}

/*
Экспорт базы данных
 */


    public  String  exportBd () throws ExecutionException, InterruptedException {
FileTask fileTask = new FileTask();
Supplier <String> supplier = new Supplier<String>() {
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
        text ="Что-то пошло не так";
        }
        return  text;

    }
};
fileTask.execute(supplier);
            return  fileTask.get();
    }

class FileTask extends AsyncTask <Supplier<String>, Void, String> {


    @Override
    protected String doInBackground(Supplier<String>... suppliers) {
        return suppliers[0].get();
    }
}
}
