package com.totris.zebra.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.totris.zebra.Fragments.MessagesAdapter;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.R;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {
    static String TAG = "ConversationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.messagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            messages.add(new Message("Content of message " + i, MessageType.TEXT, 0, 0));
        }

        MessagesAdapter adapter = new MessagesAdapter(messages);

        adapter.setOnMessageItemListener(new MessagesAdapter.MessageItemListener() {
            @Override
            public void onMessageItemClick(int position, View v) {
                Log.d(TAG, "onMessageItemClick: " + position);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
