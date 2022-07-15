package com.liandev.materialcalendar.Object;

import java.io.Serializable;

public class Week implements Serializable {

    public int startDay;
    public int endDay;
    public String fullWeekNumbers;

    public Week(int _startDay, int _endDay, String month) {
        startDay = _startDay;
        endDay = _endDay;
        fullWeekNumbers = startDay + "-" + endDay + " " + month;
    }

    public String getFullWeekNumbers() {
        return fullWeekNumbers;
        }
        
    public int getStartDay(){
        return this.startDay;
    }  
     public int getEndDay(){
        return this.endDay;
    }  
}
