package com.liandev.materialcalendar.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.R;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.liandev.materialcalendar.GoogleTasksHelper;
import com.liandev.materialcalendar.MainActivity;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskTitle;
    private EditText taskDescription;
    private EditText taskEndDate;
    private EditText taskEndHours;
    private Long endDateTime = null;
    
    public GoogleTasksHelper googleTasksHelper;
    public static CreateTaskActivity instance;
    private Applications application = Applications.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_layout);

        googleTasksHelper = new GoogleTasksHelper(this, this, application.getCredential(this));

        String pos = getIntent().getStringExtra("pos");

        taskTitle = findViewById(R.id.create_task_title);
        taskDescription = findViewById(R.id.create_task_description);
        taskEndDate = findViewById(R.id.create_task_end_date);
       // taskEndHours = findViewById(R.id.create_task_end_time);

        Toolbar topAppBar = findViewById(R.id.create_task_appbar);

        setSupportActionBar(topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        topAppBar.setTitle(getString(R.string.task_new));

        Button delete_task = findViewById(R.id.delete_task_button);
        delete_task.setEnabled(false);

        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                });

        topAppBar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) { 
                        if (item.getItemId() == R.id.create_task) {
                            try {
                                if (TextUtils.isEmpty(taskTitle.getText().toString()) == true) {
                                    Toast.makeText(
                                                    getApplicationContext(),
                                                    "Donnez un nom à votre tâche avant de la créer",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                } else {
                                    googleTasksHelper.createTask(pos, taskTitle.getText().toString(), taskDescription.getText().toString(), endDateTime);
                                    finish();
                                }
                            } catch (Exception e) {
                                Toast.makeText(
                                                getApplicationContext(),
                                                e.getMessage(),
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                        return false;
                    }
                });

        final Calendar task_date = Calendar.getInstance(TimeZone.getDefault());
        endDateTime = task_date.getTimeInMillis();

        taskEndDate.setOnClickListener(v -> {
                        MaterialDatePicker.Builder materialDateBuilder =
                                MaterialDatePicker.Builder.datePicker();

                        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                        materialDatePicker.show(
                                getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

                        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {

                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                        task_date.setTimeInMillis(selection);

                                        taskEndDate.setText(materialDatePicker.getHeaderText());
                                        endDateTime = task_date.getTimeInMillis();
                                        }
                                });
                });
      /*  taskEndHours.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final MaterialTimePicker materialTimePicker =
                                new MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_24H)
                                        .setHour(new Date().getHours())
                                        .setMinute(new Date().getMinutes())
                                        .build();

                        materialTimePicker.show(getSupportFragmentManager(), "tag");

                        materialTimePicker.addOnPositiveButtonClickListener(
                                new View.OnClickListener() {
                                    public void onClick(View v) {
                                        final String hours =
                                                materialTimePicker.getHour() < 10
                                                        ? "0" + materialTimePicker.getHour()
                                                        : "" + materialTimePicker.getHour();
                                        final String minutes =
                                                materialTimePicker.getMinute() < 10
                                                        ? "0" + materialTimePicker.getMinute()
                                                        : "" + materialTimePicker.getMinute();

                                        Date date = new Date();
                                        date.setTime(
                                                endDateTime == null
                                                        ? new Date().getTime()
                                                        : endDateTime);

                                        task_date.set(
                                                Calendar.getInstance().get(Calendar.YEAR),
                                                date.getMonth(),
                                                date.getDate(),
                                                materialTimePicker.getHour(),
                                                materialTimePicker.getMinute(),
                                                0);
                                        taskEndHours.setText(hours + ":" + minutes);
                                        endDateTime = task_date.getTimeInMillis();
                                    }
                                });
                    }
                });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_task_appbar_items, menu);
        return true;
    }
}
