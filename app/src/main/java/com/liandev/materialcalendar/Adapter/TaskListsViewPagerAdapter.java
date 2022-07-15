package com.liandev.materialcalendar;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.liandev.materialcalendar.Fragments.TasksFragment;
import com.liandev.materialcalendar.MainActivity;
import com.liandev.materialcalendar.Object.TaskList;

import java.util.ArrayList;

public class TaskListsViewPagerAdapter extends FragmentStateAdapter {
   public ArrayList<DynamicFragment> tasksFragment = new ArrayList<>(); 
   public ArrayList<TaskList> taskLists = new ArrayList<>(); 
   
   public TaskListsViewPagerAdapter(@NonNull TasksFragment fragmentActivity) {
            super(fragmentActivity);
        }
        
   @NonNull 
   @Override 
   public Fragment createFragment(int position) {
       return tasksFragment.get(position);
   }
   
   public void addFrag(DynamicFragment fragment, String id, String taskListName) {
            taskLists.add(new TaskList(id, taskListName));
            tasksFragment.add(fragment.newInstance(getList(taskLists.size() - 1)));
            notifyDataSetChanged();
        }
        
    public TaskList getList(int position){
        return taskLists.get(position);
    }

   @Override 
     public int getItemCount() {
       return tasksFragment.size();
    }
    
    public void clear(){
        tasksFragment.clear();
        taskLists.clear();
        notifyDataSetChanged();
    }
 } 