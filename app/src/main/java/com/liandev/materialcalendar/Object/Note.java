package com.liandev.materialcalendar.Object;
import java.util.Date;

public class Note {
    
    public String name;
    public String description;
    public String secondary_description;
    public Date creation_date;
    
    public Note(String _name, String _description, String _secondary_description, Date _creation_date){ 
     name = _name;
     description = _description;
     secondary_description = _secondary_description;
     creation_date = _creation_date;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
       return description;
    }
    
   public String getSecondaryDescription(){
       return secondary_description;
    } 
    
    public Date getCreationDate(){
       return creation_date;
    } 
}    

