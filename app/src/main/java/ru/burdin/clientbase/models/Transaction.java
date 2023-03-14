package ru.burdin.clientbase.models;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.burdin.clientbase.Bd;
import ru.burdin.clientbase.StatActivity;
import ru.burdin.clientbase.StaticClass;
import ru.burdin.clientbase.analytics.Analytics;

public class Transaction implements  Comparable{

    private  long date;
private  double summa;
private  String comment;

public  void  setDate(long date) {
    this.date = date;
}
public  void  setSumma (double summa) {
    this.summa = summa;
}

    public long getDate() {
        return date;
    }

    public double getSumma() {
        return summa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (date != that.date) return false;
        if (Double.compare(that.summa, summa) != 0) return false;
        return comment != null ? comment.equals(that.comment) : that.comment == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (date ^ (date >>> 32));
        temp = Double.doubleToLongBits(summa);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    public String getComment() {
        return comment;
    }

    public  void  setComment (String comment) {
    this.comment = comment;
}

/*
получает список всех транзакций конкретного пользователя
 */
public  static List <Transaction> getTransactions (Long date, long userId, Bd bd) {
    List <Transaction> transactions = new ArrayList<>();
    List <Record> records = Analytics.listRecords(bd.getRecords(), userId);
for (Record record:records) {
    if (record.getStart() <= date) {
        if (record.getPrice() > 0.0) {
            Transaction transaction = new Transaction();
            transaction.setDate(record.getStart());
            transaction.setSumma(record.getPrice() * -1);
            transaction.setComment("списание за " + record.getProcedure());
            transactions.add(transaction);
        }
    }
if ((record.getStart() + record.getEnd()) <= date){
    if (record.getPay() > 0.0) {
    Transaction transaction = new Transaction();
    transaction.setDate((record.getStart() + record.getEnd()));
    transaction.setSumma(record.getPay());
    transaction.setComment("Пополнение счета за " + record.getProcedure());
    transactions.add(transaction);
    }
    }
}
return  transactions;
}

/*
Подсчитывает общий баланс клиента
 */
public  static  double getAllSumma (long date, long userId, Bd bd) {
    double result = 0.0;
    List <Transaction> transactions = getTransactions(date, userId, bd);
    for (Transaction transaction:transactions) {
        result += transaction.getSumma();
    }
return  result;
}

@Override
    public int compareTo(Object o) {
Transaction transaction = (Transaction) o;
        return Long.compare(this.date, transaction.date);
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
String result = "";
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY, HH:mm");
result = dateFormat.format(this.date) + ", " + this.comment + " " + StaticClass.priceToString(summa);
        return  result;
    }
}
