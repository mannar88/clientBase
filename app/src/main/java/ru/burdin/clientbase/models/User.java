package ru.burdin.clientbase.models;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import okhttp3.internal.Util;
import ru.burdin.clientbase.setting.Preferences;

public class User implements Comparable, Model  {

    private  long id;
    private  String name;
    private  String surname;
    private  String phone;
private  String comment;
public  static  final  int CALL_PERMISSION = 1;

public User() {
    }
    public User(long id, String name, String surname, String phone, String comment) {
    this.id = id;
        this.name = notNull(name);
        this.surname = notNull(surname);
        this.phone = phoneFormat(phone);
        this.comment = notNull(comment);
if ((this.surname == "" && this.name == "") || (this.surname == null && this.name == null)) {
    this.surname = phone;
}
}

    /*
    Исправляет номера телефона
     */

    private String phoneFormat (String phone) {
        if (phone.length() < 10) {
            phone ="+7000000000";
        }
        String result = PhoneNumberUtils.normalizeNumber(phone);
if (result.length() > 0) {
    if ("RU".equalsIgnoreCase(Locale.getDefault().getCountry())) {
        if (result.substring(0, 2).equalsIgnoreCase("89")) {
            result = "+7" + result.substring(1);
        }
        if (phone.charAt(0) == '9' && result.length() == 10) {
            result = "+7" + result;
        }
    }
}
        return result.length() >0? result : phone;
}

/*
Убирает null, если есть
 */
    private String notNull( String string) {
        return string.equalsIgnoreCase("null")? "": string;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /*
    Позвонить
     */
public  void call(Activity activity) {
    if (!Preferences.getBoolean(activity.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_LINEPHONE, false)) {
        String callPermission = Manifest.permission.CALL_PHONE;
        int hasPermission = activity.checkSelfPermission(callPermission);
        String[] permissions = new String[]{callPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(permissions, CALL_PERMISSION);
        } else {
            Intent intentColl = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.phone));
            activity.startActivity(intentColl);
        }
    }else {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            activity.startActivity(intent);
    }

}

/*
Написать
 */
    public  void  send (Activity activity) {
        String [] messanger = new  String[] {"SMS","WHATSAPP", "TELEGRAMM", "VIBER"};
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setItems(messanger, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PackageManager pm=activity.getPackageManager();
                switch (i){
    case 0:
    Intent intentSend = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
       activity.startActivity(intentSend);
break;
    case 1:
        String formattedNumber = phone.substring(1);
        try{
            Intent sendIntent =new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT,"");
            sendIntent.putExtra("jid", formattedNumber +"@s.whatsapp.net");
            sendIntent.setPackage("com.whatsapp");
            activity.startActivity(sendIntent);
        }
        catch(Exception e)      {
            String  number  = "https://wa.me/" + phone;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(number));
            activity.startActivity(intent);
        }
        break;
    case 2:
        Intent intentTelegramm = new Intent(Intent.ACTION_VIEW,Uri.parse("tg://resolve?domain=" + phone.substring(1)));
        activity.startActivity(intentTelegramm);
break;
    case  3:
        String viberPackageName = "com.viber.voip";
activity.startActivity(
        new                         Intent(Intent.ACTION_VIEW,
                Uri.parse("viber://add?number="+phone.substring(1))
                ));
                break;
                }
            }
        });
    b.create().show();
    }

    @Override
    public int compareTo(Object o) {
    o = (User)o;
        return  this.getSurname().compareToIgnoreCase(((User) o).surname);
    }

    public  double saldo (List <Record> records) {
        double price = 0.0;
double pay = 0.0;
for (Record record:records) {
    price = price +record.getPrice();
    pay = pay + record.getPay();
        }
        return  pay - price;
    }
}
