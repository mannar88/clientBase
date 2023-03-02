package ru.burdin.clientbase.lits.listClient;

import android.content.Context;
import android.view.View;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.analytics.Analytics;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;
import ru.burdin.clientbase.setting.Preferences;

public class ListClient {

private Map<String, List <User>>userMap  = new HashMap<>();
private  Map <String, List<User>> mapFiltr = new HashMap<>();
private Bd bbd;
public   ListClient(List <User> users, List <Record> records, Bd bd)
{
this.bbd = bd;
    this.userMap.put("Сортировка по алфавиту", users);
this.userMap.put("Сортировка  по сеансам", sortSession(users, records));
this.mapFiltr.put("Фильтр: Показать всё", users);
    this.mapFiltr.put("Фильтр: показать с отрицательным балансом", saldoDef(users));
this.mapFiltr.put("Фильтр: показать с положительным балансом", saldoPlus(users));
}
/*
Фильтр с положительным балансом
 */
    private List<User> saldoPlus(List<User> users) {
        List <User> result = new ArrayList<>();
        for (User user:users) {
            if (user.saldo(Analytics.listRecords(bbd.getRecords(),user.getId())) > 0.0) {
                result.add(user);
            }
        }
        return  result;
    }

    /*
    ПОтдает ключи от сортировки
     */
public  String[] getKeysString() {
    return  userMap.keySet().toArray(new String[userMap.size()]);
}

/*
отдает ключи фильтра
 */
public  String[] keysFiltr() {
    return  mapFiltr.keySet().toArray(new  String[mapFiltr.size()]);
}

    public  List <User> getListUsers (String key) {
    return  userMap.get(key);
}

/*
Отдает значение фильтра по ключу
 */
public  List <User> getMapFiltrVelues(String key, String sort) {
    this.mapFiltr.put("Фильтр: Показать всё", userMap.get(sort));
    this.mapFiltr.put("Фильтр: показать с отрицательным балансом", saldoDef(userMap.get(sort)));
    this.mapFiltr.put("Фильтр: показать с положительным балансом", saldoPlus(userMap.get(sort)));
    return  mapFiltr.get(key);
}

    /*
Сортировка по сеансам
 */

private  List <User> sortSession (List <User> users, List <Record> records) {
List <User> result = new ArrayList<>();
records.sort(Comparator.reverseOrder());
for (Record record:records) {
int index = StaticClass.indexList(record.getIdUser(), users);
if (index > -1 && !result.contains(users.get(index))) {
    result.add(users.get(index));
}
}
for (User user:users) {
    if (!result.contains(user)) {
        result.add(user);
    }
}
return  result;
}

private  List <User> saldoDef (List<User> users) {
    List <User> result = new ArrayList<>();
    for (User user:users) {
        if (user.saldo(Analytics.listRecords(bbd.getRecords(),user.getId())) < 0.0) {
            result.add(user);
        }
    }
return  result;
}
/*
установка доступности оплаты
 */
    public void visibilityPay(Context context, Spinner filtr) {
    int acsees = Preferences.getBoolean(context, Preferences.SET_CHECK_BOX_PAY, true)? View.VISIBLE:View.GONE;
    filtr.setVisibility(acsees);
    }
}
