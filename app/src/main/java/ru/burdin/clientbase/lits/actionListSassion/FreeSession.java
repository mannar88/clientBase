package ru.burdin.clientbase.lits.actionListSassion;

import android.content.Context;
import android.content.Intent;

import java.util.function.Consumer;

import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.models.Record;

public class FreeSession implements Consumer <Record>{
    Context context;
    public  FreeSession (Context context) {
    this.context = context;
    }

    @Override
    public void accept(Record record) {
        Intent intent = new Intent(context, AddSessionActivity.class);
        intent.putExtra(StaticClass.TIMEFREE, record.getStart());
        context.startActivity (intent);

    }
}
