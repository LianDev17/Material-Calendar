package com.liandev.materialcalendar.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.MainActivity;
import com.liandev.materialcalendar.Object.Note;
import com.liandev.materialcalendar.databinding.CreateNoteActivityBinding;
import com.liandev.materialcalendar.databinding.EditNoteActivityBinding;
import java.lang.Override;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import org.apache.http.util.TextUtils;
import android.view.View;


public class EditNoteActivity extends AppCompatActivity {
  private EditNoteActivityBinding binding;
  public DatabaseManager db = new DatabaseManager(this);
  Context context;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = EditNoteActivityBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    binding.editNoteAppbar.setTitle("Modification de la note");
   
    String noteId = "" + Long.parseLong(getIntent().getStringExtra("NOTE_ID"));
    Log.i("noteId", noteId);  
    Note noteData = db.getNoteData("*", "notes", noteId); 
    
    context = this;
    
    if(noteData == null){
    finish();  
    } else {
    binding.noteTitle.setText(noteData.getName());   
    binding.noteDescription.setText(noteData.getDescription());
    binding.noteSecondaryDescription.setText(noteData.getSecondaryDescription());
           
    binding.editNoteButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) { 
             String title = binding.noteTitle.getText().toString();
             String description = binding.noteDescription.getText().toString();
             String secondary_description = binding.noteSecondaryDescription.getText().toString();   
                  
             if(TextUtils.isEmpty(title) == true && TextUtils.isEmpty(description) && TextUtils.isEmpty(secondary_description)){
              Toast.makeText(getApplicationContext(), "Vous devez entrer des caractères dans un moins un champ de saisie.", Toast.LENGTH_LONG).show();  
              } else {
               ContentValues event = new ContentValues();        
               event.put("note_title",title); 
               event.put("note_description",description);  
               event.put("note_secondary_description",secondary_description);                                    
               db.updateItem("notes", noteId, event);                  
               Intent i = new Intent();
               i.putExtra("ACTION", "EDIT");
               i.putExtra("NOTE_POSITION", getIntent().getIntExtra("NOTE_POSITION", 0));
               setResult(Activity.RESULT_OK, i);      
               finish();   
           }
        }
     });    
     
     binding.deleteNoteButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {                 
               db.removeItem("notes", noteId);   
               Intent i = new Intent();
               i.putExtra("ACTION", "DELETE");
               setResult(Activity.RESULT_OK, i);             
               finish();     
               }                
       });  
     }                 
  }
}
