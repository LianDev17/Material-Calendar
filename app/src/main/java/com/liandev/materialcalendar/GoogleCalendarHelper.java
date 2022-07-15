package com.liandev.materialcalendar;

import static android.app.Activity.RESULT_OK;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.Fragments.CalendarFragment;
import com.liandev.materialcalendar.Fragments.TasksFragment;

import com.liandev.materialcalendar.MainActivityKt;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GoogleCalendarHelper {
     
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    
    private Context context;
    private Activity activity; 
    private TasksFragment fragment;
    
    public GoogleAccountCredential credential;
    public com.google.api.services.calendar.Calendar services;
    public DatabaseManager db;
    
    public TaskLists tasklists = null;
    public Events events = null;  
    
    public GoogleCalendarHelper(Activity activity, Context context, GoogleAccountCredential _credential){
       this.context = context;
       this.activity = activity;
       this.fragment = fragment;
       this.credential = _credential;
       this.services = initServices();
       
       db = new DatabaseManager(context);
    }
    
    public com.google.api.services.calendar.Calendar initServices(){
     return new com.google.api.services.calendar.Calendar.Builder(new NetHttpTransport(), new GsonFactory(), credential)
         .setApplicationName("Material Calendar")
         .build();
    }
 
     public void createEvent(String title, String description, Long startDateTime, Long endDateTime){
       new Thread(() -> {
         try {  
          
              Event event = new Event();
              event.setSummary(title);
              event.setDescription(description != null && description != "" ? description : null);
            
              Date s = new Date(startDateTime);
              Date e = new Date(endDateTime);
            
            
              DateTime _startDateTime = new DateTime(startDateTime);    
              EventDateTime start = new EventDateTime()
              .setDateTime(_startDateTime);
              event.setStart(start);
  
              DateTime _endDateTime = new DateTime(endDateTime);    
              EventDateTime end = new EventDateTime()
              .setDateTime(_endDateTime);
              event.setEnd(end); 
           
        
           event = services.events().insert("primary", event).execute();
          } catch (Exception e){
           handleErrors(e);
          }    
         activity.runOnUiThread(()->{
          });
        }).start();
      } 
      
      public void deleteEvent(String listId, String eventId){
         new Thread(() -> {
          try {   
            services.events().delete(listId, eventId).execute();
         } catch (Exception e){
           handleErrors(e);
          }    
         activity.runOnUiThread(()->{
          });
        }).start(); 
      } 
      
      public void updateEvent(String listId, String taskId, String title, String description, Long startDateTime, Long endDateTime){
       new Thread(() -> {
         try {  
          Event event = new Event();
          event.setSummary(title);
          event.setDescription(description != null && description != "" ? description : null);
        
           DateTime _startDateTime = new DateTime(startDateTime);    
           EventDateTime start = new EventDateTime()
           .setDateTime(_startDateTime);
           event.setStart(start);
        
           DateTime _endDateTime = new DateTime(endDateTime);    
           EventDateTime end = new EventDateTime()
           .setDateTime(_endDateTime);
           event.setEnd(end);
        
          event = services.events().update(listId, taskId, event).execute();
         } catch (Exception e){
           handleErrors(e);
          }   
         activity.runOnUiThread(()->{
          });
        }).start(); 
      }  
     
     public Events getEvents(CalendarFragment fragment, MainActivityKt context, String listId){
       new Thread(() -> {
         try { 
           
         CalendarList calendarList = services.calendarList().list().execute();
        List<CalendarListEntry> list = calendarList.getItems();

        for (CalendarListEntry calendarListEntry : list) {
           // Log.i("List", calendarListEntry.getSummary());
           // Log.d("ListId", "Calender Id: " + calendarListEntry.getId());
        }
             
           events = services.events().list(listId)
           .setSingleEvents(true)
           .setShowDeleted(false)
           .execute();
         } catch (Exception e){
           handleErrors(e);
         }    
        activity.runOnUiThread(()->{
          fragment.setEvents(context);  
       });
    }).start();
       
      return events;
     }   
     
     
    /* public Tasks getTasksOfList(String listId, DynamicFragment taskfragment){
      new Thread(() -> {
        try {
           com.google.api.services.tasks.Tasks.TasksOperations.List l = services.tasks()
                  .list(listId)
                  .setShowDeleted(true)
                  .setShowCompleted(true)
                  .setShowHidden(true);
                            
           tasks = l.execute();
          } catch (Exception e){
            handleErrors(e);
           }
         activity.runOnUiThread(()->{
           taskfragment.setTasksOfList(tasks);  
         });  
       }).start();
       
       
       
      return tasks;
     }*/
    
    public void handleErrors(Exception e){
      if (e instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) e).getIntent(),
                        REQUEST_AUTHORIZATION);
       } else {
        Log.e("GOOGLE_CALENDAR_HELPER_ERROR","ERROR",e);
      } 
    }           
}