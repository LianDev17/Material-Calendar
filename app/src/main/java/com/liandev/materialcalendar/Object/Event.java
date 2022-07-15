package com.liandev.materialcalendar.Object;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    
    public String id;
    public String name;
    public String description;
    public Date start_date;
    public Date end_date;
    public Date creation_date;
    
    public Event(String _id, String _name, String _description, Date _start_date, Date _end_date, Date _creation_date){ 
     id = _id;   
     name = _name;
     description = _description;
     start_date = _start_date;
     end_date = _end_date;
   //creation_date = _creation_date;
    }
    
    public String getId(){ 
        return id;
    }
    
    public String getName(){ 
        return name;
    }
    
    public String getDescription(){
       return description;
    }
    
    public Date getStartDate(){
       return start_date;
    }   
    
    public Date getEndDate(){
       return end_date;
    }   
    
    public Date getCreationDate(){
       return creation_date;
    }   
  
    public int getViewType(){
       return 2;
   }
}