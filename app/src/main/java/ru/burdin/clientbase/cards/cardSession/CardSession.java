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
import android.widget.Toast;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;

class CardSession {

     private CardSessionActivity activity;
private Bd bd;

public  CardSession (CardSessionActivity activity, Bd bd) {
         this.activity = activity;
this.bd = bd;
}

     /*
     Отслеживает оплату
      */
     public  void  pay (Record record) {
          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          LayoutInflater inflater = activity.getLayoutInflater();

          View viewPay = inflater.inflate(R.layout.pay, null);
          EditText editTextPay = viewPay.findViewById(R.id.editTextCardSessionPay);
          Button buttonPay = viewPay.findViewById(R.id.buttonCardSessionPay);
          editTextPay.setText(StaticClass.priceToString(record.getPay()));
          builder.setView(viewPay);

          Dialog dialog = builder.create();
          buttonlesten(buttonPay, editTextPay,  dialog, record);
          dialog.show();
     }

     private  void  buttonlesten (Button button, EditText editText, Dialog dialog, Record record) {
          button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
double result = 0.0;
                    if (editText.getText().length() == 0) {
     result = result;
}else {
                         result = Double.valueOf(editText.getText().toString());
                    }
ContentValues contentValues = new ContentValues();
                    contentValues.put(Bd.COLUMN_PAY, result);
                    if (bd.update(Bd.TABLE_SESSION, contentValues, record.getId()) == 1) {
                        record.setPay(result);
                        dialog.cancel();
                        Toast.makeText(activity.getApplicationContext(), "Сохранено: " + StaticClass.priceToString(result), Toast.LENGTH_SHORT).show();
                        activity.textViewPay.setText("Оплачено: " + StaticClass.priceToString(record.getPay()) + ", кликнете что-бы изменить");
                    }else {
                        Toast.makeText(activity.getApplicationContext(), "Что-то тут не то...", Toast.LENGTH_SHORT).show();
                    }
                    }
          });
     }
}
