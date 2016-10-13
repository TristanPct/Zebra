package com.totris.zebra.Activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.totris.zebra.Fragments.ConversationFragment;
import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.R;
import com.totris.zebra.Utils.EventBus;

import java.util.Date;

public class ConversationActivity extends AppCompatActivity implements ConversationFragment.ConversationListener {
    static String TAG = "ConversationActivity";
    private Group group;
    private ConversationFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        group = (Group) getIntent().getSerializableExtra("group");
        group.addChildEventListener();

        currentFragment = new ConversationFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_conversation, currentFragment)
                    .commit();
        }
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

    public Group getGroup() {
        return group;
    }

    @Override
    public void onSubmitMessage(String message) {
        Log.d(TAG, "onSubmitMessage: " + message);

        Message messageObj = new Message(message, MessageType.TEXT);

        //currentFragment.addMessage(messageObj);

        messageObj.setCreatedAt(new Date());

        group.sendMessage(messageObj);
    }
}
