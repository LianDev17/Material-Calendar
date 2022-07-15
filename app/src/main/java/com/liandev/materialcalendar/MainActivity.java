package com.liandev.materialcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.*;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.MenuItemCompat;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.TasksScopes;
//import com.itsaky.androidide.logsender.LogSender;
import com.liandev.materialcalendar.Activity.WelcomeActivity;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.Fragments.CalendarFragment;
import com.liandev.materialcalendar.Fragments.TasksFragment;
import com.liandev.materialcalendar.databinding.ActivityMainBinding;
import de.hdodenhof.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
   
  public ActivityMainBinding binding; 
  private static final String SAVED_IS_SIGNED_IN = "isSignedIn";
  private static final int REQUEST_FOR_ACTIVITY_CODE = 0; 
  private static final String[] SCOPES = {CalendarScopes.CALENDAR, TasksScopes.TASKS};

  public TasksFragment tasksFragment = new TasksFragment();
  public CalendarFragment calendarFragment = new CalendarFragment();    
  
  public GoogleAccountCredential mCredential;
    
  public int CREATE_TASK_RESULT = 1;
  public int EDIT_TASK_RESULT = 2;
  public boolean status = false;
  public String currentListId;
  
  public String weekDays[] =
      new String[] {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
  public String yearMonth[] =
      new String[] {
        "janvier",
        "février", 
        "mars",
        "avril",
        "mai",
        "juin",
        "juillet",
        "août",
        "septembre",
        "octobre",
        "novembre",
        "décembre"
      };
     
  public DatabaseManager db = new DatabaseManager(this);
  public static MainActivity instance;
  public GoogleTasksHelper googleTasksHelper;
  public GoogleCalendarHelper googleCalendarHelper;
  public ConnectivityManager cm;
  private CoordinatorLayout mSnackbarLayout;
  private Snackbar snackbar;
  private boolean connectionIsLost = false;
  private Applications application = Applications.getInstance();
  
  
  public static MainActivity getInstance(){
     return instance;
  } 

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    instance = this; 
    
    SplashScreen.installSplashScreen(this);  
    super.onCreate(savedInstanceState);
    // Init Binding
    binding = ActivityMainBinding.inflate(getLayoutInflater());    	
    setContentView(binding.getRoot());

     mSnackbarLayout = findViewById(R.id.snackbar_layout);
    
     snackbar =  Snackbar.make(mSnackbarLayout, getString(R.string.no_internet_message), Snackbar.LENGTH_INDEFINITE);
     cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    

     getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

    /* NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
     NavController navController = navHostFragment.getNavController();
     BottomNavigationView bottomNav = binding.bottomNavigation;
     NavigationUI.setupWithNavController(bottomNav, navController);
     binding.appbar.setTitle(getString(R.string.tasks)); 
     
     bottomNav.setOnItemSelectedListener(item -> {
      return true;
   });
    snackbar.setAnchorView(binding.bottomNavigation);
     */
    connectionIsLost = !application.isInternetAvailable(this);
    
    if(GoogleSignIn.getLastSignedInAccount(instance) == null){
    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
   } else {
    initGoogleTasksServices();

    String dayGoodName = new Date().getHours() < 17 && new Date().getHours() > 3 ? getString(R.string.hello) : getString(R.string.good_evening);
    Cursor user = db.getItemData("welcomeMessage", "user", "user");

     if(Boolean.parseBoolean(user.getString(0)) == false){
               new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(getString(R.string.welcome_title))
                        .setMessage(dayGoodName + " " + getString(R.string.welcome_message))
                        .setPositiveButton(getString(R.string.okay_thanks), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                             ContentValues user = new ContentValues();
                             user.put("welcomeMessage", "true");
                             db.updateItem("user", "user", user);
                            }
                }).create().show();                       
    }  
  }       
      if(!isInternetConnected())
       snackbar.show();
    
      cm.registerDefaultNetworkCallback(networkCallback);
  }
  
  public void initGoogleTasksServices(){
      
     GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(instance); 
      mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccount(googleUser.getAccount());   


    /*    MenuItem menuItem = binding.appbarlay.appbar.getMenu().findItem(R.id.userProfile);
         View view = MenuItemCompat.getActionView(menuItem);

        CircleImageView profileImage = view.findViewById(R.id.appbar_userprofile_image);


      if(googleUser.getPhotoUrl() != null){
        Glide
          .with(this)
          .load(googleUser.getPhotoUrl().toString())
          .into(profileImage);
       } else {
         profileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_account));
       }
  

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UserProfileDialog bottomSheet = new UserProfileDialog();
                    bottomSheet.show(getSupportFragmentManager(),
                                     "ModalBottomSheet");
            }
        });
    */
        
    
  }
  
  
  public boolean isInternetConnected() {
   status = cm != null && cm.getActiveNetwork() != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null ? true : false;
   return status;
 }
  
      @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        
        super.onActivityResult(requestCode, resultCode, data);             
        
        try {
       // if(data.getStringExtra("TYPE") != null)
      //  calendarFragment.getEvents(instance);
        } catch (Exception e){}
        
         switch (requestCode) {
             case REQUEST_FOR_ACTIVITY_CODE:
        /*       if(resultCode == Activity.RESULT_OK){
                 if(data.getStringExtra("ACTION").equals("DELETE")){             
                   calendarFragment.delete(data.getIntExtra("EVENT_POSITION", 0)); 
                } else {
           //   calendarFragment.update(); 
            }
        } else if(requestCode == 1 && resultCode == Activity.RESULT_OK){
           if(data.getStringExtra("ACTION").equals("DELETE")){             
              noteFragment.delete(data.getIntExtra("EVENT_POSITION", 0)); 
               } else {
                 noteFragment.update();
              }
            }*/
                break;
         }
    }  
    
    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        public void onAvailable(Network network) {
         if(connectionIsLost){
         toast("Connexion disponible.");
         snackbar.dismiss(); 
         connectionIsLost = false;
           }
      }
     
       public void onLost(Network network) {
        connectionIsLost = true; 
        snackbar.show();
      }
    };
    
    public void toast(String message){
      new Thread(() -> {
         runOnUiThread(()->{
          Toast.makeText(instance, message, Toast.LENGTH_LONG).show(); 
        });
      }).start();  
    }
}