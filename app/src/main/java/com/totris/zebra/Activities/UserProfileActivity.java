package com.totris.zebra.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.totris.zebra.Fragments.EditProfileListFragment;
import com.totris.zebra.Models.EditProfileFragmentType;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;
import com.totris.zebra.Fragments.UserProfileFragment;

public class UserProfileActivity extends DrawerMenuActivity implements EditProfileListFragment.OnClickListener {

    private static String TAG = "UserProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_user_profile, null, false);
        contentLayout.addView(contentView, 0);

        String userId = getIntent().getStringExtra("user");

        Bundle bundle = new Bundle();

        if(userId != null && !User.getCurrent().getUid().equals(userId)) { // Display for currentUser
            bundle.putString("userId", userId);
        } else { // Display for another user
            bundle.putString("userId", User.getCurrent().getUid());
        }

        Fragment fragment = new UserProfileFragment();
        fragment.setArguments(bundle);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.user_activity_profile, fragment)
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
