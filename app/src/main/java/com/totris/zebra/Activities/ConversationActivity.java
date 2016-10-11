package com.totris.zebra.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.totris.zebra.Fragments.MessagesAdapter;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ConversationActivity extends AppCompatActivity {
    static String TAG = "ConversationActivity";

    @BindView(R.id.messageInput)
    TextView messageInput;

    @BindView(R.id.messageSubmit)
    Button messageSubmit;

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

    @OnClick(R.id.messageSubmit)
    public void submitMessage(Button button) {

    }
}
