package ru.burdin.clientbase.setting;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.R;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;

public class CalendarSetting {

    private static CalendarSetting calendarSetting;
    public long id = 0;
    private String name;
    private Map<String, Long> calendars = new LinkedHashMap<>();
    private Context context;
    private boolean checkBoxCalender;
    public   final static  String EMPTY = "Не найдены доступные календари";
    private Bd bd;
    public   static final int Calender_PERMISSION = 2;


    private CalendarSetting(Context context) {
        this.context = context;
        if        (requestSinglePermission()) {
            initCalendars();
        }

        if (calendars.size() == 0) {
            calendars.put(EMPTY, 0l);
        }
            id = Preferences.getLong(context, Preferences.APP_PREFERENCES_ID_CALENDER, 0);
            name = Preferences.getString(context, Preferences.APP_PREFERENCES_NAME_CALENDAR, EMPTY);
            checkBoxCalender = Preferences.getBoolean (context, Preferences.APP_PREFERENCES_CheckBox, false) && requestSinglePermission();

        bd = Bd.load(context);
    }

    /*
    Инициализация объекта
     */
    public static CalendarSetting load(Context context) {
        if (calendarSetting == null) {
            calendarSetting = new CalendarSetting(context);
        }
        return calendarSetting;
    }

    public boolean getCheckBox() {
        return checkBoxCalender;
    }

    /*
    Получаем список календарей
     */
    private void initCalendars()  {
        AsyncTasCalender asyncTasCalender =new AsyncTasCalender();
calendars.clear();
                Supplier <Long> supplier = new Supplier<Long>() {
    @Override
    public Long get() {
                String[] projection =
                        new String[]{

                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.NAME,
                                CalendarContract.Calendars.ACCOUNT_TYPE};
                Cursor calCursor = context.getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " > 400",
                                null,
                                null);

                        if (calCursor.moveToFirst()) {
              do {
                calendars.put(calCursor.getString(1) + "", calCursor.getLong(0));
            } while (calCursor.moveToNext());
        }
            calCursor.close();
    return  null;
    }
        };
        asyncTasCalender.execute(supplier);
        try {
            asyncTasCalender.get();
        if (calendars.size() == 0) {
            calendars.put(EMPTY, 0l);
        }
        } catch (ExecutionException e) {
            Toast.makeText(context.getApplicationContext(), "Что-то пошло не так с запросом календарей", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(context.getApplicationContext(), "Что-то пошло не так с запросом календарей", Toast.LENGTH_SHORT).show();
            }
    }


    /*
Получаем список имен календарей
 */
    public Set<String> getNameCalendar() {
                return calendars.keySet();
    }

