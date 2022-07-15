package com.liandev.materialcalendar.Fragments;

import android.app.Activity;
import static android.app.Activity.RESULT_OK;

import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import androidx.core.view.GravityCompat;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.liandev.materialcalendar.Activity.CreateCalendarEventActivity;
import com.liandev.materialcalendar.Activity.CreateTaskActivity;
import com.liandev.materialcalendar.Applications;
import com.liandev.materialcalendar.Database.DatabaseManager;
import com.liandev.materialcalendar.DynamicFragment;
import com.liandev.materialcalendar.GoogleTasksHelper;
import com.liandev.materialcalendar.MainActivityKt;
import com.liandev.materialcalendar.R;
import com.liandev.materialcalendar.TaskListsViewPagerAdapter;
import com.liandev.materialcalendar.databinding.FragmentTasksBinding;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import com.liandev.materialcalendar.Activity.AboutActivity;
import com.liandev.materialcalendar.Activity.SettingsActivity;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import java.util.Arrays;

public class TasksFragment extends Fragment {

  private static final String[] SCOPES = {CalendarScopes.CALENDAR, TasksScopes.TASKS};

  private RecyclerView taskListsRecyclerView;
  private DatabaseManager db;
  private ViewPager2 viewPager;
  public TabLayout tabLayout;

  public TaskListsViewPagerAdapter adapter;
  private TextView notConnectedToGoogleMessage;
  // private SwipeRefreshLayout refresh;
  private GoogleSignInAccount googleUser;
  private Context context;
  private Applications application = Applications.getInstance();

  private GoogleTasksHelper googleTasksHelper;
  private FragmentTasksBinding binding;
  private boolean connectionIsLost = false;
  private boolean isAllFabsVisible = false;
  
  public TasksFragment() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentTasksBinding.inflate(inflater, container, false);

    googleUser = GoogleSignIn.getLastSignedInAccount(context);
    
    if(googleUser != null){
    googleTasksHelper = new GoogleTasksHelper(getActivity(), context, application.getCredential(context));

    tabLayout = binding.tasksTablayout;
    viewPager = binding.viewpager;
    // refresh = (SwipeRefreshLayout) view.findViewById(R.id.tasksRefreshLayout);
    notConnectedToGoogleMessage = binding.notConnectedToGoogleMessage;

    MainActivityKt.getInstance().setAppbarTitle(getString(R.string.tasks));

    adapter = new TaskListsViewPagerAdapter(this);

    viewPager.setAdapter(adapter);
    viewPager.setUserInputEnabled(false);
    viewPager.setSaveEnabled(false);

    MainActivityKt.getInstance().binding.navViewNavigationViewActivity.setNavigationItemSelectedListener( menuItem -> {
           switch (menuItem.getItemId()) { 
                   case R.id.reload: adapter.tasksFragment.get(tabLayout.getSelectedTabPosition()).getTaskLists(); break;
                   case R.id.settings: startActivity(new Intent(context, SettingsActivity.class)); break;
                   case R.id.about: startActivity(new Intent(context, AboutActivity.class)); break;
                } 
          MainActivityKt.getInstance().binding.drawerNavigationViewActivity.closeDrawer(GravityCompat.START);      
              return true;
     });
               
    new TabLayoutMediator(
            tabLayout,
            viewPager,
            new TabLayoutMediator.TabConfigurationStrategy() {
              @Override
              public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(adapter.getList(position).getName());
               
              }
            })
        .attach();
        
     getTaskLists();
     
      FabSpeedDial fabSpeedDial = MainActivityKt.getInstance().binding.addTask;
      fabSpeedDial.setVisibility(View.VISIBLE);
      
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
               if(menuItem.getItemId() == R.id.add_task_list){
                 MaterialAlertDialogBuilder create_file_dialog = new MaterialAlertDialogBuilder(context);   
                 create_file_dialog.setTitle(getString(R.string.task_new_list));   
                 View input = inflater.inflate(R.layout.create_file_dialog, null);
                 create_file_dialog.setView(input);
                 EditText file_name_input = (EditText) input.findViewById(R.id.textField);
                                                                                                                  
                  create_file_dialog.setPositiveButton(getString(R.string.create), (dialog, which) -> {
                     googleTasksHelper.createTaskList(file_name_input.getText().toString());
                   });  
                  create_file_dialog.show(); 
               } else {
                 Intent i = new Intent(context, CreateTaskActivity.class);
                 i.putExtra("pos","" + adapter.getList(tabLayout.getSelectedTabPosition()).getId());
                 startActivityForResult(i, 0);
               }
              return false;
            }
        });
     
     MainActivityKt.getInstance().cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
        public void onAvailable(Network network) {
         if(connectionIsLost){
            getTaskLists();
            connectionIsLost = false;
           }
      }
     
       public void onLost(Network network) {
        getTaskLists();
        connectionIsLost = true;
      }
  });
     
    /*  refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
         public void onRefresh() {
        if(googleUser != null)
          getTaskLists(googleUser.getAccount().name);
        }
  });*/
}
    return binding.getRoot();
  }

  public void getTaskLists() {
          new Thread(
            () -> {
              getActivity().runOnUiThread(
                  () -> {
                    if (application.isInternetAvailable(context)) {
                      try {
                             googleTasksHelper.getTaskLists(MainActivityKt.getInstance(), this);
                      } catch (Exception e) {
                        Log.e("GetTasksError", "", e);
                      }
                      notConnectedToGoogleMessage.setVisibility(View.GONE);
                    } else {
                      adapter.clear();
                      notConnectedToGoogleMessage.setVisibility(View.VISIBLE);
                      notConnectedToGoogleMessage.setText(
                          getString(R.string.no_internet));
                    }
                  });
            })
        .start();
  }

  public void setTaskLists(MainActivityKt _context) {
    TaskLists lists = googleTasksHelper.tasklists;
    DynamicFragment fView = new DynamicFragment();
    
    try {

      for (TaskList list : lists.getItems()) {
        boolean exist = false;
        for (com.liandev.materialcalendar.Object.TaskList taskList : adapter.taskLists) {
          if (taskList.getId().equals(list.getId())) exist = true;
        }
        
        if (exist == false)
          adapter.addFrag(fView, list.getId(), list.getTitle());
      }

      for (com.liandev.materialcalendar.Object.TaskList taskList : adapter.taskLists) {
        boolean exist = false;
        for (TaskList list : lists.getItems()) {
          if (taskList.getId().equals(list.getId())) exist = true;
        }

        int pos = adapter.taskLists.indexOf(taskList);
        if (exist == false) {
          adapter.taskLists.remove(pos);
          adapter.notifyItemRemoved(pos);
        }
      }
    } catch (Exception e) {
      Log.e("TaskError", "", e);
    }

    // refresh.setRefreshing(false);
  }

  public GoogleAccountCredential getCredential() {
    return GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES))
        .setBackOff(new ExponentialBackOff())
        .setSelectedAccount(googleUser.getAccount());
  }

  @Override
  public void onAttach(@NonNull Context _context) {
    super.onAttach(context); 
    context = _context;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
   public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

}
