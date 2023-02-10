package ru.burdin.clientbase.lits.listClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.models.Record;
import ru.burdin.clientbase.models.User;

public class ListClient {

private Map<String, List <User>>userMap  = new HashMap<>();

public   ListClient(List <User> users, List <Record> records)
{
this.userMap.put("Сортировка по алфавиту", users);
this.userMap.put("Сортировка  по сеансам", sortSession(users, records));
}

public  String[] getKeysString() {
    return  userMap.keySet().toArray(new String[userMap.size()]);
}

public  List <User> getListUsers (String key) {
    return  userMap.get(key);
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
}
