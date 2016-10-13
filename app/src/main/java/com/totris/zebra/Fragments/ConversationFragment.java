package com.totris.zebra.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.totris.zebra.Activities.ConversationActivity;
import com.totris.zebra.Events.MessageChildAddedEvent;
import com.totris.zebra.Events.MessageDataChangeEvent;
import com.totris.zebra.Fragments.MessagesAdapter;
import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;
import com.totris.zebra.Utils.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {
    private static final String TAG = "ConversationFragment";

    private ConversationListener listener;

    private MessagesAdapter adapter = new MessagesAdapter(new ArrayList<Message>());

    private Group group;

    @BindView(R.id.messagesList) //TODO: bind all those stuff in a fragment
    RecyclerView messagesListRecyclerView;

    @BindView(R.id.messageInput)
    TextView messageInput;

    @BindView(R.id.messageSubmit)
    Button messageSubmit;

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        group = ((ConversationActivity) getActivity()).getGroup();

        //TODO: add loading

        try {
            listener = (ConversationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ConversationListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_conversation, container, false);

        ButterKnife.bind(this, view);

        LinearLayoutManager layout = new LinearLayoutManager(inflater.getContext());
        layout.setStackFromEnd(true);

        messagesListRecyclerView.setLayoutManager(layout);


        adapter.setOnMessageItemListener(new MessagesAdapter.MessageItemListener() {
            @Override
            public void onMessageItemClick(int position, View v) {
                Log.d(TAG, "onMessageItemClick: " + position);
            }
        });

        messagesListRecyclerView.setAdapter(adapter);

        return view;
    }

    public void addMessage(Message message) {
        //if(!User.getCurrent().getUid().equals(message.getSenderId()) || message.getCreatedAt() == null) {
            adapter.addMessage(message);
        //}
    }

    @Subscribe
    public void onMessageChildAddedEvent(MessageChildAddedEvent event) {
        Log.d(TAG, "onChildAdded: new message");

        addMessage(event.getMessage());
    }

    @OnClick(R.id.messageSubmit)
    public void onSubmitMessage(Button button) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (listener != null) {
            listener.onSubmitMessage(messageInput.getText().toString());
        }

        messageInput.setText("");
    }

    public interface ConversationListener {
        void onSubmitMessage(String message);
    }

}
