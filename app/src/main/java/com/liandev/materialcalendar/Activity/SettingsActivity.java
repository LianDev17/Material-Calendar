package com.liandev.materialcalendar.Activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.SwitchPreference;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.MainActivity;
import com.liandev.materialcalendar.R;

public class SettingsActivity extends AppCompatActivity {
  private Toolbar appbar;
  public static final int RC_CODE_SIGN_IN = 1;

  private boolean current_signin_istasks = false;
  private static final String PREF_ACCOUNT_NAME = "accountName";

  private Applications application = Applications.getInstance();
  public DatabaseManager db = new DatabaseManager(this);

  private SwitchPreference googleSyncSwitch;
  private SwitchPreference googleCalendarSyncSwitch;
  private SwitchPreference googleTasksSyncSwitch;

  private GoogleSignInClient mGoogleSignInClient;

  SettingsActivity instance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      setContentView(R.layout.settings_layout);
      instance = this;

      appbar = findViewById(R.id.settings_appbar);

      setSupportActionBar(appbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      appbar.setNavigationOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent();
              setResult(Activity.RESULT_OK, i);
              finish();
            }
          });
    } catch (Exception e) {
      Log.e("preferenceFragmentError: ", "exception", e);
    }
  }
}
