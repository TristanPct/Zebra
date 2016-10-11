package com.totris.zebra.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
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

    @BindView(R.id.messageInput)
    TextView messageInput;

    @BindView(R.id.messageSubmit)
    Button messageSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        ButterKnife.bind(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.messagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<List<Message>> t = new GenericTypeIndicator<List<Message>>() {
                };

                List messages = dataSnapshot.getValue(t);

                if (messages == null) {
                    Log.d(TAG, "onDataChange: No messages");
                } else {
                    Log.d(TAG, "The first message is: " + messages.get(0));
                    adapter.swap(messages);
                }
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

        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.messageSubmit)
    public void submitMessage(Button button) {
        database.getMessagesReference().push().setValue(new Message(messageInput.getText().toString(), MessageType.TEXT, 0, 0));
    }
}
