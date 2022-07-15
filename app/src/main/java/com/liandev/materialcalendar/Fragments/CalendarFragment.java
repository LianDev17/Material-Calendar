package com.liandev.materialcalendar.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.core.view.GravityCompat;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.tasks.TasksScopes;
import com.liandev.materialcalendar.Adapter.CalendarMonthListAdapter;
import com.liandev.materialcalendar.Activity.CreateCalendarEventActivity;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.GoogleCalendarHelper;
import com.liandev.materialcalendar.MainActivityKt;
import com.liandev.materialcalendar.Object.CalendarDay;
import com.liandev.materialcalendar.R;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import com.liandev.materialcalendar.Adapter.EventListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.liandev.materialcalendar.Activity.AboutActivity;
import com.liandev.materialcalendar.Activity.SettingsActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {

  private static final String[] SCOPES = {CalendarScopes.CALENDAR, TasksScopes.TASKS};

  private ArrayList<com.liandev.materialcalendar.Object.Event> eventsList = new ArrayList<>();
  public ArrayList<com.liandev.materialcalendar.Object.Event> eventsDataList = new ArrayList<>();
  public RecyclerView eventsRecyclerView;
  private ArrayList<String> daysList = new ArrayList<>();
  private int current_year = new Date().getYear() + 1900;
  private int current_month = new Date().getMonth();
  private int current_day = new Date().getDate();
  private Calendar currentInstance;
  public CircularProgressIndicator loadingEvents;
  private TextView emptyEventsListMessage;
  private GoogleSignInAccount googleUser;

  public RecyclerView recyclerView;
  public ArrayList<CalendarDay> days = new ArrayList<>();
  private CalendarView calendarView;
  private TextView dateName;
  private Applications application = Applications.getInstance();
  private boolean connectionIsLost = false;

  public GoogleCalendarHelper googleCalendarHelper;
  private Activity context;
  public CalendarMonthListAdapter adapter;
 
  public CalendarFragment() {}
  
  boolean isAllFabsVisible = false;

  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_calendar, container, false);

    googleUser = GoogleSignIn.getLastSignedInAccount(context);
    googleCalendarHelper = new GoogleCalendarHelper(context, context, getCredential());

    Date date = new Date(0);
    Calendar now = Calendar.getInstance();
    
    now.setTime(date);
    days.add(new CalendarDay(now, null));                  

    getEvents();
      
    adapter = new CalendarMonthListAdapter(this, context, context, days);
    recyclerView = view.findViewById(R.id.events_list);
    recyclerView.setAdapter(adapter);  
    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    
    FabSpeedDial fabSpeedDial = MainActivityKt.getInstance().binding.addTask;
    fabSpeedDial.setVisibility(View.GONE);
    
    FloatingActionButton add_event = view.findViewById(R.id.add_event);
    
    int v = (int) ChronoUnit.MONTHS.between(LocalDate.now(), LocalDate.ofEpochDay(0));
    v *= -1;
    recyclerView.scrollToPosition(v + 1);  
    
    add_event.setOnClickListener(vi -> {
         Intent i = new Intent(context, CreateCalendarEventActivity.class);
          ((MainActivityKt) context).result.launch(i);
    });
      
    MainActivityKt.getInstance().binding.navViewNavigationViewActivity.setNavigationItemSelectedListener( menuItem -> {
           switch (menuItem.getItemId()) { 
                   case R.id.reload: getEvents(); break;
                   case R.id.settings: startActivity(new Intent(context, SettingsActivity.class)); break;
                   case R.id.about: startActivity(new Intent(context, AboutActivity.class)); break;
                   } 
             MainActivityKt.getInstance().binding.drawerNavigationViewActivity.closeDrawer(GravityCompat.START);      
              return true;
               });          
    
    return view;
  }
  
  public void getEvents(){
    new Thread(() -> {
       context.runOnUiThread(()->{
      if(application.isInternetAvailable(context)){
        googleCalendarHelper.getEvents(this, ((MainActivityKt) context), googleUser.getEmail());
        }
      });
   }).start();
 }
 
 public void addEvent(EventListAdapter _adapter, com.liandev.materialcalendar.Object.Event event){
    Calendar cal = Calendar.getInstance();
    cal.setTime(event.getStartDate()); 
   _adapter.addEvent(event);
 }
 
 public boolean eventExist(Calendar cal){
    EventListAdapter _adapter = (EventListAdapter) adapter.getMonth(cal).getRv().getAdapter();
   return _adapter.getEvent(cal) == null ? false : true;
 }
 
  public void setTitle(String title){
      ((MainActivityKt) context).setAppbarTitle(title);
  }  
  
  public void setEvents(MainActivityKt _context){
      Events events = googleCalendarHelper.events;
      //eventsDataList.clear();
      
      for(Event event : events.getItems()){
         Date startDateTime = null;
         Date endDateTime = null;

       try {
         if(event.getStart().getDateTime() != null || event.getStart().getDate() != null){
           EventDateTime start = event.getStart();
           startDateTime = new Date(start.getDateTime() != null ? start.getDateTime().getValue() : start.getDate().getValue());
          }

         if(event.getEnd().getDateTime() != null || event.getEnd().getDate() != null){
           EventDateTime end = event.getEnd();
           endDateTime = new Date(end.getDateTime() != null ? end.getDateTime().getValue() : end.getDate().getValue());
          }

          boolean exist = false;

          for(com.liandev.materialcalendar.Object.Event _event : eventsDataList){
           if(_event.getId().equals(event.getId()))
           exist = true;
         }

          Calendar cal = Calendar.getInstance();
          cal.setTime(startDateTime);
          
          if(exist == false){
          com.liandev.materialcalendar.Object.Event _event = new com.liandev.materialcalendar.Object.Event(event.getId(), event.getSummary(), event.getDescription(), startDateTime, endDateTime, null);    
          EventListAdapter _adapter;
          try {
              _adapter = (EventListAdapter) adapter.getMonth(cal).getRv().getAdapter();
              if(_adapter.getEvent(cal) == null)
              _adapter.addEvent(_event);
          } catch (Exception e){}
          
          eventsDataList.add(_event);
         }
         
         } catch (Exception e){
           Log.e("DisplayingEventsErrorTypeA", "ERROR", e);
        }

      }

     for(Event event : events.getItems()){
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(event.getStart().getDateTime() != null ? event.getStart().getDateTime().getValue() : event.getStart().getDate().getValue());   
      EventListAdapter _adapter = adapter.getMonth(cal) != null ? (EventListAdapter) adapter.getMonth(cal).getRv().getAdapter() : null;
      
      if(_adapter != null){
      for(Object object : _adapter.events){
       if(object.getClass() == com.liandev.materialcalendar.Object.Event.class){
           com.liandev.materialcalendar.Object.Event _event = (com.liandev.materialcalendar.Object.Event) object;
        try {  
            
          boolean isDeleted = true;
          
           if(_event.getId().equals(event.getId())){
              isDeleted = false;
              _adapter.updateEvent(new com.liandev.materialcalendar.Object.Event(event.getId(), event.getSummary(), event.getDescription(), new Date(event.getStart().getDateTime() == null ? event.getStart().getDate().getValue() : event.getStart().getDateTime().getValue()), new Date(event.getEnd().getDateTime() == null ? event.getEnd().getDate().getValue() : event.getEnd().getDateTime().getValue()), null));
             }

         /*  if(isDeleted){
            eventsDataList.remove(pos);
            adapter.removeItem(pos);
                      }*/
                   } catch (Exception e){
                     Log.e("DisplayingEventsErrorTypeB", "ERROR", e);
                    }
                 }
              }
           }
        }      
    }
  
  /*
          VerticalWeekCalendar cal = view.findViewById(R.id.verticalCalendar);

          Calendar ffff = Calendar.getInstance();
          selected = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


          VerticalWeekCalendar calendarView = new VerticalWeekCalendar.Builder()
          .init(cal);

  calendarView.setOnDateClickListener(new OnDateClickListener() {
      @Override
      public void onCalenderDayClicked(int year, int month, int day) {
          GregorianCalendar selectedDay = new GregorianCalendar(year, month, day);
          if(selected.compareTo(selectedDay) != 0) {
            Not the same day is clicked
              selected = selectedDay;
          }
      }
  });

  calendarView.setDateWatcher(new DateWatcher() {
              @Override
              public int getStateForDate(int year, int month, int day, VerticalWeekAdapter.DayViewHolder view) {
                  return selected.compareTo(new GregorianCalendar(year, month, day)) == 0 ?
                          CalendarDay.SELECTED : CalendarDay.DEFAULT;
              }
          });

          return view;
        }

         googleUser = GoogleSignIn.getLastSignedInAccount(context);
         googleCalendarHelper = new GoogleCalendarHelper(context, context, getCredential());

         eventsRecyclerView = view.findViewById(R.id.events_list);
         calendarView = view.findViewById(R.id.calendar);
         dateName = view.findViewById(R.id.date_name);
         loadingEvents = (CircularProgressIndicator) view.findViewById(R.id.loadingEvents);
         emptyEventsListMessage = view.findViewById(R.id.emptyEventsListMessage);

         eventsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
         adapter = new EventAdapter(this, view.getContext(), eventsList);
         eventsRecyclerView.setAdapter(adapter);

          String _day = weekDays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
          String _month = yearMonth[current_month];
          dateName.setText(application.getFullDate());

         calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
              @Override
              public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                  currentInstance = Calendar.getInstance();
                  currentInstance.set(year, month + 1, day);
                  current_year = year;
                  current_month = month;
                  current_day = day;
                  dateName.setText(application.getFullDate(currentInstance));
                  update();
              }
          });

          currentInstance = Calendar.getInstance();
          getEvents();

         MainActivity.getInstance().binding.appbarlay.appbar.setOnMenuItemClickListener(item -> {
          if(application.isInternetAvailable(context)){
            if(item.getItemId() == R.id.reload){
              getEvents();
             } else if (item.getItemId() == R.id.add_event){
                 startActivityForResult(new Intent(context, CreateCalendarEventActivity.class), 1);
               }
            }
          return true;
       });

       MainActivity.getInstance().cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
          public void onAvailable(Network network) {
           if(connectionIsLost){
              getEvents();
              connectionIsLost = false;
             }
        }

         public void onLost(Network network) {
          getEvents();
          connectionIsLost = true;
        }
    });
          */
  /*
    public void getEvents(){
       new Thread(() -> {
       context.runOnUiThread(()->{
      if(application.isInternetAvailable(context)){
        loadingEvents.setVisibility(View.VISIBLE);
        emptyEventsListMessage.setVisibility(View.GONE);
        googleCalendarHelper.getEvents(this, ((MainActivity) context), googleUser.getEmail());
       } else {
        eventsList.clear();
        adapter.notifyAdd(eventsList);
        loadingEvents.setVisibility(View.GONE);
        emptyEventsListMessage.setVisibility(View.VISIBLE);
        emptyEventsListMessage.setText(getString(R.string.no_internet));
       }
       });
     }).start();
    }

    public void setEvents(MainActivity _context){
      Events events = googleCalendarHelper.events;
      eventsDataList.clear();

      for(Event event : events.getItems()){
         Date startDateTime = null;
         Date endDateTime = null;

       try {
         if(event.getStart().getDateTime() != null || event.getStart().getDate() != null){
           EventDateTime start = event.getStart();
           startDateTime = new Date(start.getDateTime() != null ? start.getDateTime().getValue() : start.getDate().getValue());
          }

         if(event.getEnd().getDateTime() != null || event.getEnd().getDate() != null){
           EventDateTime end = event.getEnd();
           endDateTime = new Date(end.getDateTime() != null ? end.getDateTime().getValue() : end.getDate().getValue());
          }

          boolean exist = false;

          for(com.liandev.materialcalendar.Object.Event _event : eventsDataList){
           if(_event.getId().equals(event.getId()))
           exist = true;
         }


          if(exist == false)
           eventsDataList.add(new com.liandev.materialcalendar.Object.Event(event.getId(), event.getSummary(), event.getDescription(), startDateTime, endDateTime, null));

         } catch (Exception e){
           Log.e("DisplayingEventsErrorTypeA", "ERROR", e);
        }

      }

      try {
      for(com.liandev.materialcalendar.Object.Event _event : eventsDataList){
          boolean isDeleted = true;

          for(Event event : events.getItems()){
           int pos = eventsDataList.indexOf(_event);
           if(_event.getId().equals(event.getId())){
              isDeleted = false;
               adapter.updateEvent(pos, new com.liandev.materialcalendar.Object.Event(event.getId(), event.getSummary(), event.getDescription(), new Date(event.getStart().getDateTime() == null ? event.getStart().getDate().getValue() : event.getStart().getDateTime().getValue()), new Date(event.getEnd().getDateTime() == null ? event.getEnd().getDate().getValue() : event.getEnd().getDateTime().getValue()), null));
             }
           }


           if(isDeleted){
            int pos = eventsDataList.indexOf(_event);
            eventsDataList.remove(pos);
            eventsList.remove(pos);
           }
         }

          } catch (Exception e){
           Log.e("DisplayingEventsErrorTypeB", "ERROR", e);
      }
      update();
    }
*/
/*
    public void delete(int position){
        eventsList.remove(position);
        adapter.notifyItemRemoved(eventsList.size());
        Toast.makeText(context, "L'événement a été supprimé de votre calendrier avec succès.",Toast.LENGTH_SHORT).show();
    }
*/
  public GoogleAccountCredential getCredential(){
    return GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES))
            .setBackOff(new ExponentialBackOff())
            .setSelectedAccount(googleUser.getAccount());
  }

  @Override
  public void onAttach(Activity _context) {
    super.onAttach(_context);
    context = _context;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }
}
