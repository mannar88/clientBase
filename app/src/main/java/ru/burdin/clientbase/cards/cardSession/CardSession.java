package ru.burdin.clientbase.cards.cardSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.analytics.Analytics;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;

class CardSession {

    private CardSessionActivity activity;
    private Bd bd;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-YYYY, EEEE");
    private int indexUser;

    public CardSession(CardSessionActivity activity, Bd bd) {
        this.activity = activity;
        this.bd = bd;
    }

    /*
    Исчет индекс юзера
     */
    public int getIndexUser(Record record) {
        return StaticClass.indexList(record.getIdUser(), bd.getUsers());
    }

    /*
     Отслеживает оплату
      */
    public void pay(Record record) {
activity.checkBoxPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ContentValues contentValues = new ContentValues();
    contentValues.put(Bd.COLUMN_PAY, b? record.getPrice():0.0);
    if (bd.update(Bd.TABLE_SESSION, contentValues, record.getId(), Preferences.getBoolean(activity.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(activity.getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false)) == 1) {
        record.setPay(b? record.getPrice():0.0);
    setScreenInfo(record);
    }
    }
});
        }

    /*
   Устанавливает информацию на экран
    */
    public void setScreenInfo(Record record) {
        int indexUser = getIndexUser(record);
        User user = bd.getUsers().get(indexUser);
        int[] arr = Analytics.placeOnTheList(bd.getRecords(), user.getId(), record.getId());
        activity.textViewDate.setText("Время записи: " + dateFormat.format(new Date(record.getStart())));
        activity.textViewNameUser.setText("Клиент: " + user.getSurname() + " " + user.getName() + " Нажмите, что бы открыть карточку клиента.");
        activity.textViewProcedure.setText("Услуги: " + record.getProcedure());
        activity.textViewPrice.setText("Стоимость: " + StaticClass.priceToString(record.getPrice()));
        activity.checkBoxPay.setChecked(record.getPay() == record.getPrice()? true:false);
        activity.checkBoxPay.setText(activity.checkBoxPay.isChecked()? "Оплачено":"Оплатить?");
        activity.textViewTimeEnd.setText("Продолжительность услуги: " + TimeUnit.MILLISECONDS.toMinutes(record.getEnd()) + " минут");
        activity.textViewComment.setText("Комментарии: " + record.getComment());
        activity.textViewPlaceOnTheList.setText("Сеанс в курсе: " + arr[0] + ", курс: " + arr[1] + ", всего: " + arr[2] + " из " + arr[3]);
        activity.checkBoxPlaceOnTheList.setChecked(StaticClass.intInBoolean(record.getOneLine()));
        if (arr[2] == 1) {
            activity.checkBoxPlaceOnTheList.setChecked(true);
            activity.checkBoxPlaceOnTheList.setEnabled(false);
        }
        activity.checkBoxNotNotification.setChecked(StaticClass.longInBoolean(record.getNotNotification()));
    }

    /*
    установка доступности оплаты
     */
    public void visibilityPay() {
        int acsees = Preferences.getBoolean(activity.getApplicationContext(), Preferences.SET_CHECK_BOX_PAY, true) ? View.VISIBLE : View.GONE;
activity.checkBoxPay.setVisibility(acsees);
    }

    /*
    Пакетное удаление
     */
    public void groupDelete() {
    activity.buttonDelete.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            List <Record> records = Analytics.listRecords(bd.getRecords(), activity.record.getIdUser());
records.sort(Comparator.reverseOrder());
            String[] strings = new  String[records.size()];
            boolean [] booleans = new  boolean[records.size()];
            DateFormat dateFormat = new SimpleDateFormat("EEEE, dd-MM-YYYY HH:mm");
            for (int i = 0; i < records.size(); i++) {
strings[i] = dateFormat.format(records.get(i).getStartDay())  + ", " + records.get(i).getProcedure() + ", " + String.valueOf(records.get(i).getPrice());
            }
            List <Record> deleteRecords = new ArrayList<>(records.size());
            builder.setMultiChoiceItems(strings, booleans, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
if (b) {
deleteRecords.add(records.get(i));
}else {
    deleteRecords.remove(records.get(i));
}
                }
            });
            builder.setNegativeButton("Удалить выбранное", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
int result = 0;
                if (deleteRecords.size() > 0) {
                    for (Record record:deleteRecords) {
                        result = result +bd.delete(Bd.TABLE_SESSION, record.getId(), false, false);
                    activity.calendarSetting.delete(record.getEvent_id());
                    }
if (result == deleteRecords.size()) {
    bd.getRecords().removeAll(deleteRecords);
Toast.makeText(activity.getApplicationContext(), "Записи удалены", Toast.LENGTH_SHORT).show();
    activity.finish();
bd.autoExport(1l, Preferences.getBoolean(activity.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false), Preferences.getBoolean(activity.getApplicationContext(), Preferences.SET_CHECK_VOX_AUTO_EXPORT_BD, false));
}
                }
                }
            });
            builder.setPositiveButton("Выбрать все", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (int j = 0; j < booleans.length; j++) {
                        booleans[j] = true;
                         }
                    deleteRecords.addAll(records);
builder.create().show();
                }
            });
            builder.create().show();
            return true;
        }
    });
    }
}

