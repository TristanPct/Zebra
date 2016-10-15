package com.totris.zebra.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.totris.zebra.Fragments.EditUsernameFragment;
import com.totris.zebra.Fragments.EditEmailFragment;
import com.totris.zebra.Models.EditProfileFragmentType;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

public class EditProfileItemActivity extends AppCompatActivity implements EditEmailFragment.OnSubmitListener, EditUsernameFragment.OnSubmitListener {

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

    @Override
    public void onEmailSubmit(String email) {
        User user = User.getCurrent();

        user.updateMail(email).commit();

        Intent intent = new Intent(EditProfileItemActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUsernameSubmit(String username) {
        User user = User.getCurrent();

        user.updateUsername(username).commit();

        Intent intent = new Intent(EditProfileItemActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }
}
