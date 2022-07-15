package com.liandev.materialcalendar.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.elevation.SurfaceColors;

import com.liandev.materialcalendar.BuildConfig;
import com.liandev.materialcalendar.R;

public class AboutActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.about_layout);

        TextView version = findViewById(R.id.version);
        TextView version_available = findViewById(R.id.version_available);
        
        version.setText("Version " + BuildConfig.VERSION_NAME);
        version_available.setText("Votre application est à jour.");
    }
    
}
