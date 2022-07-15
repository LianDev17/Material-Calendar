package com.liandev.materialcalendar.Object;

import android.app.Activity;
import android.view.View;
import com.liandev.materialcalendar.R;
import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.liandev.materialcalendar.Adapter.EventListAdapter;

public class CalendarDay {
  private Calendar cal;
  private RecyclerView rv;
  
  public CalendarDay(Calendar _cal, RecyclerView _rv) {
    this.cal = _cal;
    this.rv = _rv;
  }

  public Calendar getCal() {
    return this.cal;
  }

  public void setCal(Calendar cal) {
    this.cal = cal;
  }
  
  public RecyclerView getRv() {
    return this.rv;
  }
  
  public void setRv(RecyclerView _rv) {
    this.rv = _rv;
  }
  
}
