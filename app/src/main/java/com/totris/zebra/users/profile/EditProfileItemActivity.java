package com.totris.zebra.users.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.totris.zebra.users.User;
import com.totris.zebra.R;
import com.totris.zebra.users.auth.ReauthenticateDialogFragment;
import com.totris.zebra.utils.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileItemActivity extends AppCompatActivity implements ReauthenticateDialogFragment.ReauthenticateDialogListener {

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
                editCurrentValue(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editCurrentValue(boolean reauthenticate) {
        String value = fragment.getValue();

        if (value == null) return;

        if (reauthenticate) {
            ReauthenticateDialogFragment reauthenticateDialog = new ReauthenticateDialogFragment();
//            reauthenticateDialog.setListener(new ReauthenticateDialogFragment.ReauthenticateDialogListener() {
//                @Override
//                public void onReauthenticationSuccess() {
//                    editCurrentValue(false);
//                }
//            });
            reauthenticateDialog.show(getSupportFragmentManager(), "ReauthenticateDialogFragment");
            return;
        }

        ViewUtils.closeKeyboard(this);

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

                Log.d(TAG, "editCurrentValue#onComplete: " + success + " " + errors);

                if (success) {
                    Intent intent = new Intent(EditProfileItemActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    fragment.setError(errors.get(0));
                }
            }
        });
    }

    private void toggleProgress(boolean show) {
        ViewUtils.toggleProgress(progressView, editProfileItemContent, show);
    }

    @Override
    public void onReauthenticationSuccess() {
        editCurrentValue(false);
    }
}
