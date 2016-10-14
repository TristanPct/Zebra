package com.totris.zebra.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.totris.zebra.Fragments.EditProfileListFragment;
import com.totris.zebra.R;
import com.totris.zebra.Fragments.UserProfileFragment;

public class UserProfileActivity extends AppCompatActivity implements EditProfileListFragment.OnClickListener {

    private static String TAG = "UserProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.user_activity_profile, new UserProfileFragment())
                    .commit();
        }
    }

    @Override
    public void onEditUsernameClick() {
        Log.d(TAG, "onEditUsernameClick");
    }

    @Override
    public void onEditEmailClick() {
        Log.d(TAG, "onEditUsernameClick");
    }
}
