package com.kjt.android.calendar_vacation;

/**
 * Created by welgate_kjt on 2017-04-07.
 */

public class DateClass {
    int day;
    int dayofWeek;
    int year, month;
    String sMonth, sDay;

    public DateClass(int d, int h, int y, int m){
        day = d;
        dayofWeek = h;
        year = y;
        month = m;
    }

    public int getDay(){
        return day;
    }

    public int getDayofweek(){
        return dayofWeek;
    }

    public String getFullDate (){

        if(month < 10){
            sMonth = "0"+month;
        }else{
            sMonth = ""+month;
        }

        if(day < 10){
            sDay = "0"+day;
        }else{
            sDay = ""+day;
        }

        return year+"-"+sMonth+"-"+sDay;
    }
}
