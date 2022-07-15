package com.liandev.materialcalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.content.ContentValues;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.color.DynamicColors;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.drive.DriveScopes;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.MainActivityKt;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
 
import com.liandev.materialcalendar.Internet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Applications extends Application {
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	public static final String NIGHT_MODE = "NIGHT_MODE";
    public Boolean isNightModeEnabled = false;
    public String DATABASE_PATH = "User.db";
    public DatabaseManager db;
    public SignInCredential credential;
    public boolean result = false;   
    public static String[] SCOPES = {CalendarScopes.CALENDAR, TasksScopes.TASKS};
    
    private static Applications singleton = null;

	public static Applications getInstance() {

        if(singleton == null)
        {
            singleton = new Applications();
        }
        
        return singleton;
    }
    
    public DatabaseManager getDatabase(){
        return db;
    }
    
    @Override
    public void onCreate() {  
        new Thread(() -> {   
          result = new Internet(getApplicationContext()).isConnectingToInternet();
       }).start(); 
       
      this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.putExtra("error", getStackTrace(ex));
				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				uncaughtExceptionHandler.uncaughtException(thread, ex);
			}
		});
        super.onCreate();  
        db = new DatabaseManager(this);
        DynamicColors.applyToActivitiesIfAvailable(this);
       // initDarkMode(this);
    }
	
   private String getStackTrace(Throwable th){
	final Writer result = new StringWriter();
	final PrintWriter printWriter = new PrintWriter(result);
	Throwable cause = th;
	while(cause != null){
		cause.printStackTrace(printWriter);
		cause = cause.getCause();
	}
	final String stacktraceAsString = result.toString();
	printWriter.close();
	return stacktraceAsString;
    }
    
    public void initDarkMode(Context context){	
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                this.isNightModeEnabled = true;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                this.isNightModeEnabled = false;
                break;  
        }   
    } 
    
    public void changeDarkMode(Context context, boolean dmEnabled){
        
       if(!dmEnabled){           
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);  
         } else {
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        restartApp(context);
       /* ContentValues cv = new ContentValues();
        cv.put("dark_mode", "" + isNightModeEnabled);
        db.updateItem("settings", "settings", cv); */                 
      //  initDarkMode(context);
    }
    
    public void requestSignIn(ActivityResultLauncher<Intent> activityResultLauncher, Context context, boolean connectToGoogle) {
      GoogleSignInOptions signInOptions =
			new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)		
	        .requestEmail()
		    .requestScopes(new Scope(CalendarScopes.CALENDAR))
            .build();
            
		GoogleSignInClient client = GoogleSignIn.getClient(context, signInOptions);		  
       if(connectToGoogle){
		activityResultLauncher.launch(client.getSignInIntent());	    
        } else {
         client.signOut();
         restartApp(context);
        }   
    } 
    
   public void login(ActivityResultLauncher<Intent> activityResultLauncher, Context context){
    requestSignIn(activityResultLauncher, context, true);
  }
  
  public void logout(Context context){
   requestSignIn(null, context, false);   
  }

   public void restartApp(Context context) {
    Intent intent = new Intent(context, MainActivityKt.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }
  
  public boolean isInternetAvailable(Context context) {
   ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }
  
  public String getFullDate(Calendar cal){
   DateTimeFormatter userFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);      
   String dayName = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).format(userFormatter);
   return dayName;
  }
  
  public String getFullDate(){
     Calendar cal = Calendar.getInstance();
     Date d = new Date();
     d.setMonth(d.getMonth() + 1);
     cal.setTime(d);
     return getFullDate(cal);
  }
  
  public GoogleAccountCredential getCredential(Context context){
          GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(context);     
           return GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES))
          .setBackOff(new ExponentialBackOff())
          .setSelectedAccount(googleUser.getAccount());
    }      
}