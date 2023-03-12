package ru.burdin.clientbase.lits.litsSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.notificationSMS.SendSMS;
import ru.burdin.clientbase.setting.Preferences;

import static java.lang.Thread.sleep;

public class ListSession {

  private   ListSessionActivity activity;

  public  ListSession (ListSessionActivity activity) {
    this.activity = activity;
}

/*
Вызывает меню текущего дня
 */
public  void  textViewMenu () {
    activity.textViewTime.setOnLongClickListener(new View.OnLongClickListener() {
                                                     @Override
                                                     public boolean onLongClick(View view) {
                                                         AlertDialog.Builder builder = new  AlertDialog.Builder(activity);
                                                         String [] strings = new  String[] {"Отправить всем напоминалку", "Очистить расписание"};
                                                         builder.setItems(strings, new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialogInterface, int i) {
switch (i) {
    case 0:
        allNotifacation();
        break;
    case  1:
        allDeleteRecords();
        break;
}
                                                             }
                                                         });
                                                         builder.create().show();

                                                         return true;
                                                     }
                                                 }
    );
}

/*
Очищает в выбранный расписание
 */
    private void allDeleteRecords() {
AlertDialog.Builder builder = new AlertDialog.Builder(activity);
builder.setNeutralButton("Очистить без уведомления", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
    deleteAndNotification(-1);
    }
});
builder.setPositiveButton("Уведомить через SMS", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
deleteAndNotification(0);
    }
});
builder.setNegativeButton("Уведомить через  Whatsapp", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
deleteAndNotification(1);
    }
});
builder.create().show();
    }

    /*
    удаляет и уведомляет
     */
    private void deleteAndNotification(int i) {
        List<Record> recordList = new ArrayList<>();
        for (Record record:activity.recordsEnpty) {
            if (record.getId() > 0) {
                if (activity.bd.delete(Bd.TABLE_SESSION, record.getId(), false, false) == 1) {
activity.calendarSetting.delete(record.getEvent_id());
                    activity.bd.getRecords().remove(record);
        recordList.add(new Record(record));
                }
            }
        }
        activity.recUpdate();
        activity.bd.autoExport(1l, Preferences.getBoolean(activity.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(activity.getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
if (recordList.size() > 0 && i == 0) {
    Thread thread = new Thread(() ->
    recordList.forEach(this::accept
    ));
thread.start();
}
    if (recordList.size() > 0 && i == 1) {
        recordList.forEach( record ->
                SendSMS.send(activity, Preferences.getString(activity, SendSMS.KEY_PREFERENSES.get(3), SendSMS.TEMPLETS.get(3)), record, R.id.radioButtonAddSessionWAthsApp)
        );
    }
    }

    /*
    Отправка всем напоминалки
     */
    private void allNotifacation() {
AlertDialog.Builder builder = new AlertDialog.Builder(activity);
builder.setNegativeButton("SMS", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        for (Record record : activity.recordsEnpty) {
            if (record.getIdUser() > 0 && record.getNotNotification() == 0) {
                SendSMS.send(activity, Preferences.getString(activity, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionSMS);
                try {
                    sleep(TimeUnit.MINUTES.toMillis(Preferences.getInt(activity.getApplicationContext(), Preferences.PAUSE_SMS, 0)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
};
Thread thread = new Thread(runnable);
thread.start();
    }
});
builder.setPositiveButton("Watsapp", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        for (Record record:activity.recordsEnpty) {
            if (record. getIdUser() > 0 ) {
                SendSMS.send(activity, Preferences.getString(activity, SendSMS.KEY_PREFERENSES.get(1), SendSMS.TEMPLETS.get(1)), record, R.id.radioButtonAddSessionWAthsApp);
            }
        }

    }
});
    builder.create().show();
    }

    private void accept(Record record) {
        SendSMS.send(activity, Preferences.getString(activity, SendSMS.KEY_PREFERENSES.get(3), SendSMS.TEMPLETS.get(3)), record, R.id.radioButtonAddSessionSMS);
        try {
            sleep(TimeUnit.MINUTES.toMillis(Preferences.getInt(activity.getApplicationContext(), Preferences.PAUSE_SMS, 0)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

