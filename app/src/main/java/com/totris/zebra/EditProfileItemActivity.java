package com.totris.zebra;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.totris.zebra.Models.EditProfileFragmentType;
import com.totris.zebra.Models.MessageType;

public class EditProfileItemActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_item);

        String fragmentType = getIntent().getStringExtra("fragmentType");

        Log.d(TAG, "onCreate: " + fragmentType);

        Fragment currentFragment;
        
        if(fragmentType.equals(EditProfileFragmentType.USERNAME.toString())) {
            Log.d(TAG, "onCreate: text");
            currentFragment = new EditUsernameFragment();
        }else if(fragmentType.equals(EditProfileFragmentType.EMAIL.toString())) {
            Log.d(TAG, "onCreate: text");
            currentFragment = new EditEmailFragment();
        } else {
            currentFragment = new Fragment();
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_edit_profile_item, currentFragment)
                    .commit();
        }
    }
}
