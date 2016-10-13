package com.totris.zebra.Activities;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.totris.zebra.Fragments.ConversationListFragment;
import com.totris.zebra.Fragments.ConversationsAdapter;
import com.totris.zebra.Models.GroupUser;
import com.totris.zebra.R;
import com.totris.zebra.Utils.EventBus;

public class ConversationListActivity extends AppCompatActivity implements ConversationsAdapter.ConversationItemListener {
    private final static String TAG = "ConversationListActivit";

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        if(savedInstanceState == null) {
            currentFragment = new ConversationListFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.conversationContent, currentFragment)
                    .commit();
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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
    protected void onResume() {
        super.onResume();
        EventBus.register(currentFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(currentFragment);
    }

    @Override
    public void onConversationItemClick(GroupUser conversation) {
        Log.d(TAG, "onConversationItemClick");
    }
}
