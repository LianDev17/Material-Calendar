package com.liandev.materialcalendar.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.widget.Toast;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.MainActivity;
import com.liandev.materialcalendar.databinding.CreateNoteActivityBinding;
import java.lang.Override;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import org.apache.http.util.TextUtils;
import android.view.View;



public class CreateNoteActivity extends AppCompatActivity {
   private CreateNoteActivityBinding binding;
    
   public DatabaseManager db = new DatabaseManager(this);
   Context context;
    
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = CreateNoteActivityBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    binding.createNoteAppbar.setTitle("Nouvelle note");    
    context = this;
          
    binding.createNoteButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) { 
             String title = binding.noteTitle.getText().toString();
             String description = binding.noteDescription.getText().toString();
             String secondary_description = binding.noteSecondaryDescription.getText().toString();   
                  
             if(TextUtils.isEmpty(title) == true && TextUtils.isEmpty(description) && TextUtils.isEmpty(secondary_description)){
              Toast.makeText(getApplicationContext(), "Vous devez entrer des caractères dans un moins un champ de saisie.", Toast.LENGTH_LONG).show();  
              } else {
              Long creation_time = new Date().getTime();
              ContentValues event = new ContentValues();
               event.put("id", creation_time);
               event.put("note_title",title); 
               event.put("note_description",description);  
               event.put("note_secondary_description",secondary_description);             
               event.put("note_creation_time", "" + creation_time);               
               db.insertItem("notes", event);                 
               Intent i = new Intent();
               i.putExtra("ACTION", "CREATE");
               i.putExtra("NOTE_POSITION", getIntent().getIntExtra("NOTE_POSITION", 3));
               setResult(Activity.RESULT_OK, i);  
               finish();    
           }
        }
     });    
  }
}