    /*
    Выбираем календарь для синхронизации
     */
    public void listenCSpinner(Spinner spinner, List <String> namesCalender) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
name = namesCalender.get(i);
id = calendars.get(name);
Preferences.set(context, Preferences.APP_PREFERENCES_ID_CALENDER, id);
                Preferences.set(context, Preferences.APP_PREFERENCES_NAME_CALENDAR, name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setCheckBoxCalender(boolean checkBoxCalender) {
        this.checkBoxCalender = checkBoxCalender;
    }

    /*
        Сохраненный выбор календаря
         */
    public int indexSave(List<String> list) {
        int result = 0;
            result = list.indexOf(name);
        return result;
    }

    /*
    Слушатель флажка календаря
     */
    public void listenChexBox(CheckBox checkBox, Spinner spinner, Activity calenderAxtivity) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                spinner.setEnabled(b);
                checkBoxCalender = b && requestSinglePermission();
                if (!checkBoxCalender) {
    String [] permissions = new  String[] {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
calenderAxtivity.requestPermissions(permissions, Calender_PERMISSION);
}else {
                    if (b) {
                        initCalendars();

                        calenderAxtivity.recreate();
                    name = calendars.keySet().stream().findFirst().get();
                    id = calendars.get(name);
                    Preferences.set(context, Preferences.APP_PREFERENCES_NAME_CALENDAR, name);
                    Preferences.set(context,Preferences.APP_PREFERENCES_ID_CALENDER, id);
                    }else  {
                        spinner.setEnabled(b);
                    }

                }

                Preferences.set(context, Preferences.APP_PREFERENCES_CheckBox, checkBoxCalender);
            }
        });
    }

    /*
    Добавить событие в календарь
     */
    public long addRecordCalender (Record record, String surname) {
        long result = -2;
        if (checkBoxCalender && requestSinglePermission() && id != 0) {
            AsyncTasCalender asyncTasCalender = new AsyncTasCalender();
            Supplier<Long> supplier = new Supplier<Long>() {
                @Override
                public Long get() {
                    Uri uri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, getContentValues(record, surname));
                    return Long.parseLong(uri.getLastPathSegment());
                }
            };
            asyncTasCalender.execute(supplier);
            try {
                result = asyncTasCalender.get(1, TimeUnit.DAYS.SECONDS);
            } catch (ExecutionException e) {
                Toast.makeText(context.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (InterruptedException | TimeoutException e) {
                Toast.makeText(context.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }        return  result;
    }

    /*
    Редактирование события календаря
     */
    public int update(Record record, String nameSurname) {
        long result = -2;
        if (checkBoxCalender && requestSinglePermission() && id !=0) {
            AsyncTasCalender asyncTasCalender = new AsyncTasCalender();
            Supplier<Long> supplier = new Supplier<Long>() {
                @Override
                public Long get() {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, record.getEvent_id());
                    return (long) context.getContentResolver().update(uri, getContentValues(record,nameSurname ), null, null);
                }
            };
            asyncTasCalender.execute(supplier);
            try {
                result = asyncTasCalender.get(1, TimeUnit.DAYS.SECONDS);
            } catch (ExecutionException e) {
                Toast.makeText(context.getApplicationContext(), "Поток с изменением записи календаря закрылся не корректно", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException | TimeoutException e) {
                Toast.makeText(context.getApplicationContext(), "Поток с изменением записи календаря закрылся не корректно", Toast.LENGTH_SHORT).show();
            }
        }
        return  (int)result;
    }

    /*
удаляет запись в календаре
 */
    public int delete(long id) {
        long result = -2;
        if (checkBoxCalender && requestSinglePermission() && id !=0) {

            AsyncTasCalender asyncTasCalender = new AsyncTasCalender();
            Supplier<Long> supplier = new Supplier<Long>() {
                @Override
                public Long get() {
                    Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
                    return (long) context.getContentResolver().delete(deleteUri, null, null);
                }
            };
            asyncTasCalender.execute(supplier);
            try {
                result = asyncTasCalender.get(1, TimeUnit.DAYS.SECONDS);
            } catch (ExecutionException e) {
                Toast.makeText(context.getApplicationContext(), "Поток с удалением записи календаря закрылся не корректно", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException | TimeoutException e) {
                Toast.makeText(context.getApplicationContext(), "Поток с удалением записи календаря закрылся не корректно", Toast.LENGTH_SHORT).show();
            }
        }
        return  (int) result;
}
    /*
    создает и наполняет contentValues
     */
    private ContentValues getContentValues(Record record, String nameSurname) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.DTSTART, record.getStart());
        contentValues.put(CalendarContract.Events.DTEND, record.getStart() + record.getEnd());
        contentValues.put(CalendarContract.Events.TITLE, context.getResources().getString(R.string.app_name) + " " +nameSurname + " " + record.getProcedure());
        contentValues.put(CalendarContract.Events.DESCRIPTION, "");
        contentValues.put(CalendarContract.Events.CALENDAR_ID, this.id);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());

        return contentValues;
    }

    /*
Проверка разрешения
 */
    private  boolean requestSinglePermission() {

        String calenderPermission = Manifest.permission.READ_CALENDAR;
        String calenderPermission1 = Manifest.permission.WRITE_CALENDAR;

        int hasPermission =context.checkSelfPermission(calenderPermission);
        int hasPermission2 =context.checkSelfPermission(calenderPermission1);
        return hasPermission  == PackageManager.PERMISSION_GRANTED && hasPermission2 == PackageManager.PERMISSION_GRANTED;
    }

    private class AsyncTasCalender extends AsyncTask<Supplier<Long>, Void,Long> {

        @Override
        protected Long doInBackground(Supplier<Long>... suppliers) {

            return suppliers[0].get();
        }
    }

            public  void getDialog (Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Нет разрешения чтение и записи календаря! Те чё, западло разрешить?");
                builder.setPositiveButton("Нет, не западло, сейчас разрешу",   new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                intent.setData(uri);
                                context.startActivity(intent);

                            }
                        }
                );
                builder.setNegativeButton("Не разрешу, мало ли чё там в коде хакерского зарыто", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        }
