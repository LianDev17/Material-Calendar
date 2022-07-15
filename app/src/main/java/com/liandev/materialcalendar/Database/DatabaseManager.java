package com.liandev.materialcalendar.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.content.ContentValues;
import android.app.Activity;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Object.Event;
import com.liandev.materialcalendar.Object.Note;
import java.util.Date;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 7;

    private Context context;

    public DatabaseManager( Context context ) {        
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
        this.context = context;
    }
    
    public SQLiteDatabase getDatabase(){
        return this.getWritableDatabase();
    }        
    
    public void createNewDatabase(){
        
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user = "CREATE TABLE IF NOT EXISTS user ("  
        + "id TEXT NOT NULL,"
        + "welcomeMessage TEXT NOT NULL);";   
        
        String googleUser = "CREATE TABLE IF NOT EXISTS googleUser ("  
        + "id TEXT NOT NULL,"
        + "name TEXT NOT NULL,"
        + "password TEXT NOT NULL);";  
        
             
        String settings = "CREATE TABLE IF NOT EXISTS settings ("
            + "id TEXT NOT NULL,"
            + "dark_mode BOOLEAN NOT NULL,"
            + "remove_outdated_task BOOLEAN NOT NULL,"
            + "google_sync BOOLEAN NOT NULL,"     
            + "google_calendar_sync BOOLEAN NOT NULL,"
            + "google_tasks_sync BOOLEAN NOT NULL);";
            
        String events = "CREATE TABLE IF NOT EXISTS events ("
            + "id TEXT NOT NULL,"
            + "event_title TEXT NOT NULL,"
            + "event_description TEXT NOT NULL,"                  
            + "event_creation_time LONG NOT NULL,"
            + "event_start_time LONG NOT NULL,"
            + "event_end_time LONG NOT NULL);";
            
         String notes = "CREATE TABLE IF NOT EXISTS notes ("
            + "id TEXT NOT NULL,"
            + "note_title TEXT NOT NULL,"
            + "note_description TEXT NOT NULL,"     
            + "note_secondary_description TEXT NOT NULL,"                             
            + "note_creation_time TEXT NOT NULL);";  
            
         String tasks = "CREATE TABLE IF NOT EXISTS tasks ("
            + "id TEXT NOT NULL,"
            + "task_title TEXT NOT NULL,"
            + "task_description TEXT NOT NULL,"                  
            + "task_creation_time TEXT NOT NULL,"
            + "task_start_time LONG NOT NULL,"
            + "task_end_time LONG NOT NULL);";            
 
            
        String createSettings = "insert into settings (id, dark_mode, remove_outdated_task, google_sync, google_calendar_sync, google_tasks_sync) values ('settings', 'false', 'false', 'false', 'false', 'false');";
		String createUser = "insert into user (id, welcomeMessage) values ('user', 'false');";
        String createGoogleUser = "insert into googleUser (id, name, password) values ('googleUser', '', '');";
       // String testEvent = "insert into events (id, event_title, event_description, event_creation_time, event_start_time, event_end_time) values ('test', 'TestEvent', 'Systeme fonctionnel', 0, '1648918547000', '1648978061000');";
		
        db.execSQL( user );    
        db.execSQL( googleUser );       
        db.execSQL( settings ); 
        
        db.execSQL( events );   
        db.execSQL( notes );  
        db.execSQL( tasks ); 
        
        db.execSQL( createSettings );  
        db.execSQL( createUser ); 
        db.execSQL( createGoogleUser ); 
       // db.execSQL( testEvent );
   } 

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
     //  db.execSQL("drop table user; drop table tasks; drop table notes; drop table events; drop table settings");
        this.onCreate( db );
    }

    public Boolean insertItem(String tableName, ContentValues itemData){      
        SQLiteDatabase db = this.getWritableDatabase();   
        long result = db.insert(tableName, null, itemData);       
        
        Log.i("resultInsert","" + result);
        db.close();
         
       if(result == 1 || result != -1){
        return true;
        } else {  
           Log.e("DATABASE", "Error when inserting an item (CODE: " + result + ")");
           return false; 
          }
        
    }

    public void updateItem(String tableName, String id, ContentValues itemData){  
        SQLiteDatabase db = this.getWritableDatabase(); 
        db.update(tableName, itemData, "id=?", new String[]{id});
        db.close(); 
    }

    public Boolean removeItem(String tableName, String id){       
        SQLiteDatabase db = this.getWritableDatabase(); 
        boolean result = db.delete(tableName, "id=?", new String[]{id}) == 1 ? true : false;
        db.close(); 
        return result;
    }    
    
    public Cursor getAllData(String item, String table){        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT " + item + " FROM " + table,null);
        
        return res;
    }
    
   public Cursor getItemData(String item, String table, String id){    
        SQLiteDatabase db = this.getWritableDatabase();           
        Cursor res = db.rawQuery("SELECT " + item + " FROM " + table + " WHERE ID ='" + id + "'", null);   
        res.moveToFirst(); 
        db.close(); 
        return res;
    }
    
    public Event getEventData(String item, String table, String id){     
        SQLiteDatabase db = this.getWritableDatabase(); 
        Cursor eventData = db.rawQuery("SELECT * FROM events WHERE id=" + id, null);   
        eventData.moveToFirst();           
        
        Event event = new Event(eventData.getString(0), eventData.getString(1), eventData.getString(2), new Date(eventData.getLong(4)), new Date(eventData.getLong(5)), new Date(eventData.getLong(3)));      
        db.close();
        return event;
    }     
    
    public Note getNoteData(String item, String table, String id){     
        SQLiteDatabase db = this.getWritableDatabase();           
        Note note;
        Cursor noteData = db.rawQuery("SELECT * FROM notes WHERE id=" + id, null);  
        db.close(); 
       if(noteData.moveToFirst()){        
        note = new Note(noteData.getString(1), noteData.getString(2), noteData.getString(3), new Date(noteData.getLong(4)));              
        return note;
        } else {
        return null;
        }  
    }           
 }






