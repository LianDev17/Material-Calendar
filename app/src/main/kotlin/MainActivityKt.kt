package com.liandev.materialcalendar

import android.util.Log
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Network
import android.os.*
import android.view.MenuItem
import android.view.Menu
import android.view.View
import android.widget.Toast
import android.widget.TextView
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuItemCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.tasks.TasksScopes
import com.google.android.material.color.DynamicColors;
import com.itsaky.androidide.logsender.LogSender;
import com.liandev.materialcalendar.Activity.WelcomeActivity
import com.liandev.materialcalendar.Database.DatabaseManager
import com.liandev.materialcalendar.Fragments.CalendarFragment
import com.liandev.materialcalendar.Fragments.TasksFragment
import com.liandev.materialcalendar.databinding.ActivityMainBinding
import com.liandev.materialcalendar.Activity.AboutActivity;
import com.liandev.materialcalendar.Activity.SettingsActivity;
import androidx.appcompat.app.AppCompatDelegate;

import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide

import java.text.SimpleDateFormat
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays
import java.util.Calendar
import java.util.Date

public class MainActivityKt : AppCompatActivity() {
    
    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var cm: ConnectivityManager
    lateinit var snackbarLayout: CoordinatorLayout 
    lateinit var snackbar: Snackbar
    lateinit var db: DatabaseManager
    
    var connectionIsLost = false;
    
    val tasksFragment = TasksFragment();
    val calendarFragment = CalendarFragment();    
    @JvmField val EDIT_TASK_RESULT = 2;
    
    val application: Applications = Applications.getInstance()
  //  var instance: Context 
    
    override fun onCreate(savedInstanceState: Bundle?) {   
         instance = this
         installSplashScreen()
         super.onCreate(savedInstanceState)
         LogSender.startLogging(this@MainActivityKt)
         
         db = DatabaseManager(this);
         
         val cursor = db.getAllData("*", "settings");
         cursor.moveToFirst(); 
         
         if(!cursor.getString(1).toBoolean() == true){
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);    
        }
         
         DynamicColors.applyIfAvailable(this@MainActivityKt)
         binding = ActivityMainBinding.inflate(layoutInflater); 
         setContentView(binding.root)
        
         val googleUser = GoogleSignIn.getLastSignedInAccount(this)
        
         snackbarLayout = binding.snackbarLayout
         snackbar =  Snackbar.make(snackbarLayout, getString(R.string.no_internet_message), Snackbar.LENGTH_INDEFINITE)
         cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController
         
         getWindow().navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
     	setSupportActionBar(binding.appbar) 
         supportActionBar!!.setDisplayShowHomeEnabled(true) 
         supportActionBar!!.setDisplayHomeAsUpEnabled(true) 
         supportActionBar!!.setTitle(getString(R.string.tasks))
         
         NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
         snackbar.anchorView = binding.bottomNavigation
         connectionIsLost = !application.isInternetAvailable(this)
        
         with(binding) {
           if(googleUser == null){
               startActivity(Intent(this@MainActivityKt, WelcomeActivity::class.java));
            } else {
         bottomNavigation.setOnItemSelectedListener {
           if(it.itemId != bottomNavigation.getSelectedItemId()){
            var dayName = SimpleDateFormat("EEEE").format(Calendar.getInstance().getTime())
            
            dayName = dayName.replaceFirstChar(Char::titlecase)
            if(R.id.tasks == it.itemId)
            appbar.title = getString(R.string.tasks)
            navController.navigate(it.itemId);
         } 
          true;
       }
 
         navViewNavigationViewActivity.getHeaderView(0).findViewById<TextView>(R.id.tvName_HeaderNavView).setText(googleUser.displayName)
         navViewNavigationViewActivity.getHeaderView(0).findViewById<TextView>(R.id.tvEmail_HeaderNavView).setText(googleUser.email)
         setUserProfileImage(googleUser.getPhotoUrl().toString())
         
            val actionToggle = ActionBarDrawerToggle( 
                this@MainActivityKt, 
                drawerNavigationViewActivity, 
                R.string.open, 
                R.string.close 
            ) 
 
            drawerNavigationViewActivity.addDrawerListener(actionToggle) 
            actionToggle.syncState() 
            } 
         }
   } 
      
   fun setUserProfileImage(uri: String){
       val profileImage = binding.navViewNavigationViewActivity.getHeaderView(0).findViewById<CircleImageView>(R.id.user_picture);

      if(uri != null){
         Glide
          .with(this)
          .load(uri)
          .into(profileImage);
        }  
   }
   
   fun setAppbarTitle(title: String){
       binding.appbar.title = title
   }
   
   @JvmField val result = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()){
    // Handle the returned Uri
   }  
   
   val networkCallback = object: ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network: Network) {
         if(connectionIsLost){
            toast("Connexion disponible.");
            snackbar.dismiss(); 
            connectionIsLost = false;
           }
      }
     
     override fun onLost(network: Network) {
        connectionIsLost = true; 
        snackbar.show();
      }
    };
      
    override fun onOptionsItemSelected(item: MenuItem): Boolean { 
        when (item.itemId) { 
            android.R.id.home -> with(binding) { 
                if (!drawerNavigationViewActivity.isDrawerOpen(GravityCompat.START)) { 
                    drawerNavigationViewActivity.open() 
                } 
            } 
        } 
        return true 
    }
    
  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    getMenuInflater().inflate(R.menu.appbar_items, menu)
    return true
  }
    
    
    fun toast(message: String){
      Thread {
         runOnUiThread {
          Toast.makeText(this@MainActivityKt, message, Toast.LENGTH_LONG).show(); 
        }
      }.start();  
    } 
    
    companion object {
        private var instance: MainActivityKt? = null
        
        @JvmStatic
        fun getInstance(): MainActivityKt? {
            return instance
        }
    }
    
    init {
        instance = this@MainActivityKt
    }
} 