package com.totris.zebra.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.totris.zebra.Fragments.ContactsListFragment;
import com.totris.zebra.Fragments.ContactsAdapter;
import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsListActivity extends AppCompatActivity implements ContactsAdapter.ContactItemListener {
    private final static String TAG = "ContactsListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        if(savedInstanceState == null) {
            ContactsListFragment contactsList = new ContactsListFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contactsListContent, contactsList)
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_contacts_list));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onContactItemClick(User user) {
        Log.d(TAG, "onContactItemClick");

        // TODO: open contact profile
        List<User> users = new ArrayList<>();
        users.add(user);

        Group group = Group.getCommonGroup(users);

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }
}
