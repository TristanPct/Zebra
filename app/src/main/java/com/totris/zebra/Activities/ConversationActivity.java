package com.totris.zebra.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.totris.zebra.Events.MessageDataChangeEvent;
import com.totris.zebra.Fragments.MessagesAdapter;
import com.totris.zebra.Models.EncryptedMessage;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.R;
import com.totris.zebra.Utils.Database;
import com.totris.zebra.Utils.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationActivity extends AppCompatActivity {
    static String TAG = "ConversationActivity";

    private MessagesAdapter adapter;

    @BindView(R.id.messagesList) //TODO: bind all those stuff in a fragment
    RecyclerView messagesListRecyclerView;

    @BindView(R.id.messageInput)
    TextView messageInput;

    @BindView(R.id.messageSubmit)
    Button messageSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        ButterKnife.bind(this);

        messagesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Message> messages = new ArrayList<>();

        messages.add(new Message("Loading messages...", MessageType.TEXT, 0, 0));

        adapter = new MessagesAdapter(messages);

        adapter.setOnMessageItemListener(new MessagesAdapter.MessageItemListener() {
            @Override
            public void onMessageItemClick(int position, View v) {
                Log.d(TAG, "onMessageItemClick: " + position);
            }
        });

        messagesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Subscribe
    public void onMessageDataChangeEvent(MessageDataChangeEvent event) {
        Log.d(TAG, "onDataChange: new message");
        List<Message> messages = event.getMessages();

        adapter.clear();

        for (Message message : messages) {
            adapter.addMessage(message); // TODO: only add not already fetched messages
        }

        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.messageSubmit)
    public void submitMessage(Button button) {
        Message message = new Message(messageInput.getText().toString(), MessageType.TEXT, 0, 0);
        message.send();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        messageInput.setText("");

        /*LinearLayoutManager recyclerViewLayoutManager = (LinearLayoutManager) contactsListRecyclerView.getLayoutManager();
        recyclerViewLayoutManager.setStackFromEnd(true);*/

    }
}
