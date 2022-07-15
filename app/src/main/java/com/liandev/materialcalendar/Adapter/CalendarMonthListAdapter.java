package com.liandev.materialcalendar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Fragments.CalendarFragment;
import com.liandev.materialcalendar.Object.CalendarDay;
import com.liandev.materialcalendar.Object.Event;
import com.liandev.materialcalendar.Object.Week;
import com.liandev.materialcalendar.R;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarMonthListAdapter extends RecyclerView.Adapter<CalendarMonthListAdapter.CalendarMonthListHolder> {
    private LayoutInflater mInflater;
    public ArrayList<CalendarDay> days = new ArrayList<>();
    public ArrayList<CalendarDay> months = new ArrayList<>();
    public Context context;
    public CalendarFragment fragment;
    private Applications application = Applications.getInstance();
    public Activity activity;
    public ArrayList<Object> items = new ArrayList<>();
      
      
    public CalendarMonthListAdapter(CalendarFragment _fragment, Activity activity, Context context, ArrayList<CalendarDay> _days) {
       this.days = _days; 
       this.context = context;
       this.fragment = _fragment;
       this.activity = activity;
       this.mInflater = LayoutInflater.from(context);
    }
    
    @Override
  public CalendarMonthListHolder onCreateViewHolder(ViewGroup parent, int arg1) {
      View view = mInflater.inflate(R.layout.calendar_month_view, parent, false);
    return new CalendarMonthListHolder(view);
  } 
  
   public CalendarDay createMonth(RecyclerView rv, Calendar cal){
       months.add(new CalendarDay(cal, rv));
       return getMonth(cal);
   }
   
   public void updateEvent(int pos, Event event){
       //CalendarDay month = getMonth(event.getStartDate)
   }

    @Override
   public void onBindViewHolder(@NonNull CalendarMonthListHolder holder, int pos) {
      Long time = new Long(pos) * 2628002;
      
      EventListAdapter adapter;
      RecyclerView eventsList;
      
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time * 1000L);
      CalendarDay month = getMonth(cal);
      items = new ArrayList<>();
      
      RecyclerView _eventsList = holder.eventsList;
       EventListAdapter _adapter = new EventListAdapter(activity, context);
       _eventsList.setLayoutManager(new LinearLayoutManager(context));
       _eventsList.setAdapter(_adapter);
       
       if(month == null){
       month = createMonth(_eventsList, cal);
       } else {
       month.setRv(_eventsList);
       }
       
       _eventsList = month.getRv();
       cal = month.getCal();
       
       SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
       SimpleDateFormat month_min = new SimpleDateFormat("MMM");
       String month_min_name = month_min.format(cal.getTime());
       String month_name = (new SimpleDateFormat("MMMM")).format(cal.getTime());
       month_name = month_name.substring(0, 1).toUpperCase() + month_name.substring(1);
       
       eventsList = month.getRv();
       adapter = (EventListAdapter) month.getRv().getAdapter();
       
       items.add(new Week(1, 8, month_min_name));
       addEvents(adapter, cal, 1, 8);
       items.add(new Week(8, 16, month_min_name));
       addEvents(adapter, cal, 8, 16);
       items.add(new Week(16, 24, month_min_name));
       addEvents(adapter, cal, 16, 24);
       items.add(new Week(24, cal.getActualMaximum(Calendar.DAY_OF_MONTH), month_min_name));
       addEvents(adapter, cal, 24, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
       
       LinearLayoutManager lm = (LinearLayoutManager) fragment.recyclerView.getLayoutManager();
      
       Calendar first_month = Calendar.getInstance();
       first_month.setTimeInMillis(new Long(lm.findFirstCompletelyVisibleItemPosition()) * 2628003 * 1000L);
       
       SimpleDateFormat month_dateb = new SimpleDateFormat("MMMM");
       String month_title = month_dateb.format(first_month.getTime());
      
       month_title = month_title.substring(0, 1).toUpperCase() + month_title.substring(1);
       fragment.setTitle(month_title + " " + (first_month.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR) ? first_month.get(Calendar.YEAR) : ""));
      
      //Log.i("InfoTime", month_name + " " + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR));
      
      adapter.setItems(items);
      holder.month.setText(month_name + " " + (cal.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR) ? cal.get(Calendar.YEAR) : ""));
   }
   
   public void addEvents(EventListAdapter adapter, Calendar cal, int min, int max){
      for(Event event : fragment.eventsDataList){
        Calendar eventStartCal = Calendar.getInstance();
        eventStartCal.setTimeInMillis(event.getStartDate().getTime());
         if(eventStartCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && eventStartCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && eventStartCal.get(Calendar.DAY_OF_MONTH) >= min && eventStartCal.get(Calendar.DAY_OF_MONTH) <= max && !fragment.eventExist(cal)){        
              items.add(event);
           }
       }
   }
 
  class CalendarMonthListHolder extends RecyclerView.ViewHolder {
    TextView month;
    RecyclerView eventsList;
    
      CalendarMonthListHolder(View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.month);
            eventsList = itemView.findViewById(R.id.eventlist);
        }
  }

  @Override
   public int getItemCount() {return Integer.MAX_VALUE;}
   
   public CalendarDay getMonth(Calendar cal){
      CalendarDay month = months.stream()
      .filter(x -> x.getCal().get(Calendar.YEAR) == cal.get(Calendar.YEAR) && x.getCal().get(Calendar.MONTH) == cal.get(Calendar.MONTH))
      .findAny()
      .orElse(null);
      
      return month; 
   }
}
