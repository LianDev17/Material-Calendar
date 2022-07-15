package com.liandev.materialcalendar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.content.ContentValues;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.liandev.materialcalendar.R;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.databinding.WelcomeActivityBinding;

public class WelcomeActivity extends AppCompatActivity {
    
  private WelcomeActivityBinding binding; 
  private Applications application = Applications.getInstance();
  private DatabaseManager db = new DatabaseManager(this);
  private WelcomeActivity instance;
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    instance = this;
    binding = WelcomeActivityBinding.inflate(getLayoutInflater());    	
    setContentView(binding.getRoot());
    
    Button startButton = findViewById(R.id.startButton);
    
    startButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(application.isInternetAvailable(instance)){
                    application.login(activityResultLauncher, instance);
                    } else {
                     Toast.makeText(instance, "Impossible de se connecter aux services Google sans connexion internet. Réesayez plus tard.", Toast.LENGTH_LONG).show();
                    }
                }          
            });
  }
  
  public ActivityResultLauncher<Intent> activityResultLauncher =
       registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(),
          new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult resultb) {
                ContentValues cv = new ContentValues();
                cv.put("google_sync", "" + true); 
                cv.put("google_tasks_sync", "" + true);
                db.updateItem("settings", "settings", cv);
                application.getInstance().restartApp(instance);
            }
          });

  @Override
  public void onBackPressed() {}
}
