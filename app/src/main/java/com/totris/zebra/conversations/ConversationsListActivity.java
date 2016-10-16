package com.totris.zebra.conversations;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.totris.zebra.DrawerMenuActivity;
import com.totris.zebra.groups.GroupUser;
import com.totris.zebra.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationsListActivity extends DrawerMenuActivity implements ConversationsAdapter.ConversationItemListener {
    private final static String TAG = "ConversationsListActivi";

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_conversations_list, null, false);
        contentLayout.addView(contentView, 0);

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

        ButterKnife.bind(this, contentView);

        FloatingActionButton fab = (FloatingActionButton) contentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConversationsListActivity.this, NewConversationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    public void onConversationItemClick(GroupUser conversation) {
        Log.d(TAG, "onConversationItemClick: " + conversation.getTitle());

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("group", conversation.getGroup());
        intent.putExtra("title", conversation.getTitle());
        startActivity(intent);
    }
}
