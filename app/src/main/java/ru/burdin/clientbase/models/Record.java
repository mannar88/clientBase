package ru.burdin.clientbase.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Record implements Comparable, Model {
private  long id;
    private  long start;
    private  long end = 0;
    private long idUser;
    private String procedure;
    private double price;
    private  long event_id;

    public long getEvent_id() {
        return event_id;
    }

    public void setEvent_id(long event_id) {
        this.event_id = event_id;
    }

    private String comment;

    public Record() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public  Record (Record record) {
        this.id = record.getId();
        this.idUser = record.getIdUser();
        this.end = record.getEnd();
        this.event_id = record.getEvent_id();
        this.start = record.getStart();
        this.comment = record.getComment();
        this.price = record.getPrice();
        this.procedure = record.getProcedure();
}

    public Record(long start) {
        this.start = start;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Record(long id, long start, long end, long idUser,  String procedure, double price,   String comment, Long event_id) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.idUser = idUser;
        this.procedure = procedure;
        this.price = price;
        this.event_id = event_id;
        this.comment = comment;
    }

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getStartDay() {
        return new Date(start);
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;
boolean res = false;
DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm");
String thisStart = dateFormat.format(this.getStartDay());
String recordStart = dateFormat.format(record.getStartDay());
String thisFinish = dateFormat.format(new Date(this.start + this.end));
String recordFinish = dateFormat.format(new Date(record.start + record.end));
if (
        thisStart.compareToIgnoreCase(recordStart) == 0
||
                (thisFinish.compareToIgnoreCase(recordFinish) < 0
&&
                        thisFinish.compareToIgnoreCase(recordStart)>0
                )
||
                (thisStart.compareToIgnoreCase(recordStart) < 0
                        &&
                        thisFinish.compareToIgnoreCase(recordFinish)>0
                        )) {
    res = true;
}
return  res;
    }


    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        result = 31 * result + (int) (idUser ^ (idUser >>> 32));
        result = 31 * result + (procedure != null ? procedure.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(Object o) {
        o = (Record) o;
        return Long.compare(this.start, ((Record) o).getStart());
    }

}

