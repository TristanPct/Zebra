package com.totris.zebra.conversations;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.totris.zebra.users.contacts.ContactsAdapter;
import com.totris.zebra.users.contacts.ContactsListFragment;
import com.totris.zebra.users.contacts.ContactsListMode;
import com.totris.zebra.users.User;
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
        inflater.inflate(R.menu.menu_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                //TODO: create conversation
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
