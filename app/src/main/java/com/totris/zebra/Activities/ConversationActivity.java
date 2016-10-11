package com.totris.zebra.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.Fragments.MessagesAdapter;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.R;
import com.totris.zebra.Utils.Database;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationActivity extends AppCompatActivity {
    static String TAG = "ConversationActivity";

    private Database database;

    @BindView(R.id.messagesList)
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

        for (int i = 0; i < 2; i++) {
            messages.add(new Message("Content of message " + i, MessageType.TEXT, 0, 0));
        }

        final MessagesAdapter adapter = new MessagesAdapter(messages);

        database = Database.getInstance();

        database.getMessagesReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: new message");

                adapter.clear();

                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) { // TODO: only add not already fetched messages
                    Message message = postSnapshot.getValue(Message.class);

                    adapter.addMessage(message);

                    Log.e("Get Data", message.getContent());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        adapter.setOnMessageItemListener(new MessagesAdapter.MessageItemListener() {
            @Override
            public void onMessageItemClick(int position, View v) {
                Log.d(TAG, "onMessageItemClick: " + position);
            }
        });

        messagesListRecyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.messageSubmit)
    public void submitMessage(Button button) {
        database.getMessagesReference().push().setValue(new Message(messageInput.getText().toString(), MessageType.TEXT, 0, 0));

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        messageInput.setText("");

        /*LinearLayoutManager recyclerViewLayoutManager = (LinearLayoutManager) messagesListRecyclerView.getLayoutManager();
        recyclerViewLayoutManager.setStackFromEnd(true);*/

    }
}
