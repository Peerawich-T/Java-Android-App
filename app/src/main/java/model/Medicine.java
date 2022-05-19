package model;

import org.w3c.dom.CDATASection;

import java.util.ArrayList;

public class Medicine {
    private String time;
    private String name;
    private String expiredDate;
    private int remainValue;
    private int dosagePerOneTake;
    private ArrayList<String> day ;

    public Medicine(){
        time = "time";
        name = "name";
        expiredDate = "expired";
        remainValue =0;
        dosagePerOneTake =0;
        day = new ArrayList<String>();

    }

    public Medicine(String name, String expiredDate, int remainValue, int dosagePerOneTake, ArrayList<String> date,String time) {
        this.name = name;
        this.dosagePerOneTake = dosagePerOneTake;
        this.expiredDate = expiredDate;
        this.remainValue = remainValue;
        this.day = date;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDosagePerOneTake() {
        return dosagePerOneTake;
    }

    public void setDosagePerOneTake(int dosagePerOneTake) {
        this.dosagePerOneTake = dosagePerOneTake;
    }

    public ArrayList<String> getDay() {
        return day;
    }

    public void setDay(ArrayList<String> date) {
        this.day = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getRemainValue() {
        return remainValue;
    }

    public void setRemainValue(int remainValue) {
        this.remainValue = remainValue;
    }
}
