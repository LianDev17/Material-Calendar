package com.liandev.materialcalendar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.liandev.materialcalendar.Activity.EditCalendarEventActivity;
import com.liandev.materialcalendar.Object.Event;
import com.liandev.materialcalendar.Object.Week;
import com.liandev.materialcalendar.R;
import com.liandev.materialcalendar.MainActivityKt;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    public ArrayList<Object> events = new ArrayList<>();
    public Activity activity;
    public Context context;
    
    public EventListAdapter(Activity activity, Context context) {
       this.activity = activity;
       this.context = context;
       this.inflater = LayoutInflater.from(context);
    }
  
   @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) view = inflater.inflate(R.layout.week_view, parent, false); else view = inflater.inflate(R.layout.event_item, parent, false);
        return viewType == 1 ? new WeekListHolder(view) : new EventListHolder(view);
    }
    
    public void setItems(ArrayList<Object> items){
      events.addAll(items);
      notifyDataSetChanged();
    }
    
    public void addEvent(Event event){
        int pos = 0;
        for(Object object : events){
          Week week = null;  
            if(object.getClass() == Week.class){
            week = (Week) object;
            
           if(event.getStartDate().getDate() >= week.getStartDay() && event.getStartDate().getDate() <= week.getEndDay())
              pos = events.indexOf(week);
            }
        }
        events.add(pos + 1, event);
        notifyItemInserted(pos + 1);
    }
    
    public void updateEvent(Event event){
        Event _event = getEventById(event.getId());
        if(_event != null){
          int _pos = events.indexOf(_event);
          events.set(_pos, event);
          notifyItemChanged(_pos);
        }
    }

    @Override
   public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
      Object weekOrEventView = events.get(pos);
     if(weekOrEventView.getClass() == Week.class) weekOnBindViewHolder((WeekListHolder) holder, (Week) weekOrEventView); else eventOnBindViewHolder((EventListHolder) holder, (Event) weekOrEventView);
   }
   
   public void eventOnBindViewHolder(EventListHolder holder, Event event){
       Calendar cal = Calendar.getInstance();
       cal.setTimeInMillis(event.getStartDate().getTime());
       
       holder.event_name.setText(event.getName());
       
       if(event.getDescription() != null)
       holder.event_description.setText(event.getDescription());
       else 
       holder.event_description.setVisibility(View.GONE);
       
       
       
       holder.event_time.setVisibility(View.GONE);
       holder.event_number.setText("" + event.getStartDate().getDate());
       holder.event_dayOfWeek.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
       
       /*String startTime = (event.getStartDate().getHours() < 10 ? "0" + event.getStartDate().getHours() : event.getStartDate().getHours())
                       + ":" +
                      (event.getStartDate().getMinutes() < 10 ? "0" + event.getStartDate().getMinutes() : event.getStartDate().getMinutes());
  
        String endTime = (event.getEndDate().getHours() < 10 ? "0" + event.getEndDate().getHours() : event.getEndDate().getHours())
                       + ":" +
                      (event.getEndDate().getMinutes() < 10 ? "0" + event.getEndDate().getMinutes() : event.getEndDate().getMinutes());

        
        Calendar startDateCal = Calendar.getInstance();
        startDateCal.setTimeInMillis(event.getStartDate().getTime());
        Calendar endDateCal = Calendar.getInstance();
        endDateCal.setTimeInMillis(event.getEndDate().getTime());
        
        String startDateMonth = startDateCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String endDateMonth = endDateCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        
        holder.event_time.setText(startDateMonth.substring(0, 3) + " " + startDateCal.get(Calendar.DAY_OF_MONTH) + ", " + startTime + " - " + endDateMonth.substring(0, 3) + " " + endDateCal.get(Calendar.DAY_OF_MONTH) + ", " + endTime);*/
        holder.event.setOnClickListener(v -> {
           Intent i = new Intent(context, EditCalendarEventActivity.class);
           i.putExtra("EVENT",event);
           ((MainActivityKt) context).result.launch(i);
       });  
   }
   
   public void weekOnBindViewHolder(WeekListHolder holder , Week week){
      holder.week_number.setText(week.getFullWeekNumbers());
   }
   
   @Override
    public int getItemViewType(int pos) {
        return events.get(pos).getClass() == Week.class ? 1 : 2;
    }
    
  class EventListHolder extends RecyclerView.ViewHolder {
    CardView event; 
    TextView event_name; 
    TextView event_description; 
    TextView event_time;
    TextView event_number;
    TextView event_dayOfWeek;
      
      EventListHolder(View itemView) {
            super(itemView);
            event = itemView.findViewById(R.id.event_cardView);
            event_name = itemView.findViewById(R.id.event_name);
            event_description = itemView.findViewById(R.id.event_description);
            event_time = itemView.findViewById(R.id.event_time);
            event_number = itemView.findViewById(R.id.dayNumber);
            event_dayOfWeek = itemView.findViewById(R.id.dayOfWeek);
        }
  }
  
  class WeekListHolder extends RecyclerView.ViewHolder {
    TextView week_number; 
      
      WeekListHolder(View itemView) {
            super(itemView);
            week_number = itemView.findViewById(R.id.week);
        }
  }

  @Override
   public int getItemCount() {return events.size();}
   
   public Event getEvent(Calendar cal){
       Event event = null;
       for(Object _event : events){
        if(_event.getClass() == Event.class){
          Event fevent = (Event) _event;   
            if(fevent.getStartDate().getTime() == cal.getTimeInMillis()){
              event = fevent;
          }
        }
      }
      
      return event; 
   }
   
   public Event getEventById(String id){
       Event event = null;
       for(Object _event : events){
        if(_event.getClass() == Event.class){
          Event fevent = (Event) _event;   
            if(fevent.getId().equals(id)){
              event = fevent;
          }
        }
      }
      
      return event; 
   }
}

