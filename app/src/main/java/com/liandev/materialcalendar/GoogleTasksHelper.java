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
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.Fragments.TasksFragment;

import com.liandev.materialcalendar.MainActivityKt;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GoogleTasksHelper {
     
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    
    private Context context;
    private Activity activity; 
    private TasksFragment fragment;
    
    public GoogleAccountCredential credential;
    public com.google.api.services.tasks.Tasks services;
    public DatabaseManager db;
    
    
    public TaskLists tasklists = null;
    public Tasks tasks = null;  
    
    public GoogleTasksHelper(Activity activity, Context context, GoogleAccountCredential _credential){
       this.context = context;
       this.activity = activity;
       this.fragment = fragment;
       this.credential = _credential;
       this.services = initServices();
       
       db = new DatabaseManager(context);
       
    }
    
    private com.google.api.services.tasks.Tasks initServices(){
     return new com.google.api.services.tasks.Tasks.Builder(new NetHttpTransport(), new GsonFactory(), credential)
         .setApplicationName("Material Calendar")
         .build();
    }
 
     public void createTaskList(String name){
       new Thread(() -> {
         try {  
       TaskList taskList = new TaskList();
       taskList.setTitle(name);
       services.tasklists().insert(taskList).execute();   
       } catch (Exception e){
           handleErrors(e);
         }       
        activity.runOnUiThread(()->{
       });
    }).start();
     }
     
     public void createTask(String listId, String title, String description, Long endDateTime){
       new Thread(() -> {
         try {  
        Task task = new Task();
        task.setTitle(title);
        
        if(description == "" || description == null){
        task.setNotes(null);
        } else {
        task.setNotes(description);
        }
        
        if(endDateTime != null)
        task.setDue(new DateTime(endDateTime));
        
        services.tasks().insert(listId, task).execute();
          } catch (Exception e){
           handleErrors(e);
          }    
        }).start();
      } 
      
      public void deleteTask(String listId, String taskId){
         new Thread(() -> {
          try {   
          services.tasks().delete(listId, taskId).execute();
         } catch (Exception e){
           handleErrors(e);
          }    
         activity.runOnUiThread(()->{
          });
        }).start(); 
      } 
      
      public void updateTask(String listId, String taskId, String title, String description, Long endDateTime, Boolean isFinish){
       new Thread(() -> {
         try {  
          Task task = new Task();
          task.setId(taskId);
          task.setTitle(title);
          task.setNotes(description);
          
          if(endDateTime != null)
          task.setDue(new DateTime(endDateTime));
          
          if(isFinish.equals(true))
          task.setStatus("completed");
          
          services.tasks().update(listId, taskId, task).execute();
         } catch (Exception e){
           handleErrors(e);
          }   
         activity.runOnUiThread(()->{
          });
        }).start(); 
      }  
     
     public TaskLists getTaskLists(MainActivityKt context, TasksFragment fragment){
       new Thread(() -> {
         try {
          tasklists = services.tasklists().list().execute();  
         } catch (Exception e){
           handleErrors(e);
         }    
        activity.runOnUiThread(()->{
          fragment.setTaskLists(context);  
       });
    }).start();
       
      return tasklists;
     }   
     
     
     public Tasks getTasksOfList(String listId, DynamicFragment taskfragment){
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
     }
    
    public void handleErrors(Exception e){
      if (e instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) e).getIntent(),
                        REQUEST_AUTHORIZATION);
       } else {
        Log.e("GOOGLE_TASK_HELPER_ERROR","ERROR",e);
      } 
    }           
}