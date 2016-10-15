package com.totris.zebra.Activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.totris.zebra.Fragments.ConversationsListFragment;
import com.totris.zebra.Fragments.ConversationsAdapter;
import com.totris.zebra.Models.GroupUser;
import com.totris.zebra.R;

public class ConversationsListActivity extends AppCompatActivity implements ConversationsAdapter.ConversationItemListener {
    private final static String TAG = "ConversationsListActivi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.conversationsListContent, new ConversationsListFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_conversations_list));
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    public void onConversationItemClick(GroupUser conversation) {
        Log.d(TAG, "onConversationItemClick");

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("group", conversation.getGroup());
        startActivity(intent);
    }
}
