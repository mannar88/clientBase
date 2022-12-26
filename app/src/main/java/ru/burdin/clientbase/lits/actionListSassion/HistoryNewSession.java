package ru.burdin.clientbase.lits.actionListSassion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.function.Consumer;

import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.add.AddSessionActivity;
import ru.burdin.clientbase.models.Record;

public class HistoryNewSession implements Consumer<Record> {

private      Context context;
    private  int index ;
private  Activity activity;
    public HistoryNewSession(Context context, int index, Activity activity) {
        this.context = context;
        this.index = index;
    this.activity = activity;
    }

    @Override
    public void accept(Record record) {
        if (record.getId() ==0) {
            Intent intent = new Intent(context.getApplicationContext(), AddSessionActivity.class);
            intent.putExtra(StaticClass.TIMEFREE, record.getStart());
            intent.putExtra(StaticClass.POSITION_LIST_USERS, index);
            context.startActivity(intent);
            activity.finish();
        }else {
            Toast.makeText(context.getApplicationContext(), "Время пересекается с другой записью", Toast.LENGTH_SHORT).show();
        }
    }
}
