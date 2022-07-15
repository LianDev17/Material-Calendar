package com.liandev.materialcalendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.database.Cursor;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.liandev.materialcalendar.Database.DatabaseManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.api.client.util.DateTime;
import com.liandev.materialcalendar.Object.Task;
import com.google.api.services.tasks.model.Tasks;
import com.liandev.materialcalendar.Object.TaskList;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class DynamicFragment extends Fragment {

    private ArrayList<Task> tasksList = new ArrayList<>();
    private RecyclerView tasksRecyclerView;
    private TasksAdapter adapter;
    public TaskListsViewPagerAdapter tlvpadapter;
    public GoogleTasksHelper googleTasksHelper;
    public int fragmentPosition = 0;
    private TextView emptyTasksListMessage;
    public CircularProgressIndicator loadingTasks;
    private Context context;
    private Applications application = Applications.getInstance();
    public DatabaseManager db;
    public Cursor deleteOutdatedTasks;
    public DynamicFragment() { } 
 
      public static DynamicFragment newInstance(TaskList taskList){
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putString("listId", taskList.getId());
        fragment.setArguments(args); 
        return fragment;
    } 
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.tasks_list_fragment, container, false);
        db = new DatabaseManager(context);
        deleteOutdatedTasks = db.getItemData("remove_outdated_task", "settings", "settings");
        
        googleTasksHelper = new GoogleTasksHelper(getActivity(), context, application.getCredential(context));

        tasksRecyclerView = rootView.findViewById(R.id.tasks_list);      
        emptyTasksListMessage = rootView.findViewById(R.id.emptyTasksListMessage);    
        adapter = new TasksAdapter(context, getArguments().getString("listId"), googleTasksHelper);
        loadingTasks = (CircularProgressIndicator) rootView.findViewById(R.id.loadingTasks);
        
        tasksRecyclerView.setAdapter(adapter);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        loadingTasks.setVisibility(View.VISIBLE); 
        getTaskLists();
        
       if(!application.isInternetAvailable(context)){
          emptyTasksListMessage.setVisibility(View.VISIBLE); 
          emptyTasksListMessage.setText(getString(R.string.no_internet));
      }
      
        return rootView;
    } 
    
    public void getTaskLists(){
        googleTasksHelper.getTasksOfList(getArguments().getString("listId"), this);
    }
    
    public void setTasksOfList(Tasks list){
       if(list != null){
       for(com.google.api.services.tasks.model.Task task : list.getItems()){
           Date dateTime = null;
          if(task.getDue() != null)
          dateTime = new Date(task.getDue().getValue());
       
          if(task.getDeleted() == null && Boolean.parseBoolean(deleteOutdatedTasks.getString(0)) && dateTime != null && dateTime.getTime() <= new Date().getTime()){
              googleTasksHelper.deleteTask(getArguments().getString("listId"), task.getId());     
           } else {
          if(task.getDeleted() != null && task.getDeleted() == true){
              try {
             ArrayList<Object> taskList = adapter.tasksList;
           for(Object obj : taskList){
            if(obj.getClass() == Task.class){
             Task _task = (Task) obj;
             int pos = adapter.tasksList.indexOf(obj);  
              if(task.getId().equals(_task.getTaskId())){
                  adapter.notifyRemove(pos);  
                   }   
               } 
            }
          } catch (Exception e) {
            Log.e("RemoveTasksError", "", e);
         }
          } else { 
          try {
           boolean isExist = false;
           for(Object obj : adapter.tasksList){
            if(obj.getClass() == Task.class){
             Task _task = (Task) obj;
                if(task.getId().equals(_task.getTaskId()))
                  isExist = true;
           }
          } 
           if(isExist == false){
                ArrayList<Task> tasksList = new ArrayList<>();
                tasksList.add(new com.liandev.materialcalendar.Object.Task(task.getId(), task.getTitle(), task.getNotes(), dateTime, task.getCompleted() != null ? true : false, task.getDeleted() != null ? task.getDeleted() : false));
                adapter.notifyAdd(tasksList); 
              } else {
               final ArrayList<Object> taskList = adapter.tasksList;
               for(Object obj : taskList){
                if(obj.getClass() == Task.class){
                  Task _task = (Task) obj;
                  int pos = adapter.tasksList.indexOf(obj);  
             
                  if(task.getId().equals(_task.getTaskId())){
                    Task t = new com.liandev.materialcalendar.Object.Task(task.getId(), task.getTitle(), task.getNotes(), dateTime, task.getCompleted() != null ? true : false, task.getDeleted() != null ? task.getDeleted() : false);
                    adapter.updateItem(pos, t);
                           }
                       }
                    } 
                 }
              } catch (Exception e){}
           }
        }
     }   
  } else {
     Log.i("TASK_LENGTH","" + list);
  } 
      
      
         if(!adapter.tasksList.isEmpty()){
         tasksRecyclerView.setVisibility(View.VISIBLE);   
         emptyTasksListMessage.setVisibility(View.GONE); 
         loadingTasks.setVisibility(View.GONE); 
         } else if(adapter.tasksList.isEmpty() && GoogleSignIn.getLastSignedInAccount(context) != null){
         tasksRecyclerView.setVisibility(View.GONE);   
         emptyTasksListMessage.setVisibility(View.VISIBLE); 
         loadingTasks.setVisibility(View.GONE); 
         emptyTasksListMessage.setText(getString(R.string.empty_tasks_list));
        }
    }
    
    
    @Override 
     public void onAttach(Context _context) {
       super.onAttach(context);
        context = _context;
     }

    @Override
     public void onDetach() {
       super.onDetach();
        context = null;
      }
      
}