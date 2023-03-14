package ru.burdin.clientbase.cards.cardClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Transaction;

public class CardClient {

    private  CardClientActivity cardClientActivity;
    public   CardClient(CardClientActivity cardClientActivity) {
        this.cardClientActivity = cardClientActivity;
    }

public  void  getHistoryTransactions () {
cardClientActivity.textViewInfoRecords.setOnLongClickListener(new View.OnLongClickListener(){

    @Override
    public boolean onLongClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cardClientActivity);
        List<Transaction> t     = Transaction.getTransactions(new Date().getTime(), cardClientActivity.user.getId(), cardClientActivity.bd);
t.sort(Comparator.reverseOrder());
        String[] strings = new  String[t.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = t.get(i).toString() + ". Остаток на балансе: " + StaticClass.priceToString(Transaction.getAllSumma(t.get(i).getDate(), cardClientActivity.user.getId(), cardClientActivity.bd));
        }
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
builder.create().show();
        return true;
    }
});
}
}

