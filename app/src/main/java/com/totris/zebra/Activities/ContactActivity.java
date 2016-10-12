package com.totris.zebra.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.totris.zebra.ContactsAdapter;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {
    private static String TAG = "ContactActivity";

    @BindView(R.id.contactsList)
    RecyclerView contactsListRecyclerView;

    @BindView(R.id.fastScroller)
    FastScroller fastScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);

        /**
         * Setup RecyclerView
         */

        contactsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<User> contacts = new ArrayList<>();

        Promise contactsPromise = User.getList();

        final ContactsAdapter adapter = new ContactsAdapter(contacts);

        contactsPromise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                adapter.clear();

                for (User user : (List<User>) result) {
                    adapter.addContact(user);
                }

                adapter.notifyDataSetChanged();
            }
        });

        contactsListRecyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(contactsListRecyclerView);

        /**
         * Setup UI
         */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fastScroller.setRecyclerView(contactsListRecyclerView);
    }

}
