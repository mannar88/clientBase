package ru.burdin.clientbase.analytics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.burdin.clientbase.models.Record;

public class Analytics {

    public  static List <Record> listRecords (List <Record> records, long userId) {
    List <Record> result = new ArrayList<>();
        for (Record record : records) {
            if (record.getIdUser() == userId) {
                result.add(record);
            }
        }
return  result;
    }

    public  static  List<List<Record>> numberOfCourses (List <Record> records) {
        List <List<Record>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        int index = 0;
        for (Record record:records) {
    if (record.getOneLine() == 1){
        index++;
        result.add(new ArrayList<>());
    }
            result.get(index).add(record);
}
        return  result;
    }

    public  static int [] placeOnTheList (List <Record> records,long userId, long recordId) {
        int [] result = new  int[4];
        List <Record> recordList =listRecords(records, userId);
        List <List<Record>> lists =numberOfCourses(recordList);
        result[3] = recordList.size();
        for (int i = 0; i < recordList.size() ; i++) {
            if (recordId == recordList.get(i).getId()){
            result[2] = i +1;
            }
        }
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size(); j++) {
                if (lists.get(i).get(j).getId() == recordId) {
                    result[0] = j + 1;
                    result[1] = i + 1;
                    break;
                }
            }
        }

        return  result;
    }
}
