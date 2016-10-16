package com.totris.zebra.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.totris.zebra.Fragments.EditPasswordFragment;
import com.totris.zebra.Fragments.EditProfileItemFragment;
import com.totris.zebra.Fragments.EditUsernameFragment;
import com.totris.zebra.Fragments.EditEmailFragment;
import com.totris.zebra.Models.EditProfileFragmentType;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

public class EditProfileItemActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileItemActivity";

    private EditProfileFragmentType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_item);

        type = EditProfileFragmentType.valueOf(getIntent().getStringExtra("fragmentType"));

        Log.d(TAG, "onCreate: " + type);

        Fragment currentFragment;

        String actionBarTitle = getString(R.string.edit) + " ";

        switch (type) {
            case USERNAME:
                actionBarTitle += getString(R.string.username).toLowerCase();
                currentFragment = new EditUsernameFragment();
                break;
            case EMAIL:
                actionBarTitle += getString(R.string.email).toLowerCase();
                currentFragment = new EditEmailFragment();
                break;
            case PASSWORD:
                actionBarTitle += getString(R.string.password).toLowerCase();
                currentFragment = new EditPasswordFragment();
                break;
            default:
                currentFragment = new Fragment();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_edit_profile_item, currentFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                EditProfileItemFragment fragment = (EditProfileItemFragment) getSupportFragmentManager().getFragments().get(0);
                editCurrentValue(fragment.getValue());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editCurrentValue(String value) {
        if (value == null) return;

        User user = User.getCurrent();

        switch (type) {
            case USERNAME:
                user.updateUsername(value).commit(); //TODO: add error callback
                break;
            case EMAIL:
                user.updateMail(value).commit(); //TODO: add error callback
                break;
            case PASSWORD:
                user.updatePassword(value).commit(); //TODO: add error callback
                break;
        }

        Intent intent = new Intent(EditProfileItemActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }
}
