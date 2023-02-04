package ru.burdin.clientbase.lits.actionListSassion;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.setting.CalendarSetting;
import ru.burdin.clientbase.setting.Preferences;

public class DoubleSession implements Consumer<Record> {

    private Context context;
    private  Bd bd;
    private int indexListRecord;
    private CalendarSetting calendarSetting;
public   static boolean checkDouble;


    public  DoubleSession (Context context, int indexListRecord,  CalendarSetting calendarSetting) {
        this.context = context;
        try {
            bd = Bd.load(context);
        } catch (InterruptedException e) {
            Toast.makeText(context.getApplicationContext(), "Не удалось открыть базу данных  №1" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context.getApplicationContext(), "Не удалось открыть базу данных  №2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (TimeoutException e) {
            Toast.makeText(context.getApplicationContext(), "Не удалось открыть базу данных  №3" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        this.indexListRecord = indexListRecord;
    this.calendarSetting = calendarSetting;
    }

    @Override
    public void accept(Record record) {
        Record recordDup = new Record();
        recordDup.setStart(record.getStart());
        recordDup.setEnd(bd.getRecords().get(indexListRecord).getEnd());
        recordDup.setIdUser(bd.getRecords().get(indexListRecord).getIdUser());
        recordDup.setProcedure(bd.getRecords().get(indexListRecord).getProcedure());
        recordDup.setPrice(bd.getRecords().get(indexListRecord).getPrice());
        recordDup.setComment(bd.getRecords().get(indexListRecord).getComment());
        if (!bd.getRecords().contains(recordDup) || Preferences.getBoolean(context.getApplicationContext(), Preferences.APP_PREFERENSES_CHECKBOX_IN_TERSECTIONRECOD, false)) {
            String surnameAndName = bd.getUsers().get(StaticClass.indexList(recordDup.getIdUser(), bd.getUsers())).getSurname() + " " + bd.getUsers().get(StaticClass.indexList(recordDup.getIdUser(), bd.getUsers())).getName();
            recordDup.setEvent_id(calendarSetting.addRecordCalender(recordDup, surnameAndName));
            ContentValues contentValues = new ContentValues();
            contentValues.put(Bd.COLUMN_TIME, recordDup.getStart());
            contentValues.put(Bd.COLUMN_TIME_END, recordDup.getEnd());
            contentValues.put(Bd.COLUMN_ID_USER, recordDup.getIdUser());
            contentValues.put(Bd.COLUMN_PROCEDURE, recordDup.getProcedure());
            contentValues.put(Bd.COLUMN_PRICE, recordDup.getPrice());
            contentValues.put(Bd.COLUMN_COMMENT, recordDup.getComment());
            contentValues.put(Bd.COLUMN_EVENT_ID, recordDup.getEvent_id());
            long id = bd.add(Bd.TABLE_SESSION, contentValues);
            if (id > 0) {
                if (bd.getRecords().add(new Record(
                        id,
                        recordDup.getStart(),
                        recordDup.getEnd(),
                        recordDup.getIdUser(),
                        recordDup.getProcedure(),
                        recordDup.getPrice(),
                        recordDup.getComment(),
                        recordDup.getEvent_id(),
0,
0
                ))) {
checkDouble= true;
                }
            }
        } else {
            Toast.makeText(context.getApplicationContext(), "Запись пересекаетсяс другим клиентом", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isCheckDouble() {
        return checkDouble;
    }

}
