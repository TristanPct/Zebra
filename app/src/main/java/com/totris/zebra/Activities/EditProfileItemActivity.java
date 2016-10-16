package com.totris.zebra.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.totris.zebra.Fragments.EditPasswordFragment;
import com.totris.zebra.Fragments.EditProfileItemFragment;
import com.totris.zebra.Fragments.EditUsernameFragment;
import com.totris.zebra.Fragments.EditEmailFragment;
import com.totris.zebra.Models.EditProfileFragmentType;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileItemActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileItemActivity";

    private EditProfileFragmentType type;
    private EditProfileItemFragment fragment;

    @BindView(R.id.progress)
    View progressView;

    @BindView(R.id.editProfileItemContent)
    View editProfileItemContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_item);

        type = EditProfileFragmentType.valueOf(getIntent().getStringExtra("fragmentType"));

        Log.d(TAG, "onCreate: " + type);

        ButterKnife.bind(this);

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

        fragment = (EditProfileItemFragment) currentFragment;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.editProfileItemContent, currentFragment)
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
                editCurrentValue(fragment.getValue());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editCurrentValue(String value) {
        if (value == null) return;

        toggleProgress(true);

        User user = User.getCurrent();

        switch (type) {
            case USERNAME:
                user.updateUsername(value);
                break;
            case EMAIL:
                user.updateMail(value);
                break;
            case PASSWORD:
                user.updatePassword(value);
                break;
        }

        user.commit(new User.OnCommitListener() {
            @Override
            public void onComplete(boolean success, List<String> errors) {
                toggleProgress(false);

                if (success) {
                    Intent intent = new Intent(EditProfileItemActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    fragment.setError(errors.get(0));
                }
            }
        });
    }

    private void toggleProgress(final boolean show) {
        if (progressView.getVisibility() == View.VISIBLE && show) return;

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        editProfileItemContent.setVisibility(show ? View.GONE : View.VISIBLE);
        editProfileItemContent.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                editProfileItemContent.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
