package com.kjt.android.calendar_vacation;

/**
 * Created by welgate_kjt on 2017-04-07.
 */

public class DateClass {
    int day;
    int dayofWeek;

    public DateClass(int d, int h){
        day = d;
        dayofWeek = h;
    }

    public int getDay(){
        return day;
    }

    public int getDayofweek(){
        return dayofWeek;
    }
}
