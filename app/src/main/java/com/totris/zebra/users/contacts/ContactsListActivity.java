package com.totris.zebra.users.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.totris.zebra.conversations.ConversationActivity;
import com.totris.zebra.DrawerMenuActivity;
import com.totris.zebra.groups.Group;
import com.totris.zebra.R;
import com.totris.zebra.users.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsListActivity extends DrawerMenuActivity implements ContactsAdapter.ContactItemListener {
    private final static String TAG = "ContactsListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_contacts_list, null, false);
        contentLayout.addView(contentView, 0);

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

        FloatingActionButton fab = (FloatingActionButton) contentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onContactItemClick(User user, boolean selected) {
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
