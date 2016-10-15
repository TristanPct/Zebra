package com.totris.zebra.Activities;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.totris.zebra.Fragments.ContactsAdapter;
import com.totris.zebra.Fragments.ContactsListFragment;
import com.totris.zebra.Fragments.ContactsListMode;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import java.util.ArrayList;

public class NewConversationActivity extends AppCompatActivity implements ContactsAdapter.ContactItemListener {
    private static final String TAG = "NewConversationActivity";
    private ArrayList<String> usersIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        if (savedInstanceState == null) {
            ContactsListFragment fragment = new ContactsListFragment();
            fragment.setMode(ContactsListMode.SELECTABLE);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_new_conversation, fragment)
                    .commit();
        } else {
            usersIds = savedInstanceState.getStringArrayList("usersIds");
            ((ContactsListFragment)getSupportFragmentManager().getFragments().get(0)).setMode(ContactsListMode.SELECTABLE);
        }

        Log.d(TAG, "onCreate: " + (savedInstanceState != null) + " | " + usersIds);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_new_conversation));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_conversation, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG, "onSaveInstanceState: " + usersIds); //FIXME: onSaveInstanceState seems to not be called
        outState.putStringArrayList("usersIds", usersIds);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onContactItemClick(User user, boolean selected) {
        if (selected) {
            usersIds.add(user.getUid());
        } else {
            usersIds.remove(user.getUid());
        }

        Log.d(TAG, "onContactItemClick: " + usersIds);
    }
}
