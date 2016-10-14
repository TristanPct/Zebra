package com.totris.zebra.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.totris.zebra.EditEmailFragment;
import com.totris.zebra.EditProfileItemActivity;
import com.totris.zebra.EditUsernameFragment;
import com.totris.zebra.Fragments.EditProfileListFragment;
import com.totris.zebra.Models.EditProfileFragmentType;
import com.totris.zebra.Models.MessageType;
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

    public void startEditIntent(EditProfileFragmentType fragmentType) {
        Intent intent = new Intent(this, EditProfileItemActivity.class);
        intent.putExtra("fragmentType", fragmentType.toString());
        Log.d(TAG, "startEditIntent: " + fragmentType);
        startActivity(intent);
    }

    @Override
    public void onEditUsernameClick() {
        startEditIntent(EditProfileFragmentType.USERNAME);
        Log.d(TAG, "onEditUsernameClick");
    }

    @Override
    public void onEditEmailClick() {
        startEditIntent(EditProfileFragmentType.EMAIL);
        Log.d(TAG, "onEditUsernameClick");
    }
}
