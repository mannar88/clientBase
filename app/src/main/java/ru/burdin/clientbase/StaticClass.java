package ru.burdin.clientbase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import ru.burdin.clientbase.models.Model;
import ru.burdin.clientbase.models.User;

public final class StaticClass  {
    public  static  final  int LIST_USERS = 0;
public  static  final  int LIST_PROCEDURES = 1;
public  static  final  int LISTSESSION = 2;
public  static  final  String POSITION_LIST_RECORDS = "positionRECORD";
    public  static  final String DUPLICATION = "duplication";
public  static  final  String KEY = "key";
public  static  final  String CARDSESSION = "cardSession";
public  static  final  String TIMEFREE = "timeFree";
public  static  final  String   POSITION_LIST_USERS  = "position_list_users";
public  static  final  String NEWRECORDISCARD = "newRecordCardUser";
/*
    Ищет индекс в коллекции по его id
     */
public  static  int indexList (long id, ArrayList <? extends Model> list){
    int result = -1;
    for (int i = 0; i < list.size(); i++) {
        if (list.get(i).getId() == id) {
            result = i;
        }
    }
return  result;
}

    /*
обрабатывает цену и выводит в текст
 */
    public  static  String priceToString (double price) {
    String result = Double.toString(price);
    int count = (int)result.chars().count();
    if (result.indexOf("." )== count -2) {
        result = result + "0";
    }
    return result;
    }

    /*
    Выводит диалоговое окно
     */
    public  static void getDialog (Context context, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Нет разрешения " + text + "! Те чё, западло разрешить?");
        builder.setPositiveButton("Нет, не западло, сейчас разрешу",   new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);

                    }
                }
        );
        builder.setNegativeButton("Не разрешу, мало ли чё там в коде хакерского зарыто", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }


}
