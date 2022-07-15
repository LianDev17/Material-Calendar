package com.liandev.materialcalendar.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.liandev.materialcalendar.Activity.CreateCalendarEventActivity;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.GoogleCalendarHelper;
import com.liandev.materialcalendar.MainActivity;
import com.liandev.materialcalendar.databinding.ActivityMainBinding;
import com.liandev.materialcalendar.databinding.CreateCalendarEventActivityBinding;
import java.lang.Override;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.view.View;
import com.liandev.materialcalendar.R;

public class CreateCalendarEventActivity extends AppCompatActivity {
    private CreateCalendarEventActivityBinding binding;
    
    private Date startDate = new Date();
    private Date endDate = new Date();
    private boolean selectingDate = false;
    private boolean selectingHours = false;

    public DatabaseManager db = new DatabaseManager(this);
    public GoogleCalendarHelper googleCalendarHelper;
    private Applications application = Applications.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateCalendarEventActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appbar.setTitle(getString(R.string.event_new));
        
        googleCalendarHelper = new GoogleCalendarHelper(this, this, application.getCredential(this));
           
        setSupportActionBar(binding.appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   
           
        endDate.setHours(new Date().getHours() + 1);       
        
        setDateHoursText("date", binding.eventStartDate, startDate.getTime());
        setDateHoursText("hours", binding.eventStartHours, startDate.getTime());
        setDateHoursText("date", binding.eventEndDate, endDate.getTime());
        setDateHoursText("hours",binding.eventEndHours, endDate.getTime());
       
        eventDateSystem(binding.eventStartDate);
        eventDateSystem(binding.eventEndDate);
       
        eventHoursSystem(binding.eventStartHours);
        eventHoursSystem(binding.eventEndHours);
        
        binding.appbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                   
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);    
                    finish();
                }
            });
       
       binding.createEventButton.setOnClickListener(v -> {
             if(TextUtils.isEmpty(binding.createEventTitle.getText().toString()) == true){
              Toast.makeText(getApplicationContext(), "Donnez un nom à votre tâche avant de la créer", Toast.LENGTH_LONG).show();  
              } else if(startDate.getTime() > endDate.getTime()){
              Toast.makeText(getApplicationContext(), "La date de fin doit être après celle de début.", Toast.LENGTH_LONG).show();  
              } else {
               googleCalendarHelper.createEvent(binding.createEventTitle.getText().toString(), binding.createEventDescription.getText().toString(), startDate.getTime(), endDate.getTime());
               Intent i = new Intent();
               i.putExtra("TYPE","CALENDAR");
               setResult(Activity.RESULT_OK, i);  
               finish();        
             }
       });                        
    } 
    
    public void eventDateSystem(TextView text){
        text.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {  
             if(selectingHours == false && selectingDate == false){
               selectingDate = true; 
               long today = MaterialDatePicker.todayInUtcMilliseconds();      
             
               MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
               .setSelection(today)
               .build();
               
               materialDatePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
               @Override
                 public void onDismiss(DialogInterface dialog) {
                     selectingDate = false;     
                 }
             });
                                                        
               materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"); 
               
               materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                  @Override
                    public void onPositiveButtonClick(Long selection) {
                      setDateHoursText("date", text, selection);
               } 
            });              
            }  
          } 
       });
    }
    
    public void eventHoursSystem(TextView text){
     text.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
       if(selectingHours == false && selectingDate == false){
         selectingHours = true;
       final MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
       .setTimeFormat(TimeFormat.CLOCK_24H)
       .setHour(new Date().getHours())       
       .setMinute(new Date().getMinutes())
       .build();

      materialTimePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
               @Override
                 public void onDismiss(DialogInterface dialog) {
                     selectingHours = false;     
                 }
             });

       materialTimePicker.show(getSupportFragmentManager(), "tag");
       
       materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener(){
        public void onClick(View v){
        Date date = new Date();
        date.setHours(materialTimePicker.getHour());
        date.setMinutes(materialTimePicker.getMinute()); 
        setDateHoursText("hours", text, date.getTime()); 
              }
           });
           }
         }
      });
    } 
    
    public void setDateHoursText(String type, TextView text, Long date){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(date);
        
        int hour24hrs = cal.get(Calendar.HOUR_OF_DAY); 
        String minutes = cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : ""+ cal.get(Calendar.MINUTE);
        
        if(type.equals("date")){        
        text.setText(application.getFullDate(cal));        
        } else {
        text.setText(hour24hrs + ":" + minutes);
        } 
        
        if(text.getId() == binding.eventStartDate.getId()){
         startDate.setTime(date);
        } else {
         endDate.setTime(date);
        }
    }        
}   

