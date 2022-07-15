package com.liandev.materialcalendar;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.liandev.materialcalendar.Activity.AboutActivity;
import com.liandev.materialcalendar.Activity.SettingsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileDialog extends BottomSheetDialogFragment {
  
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.userprofile_bottom_sheet_layout, container, false);
  
        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(getActivity());
  
  
        TextView username = v.findViewById(R.id.userprofile_username);
        TextView email = v.findViewById(R.id.userprofile_email);
        CircleImageView profileImage = v.findViewById(R.id.userprofile_image);
        Button settings_button = v.findViewById(R.id.userprofile_settings);
        Button about_button = v.findViewById(R.id.userprofile_about);
        
        if(googleUser.getPhotoUrl() != null){
        Glide
          .with(getActivity())
          .load(googleUser.getPhotoUrl().toString())
          .into(profileImage); 
        } else {
         profileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty_account));
        }  
        username.setText(googleUser.getDisplayName());
        email.setText(googleUser.getEmail());
         
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(getActivity(), SettingsActivity.class), 2);
                dismiss();
            }
        });
  
        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), AboutActivity.class));
                dismiss();
            }
        });
      // getActivity().getWindow().setLayout(300 /*our width*/, ViewGroup.LayoutParams.MATCH_PARENT);
        return v;
  }
}
