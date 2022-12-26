package ru.burdin.clientbase.lits.actionListSassion;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import java.util.function.Consumer;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.CalendarSetting;

public class TransferSession implements Consumer<Record> {

    private Context context;
    private  long id;
    private CalendarSetting calendarSetting;
public  static boolean checkTransfer = false;
    private  Bd bd;

    public TransferSession(Context context, long id, CalendarSetting calendarSetting) {
        this.context = context;
        this.id = id;
        this.calendarSetting = calendarSetting;
bd = Bd.load(context);
    }



    @Override
    public void accept(Record record) {
int index = StaticClass.indexList(id, bd.getRecords());
record.setEnd(bd.getRecords().get(index).getEnd());
Record record1 = bd.getRecords().remove(index);
    if (!bd.getRecords().contains(record)) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Bd.COLUMN_TIME, record.getStart());
        contentValues.put(Bd.COLUMN_TIME_END, record.getEnd());
        contentValues.put(Bd.COLUMN_ID_USER,record1.getId());
        contentValues.put(Bd.COLUMN_PROCEDURE, record1.getProcedure());
        contentValues.put(Bd.COLUMN_PRICE, record1.getPrice());
        contentValues.put(Bd.COLUMN_COMMENT, record1.getComment());
if (bd.update(Bd.TABLE_SESSION, contentValues, id) > 0) {
    record1.setStart(record.getStart());
    bd.getRecords().add(record1);
    String surnameAndName = bd.getUsers().get(StaticClass.indexList(record1.getIdUser(), bd.getUsers())).getSurname() + " " + bd.getUsers().get(StaticClass.indexList(record1.getIdUser(), bd.getUsers())).getName();
calendarSetting.update(record1, surnameAndName);
checkTransfer = true;
}else  {
    Toast.makeText(context.getApplicationContext(), "Запись не перенесена", Toast.LENGTH_SHORT).show();
    bd.getRecords().add(record1);
}
    }else {
        Toast.makeText(context.getApplicationContext(), "Запись пересекается с другой записью", Toast.LENGTH_SHORT).show();
    bd.getRecords().add(record1);
    }
    }
}
