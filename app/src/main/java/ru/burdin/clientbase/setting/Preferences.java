package ru.burdin.clientbase.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    //Пауза между SMS
    public static final String PAUSE_SMS = "pauseSMS";
    //Загрузка приложения с списком расписания
    public static final String START_ACTIVITY_LIST_SESSION = "startActivityListSession";
    //Логин ти пароль
    public static final String LOGIN_PASSWORD = "loginPassword";
    //Установка флажка оплаты в настройках
    public static final String SET_CHECK_BOX_PAY = "setCheckBoxPay";
    //Автоматический экспорт бд на сервер
    public static final String SET_CHECK_VOX_AUTO_EXPORT_BD = "auto_export_cloud";
    //Время автоматического экспорта на облауко
    public static final String TIME_EXPORT_CLOUD = "time_export_cloud";
    //Время в лонгах для экспорта на облако
    public static final String TIME_NG_EXPORT_CLOUD = "time_long_export_cloud";

    private static final String APP_PREFERENCES = "preferenses";
    public static final String APP_PREFERENCES_NAME_CALENDAR = "name_calendar";
    public static final String APP_PREFERENCES_ID_CALENDER = "id_calender";
    public static final String APP_PREFERENCES_CheckBox = "checkBox_calender";
public  static  final  String  APP_PREFERENCES_START_WORK_HOUR = "start_work_hour";
    public  static  final  String APP_PREFERENCES_START_WORK_MINITS = "start_work_minits";
    public  static  final  String  APP_PREFERENCES_FINISH_HOUR = "ffinish_hour";
    public  static  final  String  APP_PREFERENCES_FINISH_MINUTES ="finish_minutes";
    public  static  final  String  APP_PREFERENCES_WORK_INTERVAL ="work_interval";
    public  static  final  String  APP_PREFERENCES_TEMPLETES_NOW = "app_preferenses_templetes_now";
    public  static  final  String  APP_PREFERENCES_TEMPLETESNOTIFICATION = "app_preferenses_templetes_notification";
    public  static  final  String  APP_PREFERENCES_TEMPLETES_READ = "app_preferenses_templetes_read";
    public  static  final  String  APP_PREFERENCES_TEMPLETES_DELETE = "app_preferenses_templetes_delete";
public  static  final  String APP_PREFERENSES_CHECK_SMS_NOTIFICATION_1 = "check_SMS_notification_1";
//Запоминает текст за каое время отправлять смс
public  static  final String STRING_SMS_NOTIFICATION_HOUR = "   stringSMSHour";
// часы за сколько отправлять смс
    public  static final  String COUNT_HOUR = "countHour";
//Время отправки SMS уведомлений когда не зависимости сеанса
public  static  final  String TIME_SMS_NOTIFICATION = "time_sms_notification";
public  static  final String APP_PREFERENSES_CHECK_LINEPHONE ="line_phone";
public  final  static  String APP_PREFERENSES_CHECK_AUTO_IMPORT = "auto_import";
//Булевое разрешение пересечения записи
public  static  final  String APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD ="checkBoxSettingIntersectionRecod";
//разрешение облачного экспорта по расписанию
public static final String SET_CHECK_BOX_EXPORT_schedule = "set_export_cloud_schedule";


    public  static SharedPreferences  getSharedPreferences (Context context) {
        return   context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }
    public  static  void set (Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }
public  static  void  set (Context context, String key, int value) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putInt(key, value);
    editor.apply();
}

    public  static  void  set (Context context, String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
editor.putLong(key, value);
editor.apply();
    }

public  static  void  set (Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
}

public  static  int getInt (Context context, String key, int other) {
        return  getSharedPreferences(context).getInt(key, other);
}

public  static Long getLong (Context context,  String key, long other) {
        return  getSharedPreferences(context).getLong(key, other);
    }

    public  static  String getString (Context context, String key, String other) {
        return  getSharedPreferences(context).getString(key, other);
    }

    public  static  boolean getBoolean (Context context, String key, boolean other) {
        return  getSharedPreferences(context).getBoolean(key, other);
    }


    }
