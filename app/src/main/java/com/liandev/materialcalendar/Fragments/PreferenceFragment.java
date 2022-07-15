package com.liandev.materialcalendar.Fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.os.Bundle;
import android.database.Cursor;
import android.content.ContentValues;
import android.view.View;
import android.content.res.Configuration;
import android.content.Context;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.liandev.materialcalendar.R;
import com.liandev.materialcalendar.Applications; 
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.Activity.SettingsActivity;


public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {    
    private DatabaseManager db;
    
    private boolean darkModeEnabled = Applications.getInstance().isNightModeEnabled;
    private boolean removeOutdatedTaskEnabled;
    private boolean googleSyncIsEnabled;
    private boolean googleCalendarSyncIsEnabled;
    private boolean googleTasksSyncIsEnabled;
    private SwitchPreference darkmode;
    private SwitchPreference removeOutdatedTask;
    private Preference disconnectButton;
    
    private Applications application = Applications.getInstance();
    
    
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        try {
         addPreferencesFromResource(R.xml.preferences);
         db = ((SettingsActivity) getActivity()).db;
         
         Cursor settings = db.getAllData("*", "settings");
         settings.moveToFirst(); 
                  
         darkmode = findPreference("dark_mode_switch");
         disconnectButton = findPreference("disconnect");
         removeOutdatedTask = findPreference("remove_outdated_task_switch");
         
         darkModeEnabled = isDarkMode();//Boolean.parseBoolean(settings.getString(1));
         removeOutdatedTaskEnabled = Boolean.parseBoolean(settings.getString(2));

         disconnectButton.setOnPreferenceClickListener(this);
         darkmode.setOnPreferenceChangeListener(this);
         removeOutdatedTask.setOnPreferenceChangeListener(this);
         
         Drawable icCalendarSync = getResources().getDrawable(R.drawable.ic_calendar_sync);
        // icCalendarSync.setColorFilter(Color.parseColor("#C7C7C7"), PorterDuff.Mode.SRC_ATOP);
         
        // darkmode.setSummary(darkModeEnabled ? "Activé" : "Désactivé");   
         darkmode.setIcon(darkModeEnabled ? R.drawable.weather_night_white : R.drawable.weather_night);            
         darkmode.setChecked(darkModeEnabled);     
         
         removeOutdatedTask.setChecked(removeOutdatedTaskEnabled);    
         removeOutdatedTask.setIcon(darkModeEnabled ? R.drawable.task_outdated_white : R.drawable.task_outdated);     
         
         
        } catch (Exception e){
         Log.e("PreferenceFragmentError: ", "exception", e);
        }
      }
      
      public boolean isDarkMode(){	
        boolean isDarkMode = false;  
        switch (getActivity().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                isDarkMode = true;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                isDarkMode = false;
                break; 
        } 
        return isDarkMode;  
    }  
	
    @Override 
    public boolean onPreferenceClick(Preference preference){
      String preferenceKey = preference.getKey();
      ContentValues cv = new ContentValues();
      if(preferenceKey.equals(disconnectButton.getKey())){
          application.logout(getActivity());	
       }   
      return true;  
    }
      
	@Override
     public boolean onPreferenceChange(Preference preference, Object _switchEnabled) {  
         Boolean switchEnabled = (Boolean) _switchEnabled;
         String preferenceKey = preference.getKey();
         ContentValues cv = new ContentValues();
         ContentValues userCV = new ContentValues();
         
         if(preferenceKey.equals(darkmode.getKey())){
          cv.put("dark_mode", "" + !switchEnabled);
          db.updateItem("settings", "settings", cv);
          application.changeDarkMode(getActivity(), !switchEnabled);
         } else if(preferenceKey.equals(removeOutdatedTask.getKey())){
          cv.put("remove_outdated_task", "" + switchEnabled);
          db.updateItem("settings", "settings", cv);
         }
         
         return true;
    }
}