package com.liandev.materialcalendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.liandev.materialcalendar.Activity.EditTaskActivity;
import com.liandev.materialcalendar.Object.Task;
import com.liandev.materialcalendar.Object.FinishedTasks;
import com.liandev.materialcalendar.MainActivityKt;
import com.liandev.materialcalendar.GoogleTasksHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Collections;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<Object> tasksList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private String listId;
    private GoogleTasksHelper googleTasksHelper;

    TasksAdapter(Context context, String _listId, GoogleTasksHelper _googleTasksHelper) {
        this.mInflater = LayoutInflater.from(context);
        this.listId = _listId;
        this.googleTasksHelper = _googleTasksHelper;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = viewType == 1 ? mInflater.inflate(R.layout.task_item, parent, false) : mInflater.inflate(R.layout.finished_tasks_item, parent, false);
        return viewType == 1 ? new TasksHolder(view) : new finishedTasksHolder(view);
    }

    public void notifyAdd(ArrayList<Task> _taskslist) {
      ArrayList<Task> tasksNotFinishedlist = new ArrayList<>();
      ArrayList<Task> tasksFinishedlist = new ArrayList<>();
        for(Task task : _taskslist){
          if(getTask(task.getTaskId()) == null){
           if(task.taskIsFinish()){
               tasksFinishedlist.add(task);
             } else {
              tasksNotFinishedlist.add(task);
            }
          }
        }
        
        if(getFinishedTasksPosition() != - 1 && tasksNotFinishedlist.size() != 0){
        tasksList.addAll(getFinishedTasksPosition() == 0 ? 0 : getFinishedTasksPosition() - 1, tasksNotFinishedlist);
        } else if(tasksNotFinishedlist.size() != 0) {
         tasksList.addAll(tasksNotFinishedlist);     
       }
        if(getFinishedTasksPosition() == -1 && tasksFinishedlist.size() != 0){
        tasksList.add(new FinishedTasks(context.getString(R.string.finished_tasks)));
        tasksList.addAll(tasksFinishedlist); 
        } else if(getFinishedTasksPosition() != -1) {
         tasksList.addAll(getFinishedTasksPosition() + 1, tasksFinishedlist); 
        }
        
        if(tasksNotFinishedlist.size() != 0 || tasksFinishedlist.size() != 0)
        notifyDataSetChanged();
    }
    
    public void updateItem(int pos, Task task){
        if(getFinishedTasksPosition() == -1 && ((Task) tasksList.get(pos)).taskIsFinish() == false && task.taskIsFinish()){
          tasksList.add(new FinishedTasks(context.getString(R.string.finished_tasks)));    
          tasksList.remove(pos);
          notifyItemRemoved(pos);
          tasksList.add(tasksList.size(), task);
          notifyItemMoved(pos, tasksList.size() - 1);
       } else if(getFinishedTasksPosition() != -1 && ((Task) tasksList.get(pos)).taskIsFinish() == false && task.taskIsFinish()){
          tasksList.remove(pos);
          notifyItemRemoved(pos);
          tasksList.add(tasksList.size(), task);
          notifyItemChanged(tasksList.size());
           } else if(getFinishedTasksPosition() != -1 && ((Task) tasksList.get(pos)).taskIsFinish() == true && !task.taskIsFinish()){
          tasksList.remove(pos);
          notifyItemRemoved(pos);
          tasksList.add(0, task);
          notifyItemMoved(pos, 0);
        } else {
           tasksList.set(pos, task);
           notifyItemChanged(pos); 
        }
    }
    
    public void notifyRemove(int pos){
        tasksList.remove(pos);
        notifyItemRemoved(pos);
        
        if(getFinishedTasksPosition() != -1){
        boolean taskAfterFinishedTasksExist = false;
        try { taskAfterFinishedTasksExist = tasksList.get(getFinishedTasksPosition() + 1) != null ? true : false; } catch (Exception e){ taskAfterFinishedTasksExist = false; }
        if(!taskAfterFinishedTasksExist){
           tasksList.remove(getFinishedTasksPosition());
           notifyItemRemoved(getFinishedTasksPosition()); 
           }
        }
   }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _holder, final int position) {
       if(tasksList.get(position).getClass() == Task.class){
        TasksHolder holder = (TasksHolder) _holder;
        Task task = (Task) tasksList.get(position);
        holder.task_name.setText(task.getTaskName() == null || task.getTaskName().equals("") ? context.getString(R.string.task_no_title) : task.getTaskName());
        
        if(task.taskIsFinish()){
        holder.task_finish.setChecked(true);
        holder.task.setCardForegroundColor(ColorStateList.valueOf(Color.parseColor("#906F6F6F")));                            
        } else {
        holder.task_finish.setChecked(false);
        holder.task.setCardForegroundColor(null);               
        }
        
        if(task.getTaskDescription() == null || task.getTaskDescription().equals("")){
          holder.task_description.setVisibility(View.GONE);
        } else { 
          holder.task_description.setVisibility(View.VISIBLE);
          holder.task_description.setText(task.getTaskDescription());    
        }
        
        holder.task_endAtText.setVisibility(View.GONE);
         
        if(task.getTaskEndDate() == null){
          holder.task_end_date.setVisibility(View.GONE);
         // holder.task_end_hours.setVisibility(View.GONE);
          holder.task_endAtDate.setVisibility(View.GONE);
          holder.task_endAtText.setVisibility(View.GONE);
        } else {
          Date taskEndDate = task.getTaskEndDate();
          
          Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
          cal.setTime(taskEndDate);
          int dayInt = cal.get(Calendar.DAY_OF_WEEK) - 2 < 0 ? 6 : cal.get(Calendar.DAY_OF_WEEK) - 2;
          String day = (new SimpleDateFormat("dd")).format(cal.getTime());
          String month = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
         // String endDateHours =  taskEndDate.getHours() + "h" + (taskEndDate.getMinutes() < 10 ? "0" + taskEndDate.getMinutes() : "" + taskEndDate.getMinutes());
    
          holder.task_end_date.setVisibility(View.VISIBLE);
          holder.task_endAtText.setVisibility(View.VISIBLE);
         // holder.task_end_hours.setVisibility(View.VISIBLE); 
       //   holder.task_end_hours.setText(endDateHours);
        //  holder.task_endAtHours.setVisibility(View.VISIBLE);
          
          if(cal.get(Calendar.YEAR) == (new Date().getYear() + 1900) && cal.get(Calendar.MONTH) == new Date().getMonth() && cal.get(Calendar.DAY_OF_MONTH) == new Date().getDate()){
             holder.task_end_date.setText(context. getString(R.string.task_today));
             holder.task_endAtDate.setVisibility(View.GONE);
            } else if(cal.get(Calendar.YEAR) == (new Date().getYear() + 1900) && cal.get(Calendar.MONTH) == new Date().getMonth() && cal.get(Calendar.DAY_OF_MONTH) == new Date().getDate() + 1){
              holder.task_end_date.setText(context.getString(R.string.task_ends) + " " + context.getString(R.string.task_tomorrow));
              holder.task_endAtDate.setVisibility(View.GONE);
             } else {
             holder.task_end_date.setText(day + " " + cal.getTime().getDate() + " " + month);
             holder.task_endAtDate.setVisibility(View.VISIBLE);
          }
        }  

        
        holder.task_finish.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                  try {  
                    if(holder.task_finish.isChecked()){                  
                        holder.task.setCardForegroundColor(ColorStateList.valueOf(Color.parseColor("#906F6F6F"))); 
                        googleTasksHelper.updateTask(listId, task.getTaskId(), task.getTaskName(), task.getTaskDescription(), task.getTaskEndDate() == null ? null : task.getTaskEndDate().getTime(), true);
                    } else {
                        holder.task.setCardForegroundColor(null); 
                        googleTasksHelper.updateTask(listId, task.getTaskId(), task.getTaskName(), task.getTaskDescription(), task.getTaskEndDate() == null ? null : task.getTaskEndDate().getTime(), false);    
                    }
                 } catch (Exception e){  
                  Log.e("UpdateTasksError", "", e);
                 }
             }          
        });
           
        holder.task.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    try {
                        
                    Intent EditTask = new Intent(context, EditTaskActivity.class);
                    String taskId = task.getTaskId();
                    
                    EditTask.putExtra("task_id", taskId);
                    EditTask.putExtra("task_pos", "" + position);
                    EditTask.putExtra("task_listId", listId);
                    EditTask.putExtra("task_title", task.getTaskName());
                    EditTask.putExtra("task_description", task.getTaskDescription());
                    EditTask.putExtra("task_end_date",task.getTaskEndDate() != null ? task.getTaskEndDate().getTime() : 0);
                    EditTask.putExtra("task_isFinish",task.taskIsFinish());    
                    MainActivityKt.getInstance().startActivityForResult(EditTask, MainActivityKt.getInstance().EDIT_TASK_RESULT);
                    } catch (Exception e){  
                   Log.e("IntentStartError", "", e);
                    }
                }              
           });
        } else {
          finishedTasksHolder holder = (finishedTasksHolder) _holder;
          FinishedTasks obj = (FinishedTasks) tasksList.get(position);
          holder.ftText.setText(obj.getTitle());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return tasksList.size();
    }
    
    @Override
      public int getItemViewType(int pos) {
        return tasksList.get(pos).getClass() == Task.class ? 1 : 2;
    }

    // stores and recycles views as they are scrolled off screen
    public class TasksHolder extends RecyclerView.ViewHolder {
        MaterialCardView task;
        TextView task_name;
        TextView task_description;
        TextView task_end_date;
       // TextView task_end_hours;
        TextView task_endAtText;
        TextView task_endAtDate;
        //TextView task_endAtHours;
        CheckBox task_finish;

        TasksHolder(View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
            task_name = itemView.findViewById(R.id.task_name);
            task_description = itemView.findViewById(R.id.task_description);
            task_end_date = itemView.findViewById(R.id.task_end_date);
           // task_end_hours = itemView.findViewById(R.id.task_end_hours);
            task_endAtText = itemView.findViewById(R.id.task_endAtText);
            task_endAtDate = itemView.findViewById(R.id.task_endAtDate);
            //task_endAtHours = itemView.findViewById(R.id.task_endAtHours);
            task_finish = itemView.findViewById(R.id.task_finish);
        }
    }
    
    public class finishedTasksHolder extends RecyclerView.ViewHolder {
       TextView ftText;
        
      finishedTasksHolder(View itemView) {
            super(itemView);
            ftText = itemView.findViewById(R.id.finishedTaskText);
      }
    }        

    // convenience method for getting data at click position
    Task getItem(int id) {
        return (Task) tasksList.get(id);
    }
    
    public Task getTask(String id){
      Object obj = tasksList.stream()
      .filter(x -> x.getClass() == Task.class && ((Task) x).getTaskId().equals(id))
      .findAny()
      .orElse(null);
      
      return obj != null ? (Task) obj : null;
   }
   
   public int getFinishedTasksPosition(){
       Object obj = tasksList.stream()
      .filter(x -> x.getClass() == FinishedTasks.class)
      .findAny()
      .orElse(null);
      
      int pos = obj != null ? tasksList.indexOf((FinishedTasks) obj) : -1;
      return pos;
   }
}