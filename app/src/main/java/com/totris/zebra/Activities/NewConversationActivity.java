package com.totris.zebra.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.totris.zebra.Fragments.ContactsAdapter;
import com.totris.zebra.Fragments.ContactsListFragment;
import com.totris.zebra.Fragments.ContactsListMode;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

public class NewConversationActivity extends AppCompatActivity implements ContactsAdapter.ContactItemListener {

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
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_new_conversation));
        }
    }

    @Override
    public void onContactItemClick(User user) {

    }
}
