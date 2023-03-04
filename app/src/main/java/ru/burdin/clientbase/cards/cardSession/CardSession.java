package ru.burdin.clientbase.cards.cardSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View viewPay = inflater.inflate(R.layout.pay, null);
        EditText editTextPay = viewPay.findViewById(R.id.editTextCardSessionPay);
        TextView textViewPay = viewPay.findViewById(R.id.textViewCardSessionPay);
        Button buttonPay = viewPay.findViewById(R.id.buttonCardSessionPay);
        editTextPay.setText(StaticClass.priceToString(record.getPay()));
        editTextPay.setSelection(editTextPay.length());
        builder.setView(viewPay);
        Dialog dialog = builder.create();
        double saldo = bd.getUsers().get(getIndexUser(record)).saldo(Analytics.listRecords(bd.getRecords(), record.getIdUser()));
        if (saldo < 0.0) {
            textViewPay.setText("Внести всю сумму долга: " + StaticClass.priceToString(saldo));
            textViewPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editTextPay.setText(StaticClass.priceToString(Math.abs(saldo)));
                }
            });
        }
        buttonlesten(buttonPay, editTextPay, dialog, record);

        dialog.show();
    }

    private void buttonlesten(Button button, EditText editText, Dialog dialog, Record record) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double result = 0.0;
                if (editText.getText().length() == 0) {
                    result = result;
                } else {
                    result = Double.valueOf(editText.getText().toString());
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(Bd.COLUMN_PAY, result);
                if (bd.update(Bd.TABLE_SESSION, contentValues, record.getId(), Preferences.getBoolean(activity.context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECK_AUTO_IMPORT, false)) == 1) {
                    record.setPay(result);
                    dialog.cancel();
                    Toast.makeText(activity.getApplicationContext(), "Сохранено: " + StaticClass.priceToString(result), Toast.LENGTH_SHORT).show();
                    activity.textViewPay.setText("Оплачено: " + StaticClass.priceToString(record.getPay()) + ", кликнете что-бы изменить");
                    activity.textViewSaldo.setText("Общий баланс: " + StaticClass.priceToString(bd.getUsers().get(indexUser).saldo(Analytics.listRecords(bd.getRecords(), record.getIdUser()))));
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Что-то тут не то...", Toast.LENGTH_SHORT).show();
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
        activity.textViewPay.setText("Оплачено: " + StaticClass.priceToString(record.getPay()) + ", кликнете что-бы изменить");
        activity.textViewSaldo.setText("Общий баланс: " + StaticClass.priceToString(bd.getUsers().get(indexUser).saldo(Analytics.listRecords(bd.getRecords(), record.getIdUser()))));
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
        activity.textViewSaldo.setVisibility(acsees);
        activity.textViewPay.setVisibility(acsees);
    }
}

