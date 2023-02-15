package ru.burdin.clientbase.lits.actionListSassion;

import android.content.Context;
import android.content.Intent;

import java.util.function.Consumer;

import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.cards.cardSession.CardSessionActivity;
import ru.burdin.clientbase.models.Record;

public class BusyTime implements Consumer <Record> {

    Context context;

    public  BusyTime(Context context) {
        this.context = context;
    }
    @Override
    public void accept(Record record) {
        Intent intentCardRecord = new Intent(context, CardSessionActivity.class);
        intentCardRecord.putExtra(StaticClass.POSITION_LIST_RECORDS, record.getId());
        context.startActivity(intentCardRecord);

    }
}
