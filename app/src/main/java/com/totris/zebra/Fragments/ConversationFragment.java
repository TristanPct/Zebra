package com.totris.zebra.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.squareup.otto.Subscribe;
import com.totris.zebra.Events.MessageChildAddedEvent;
import com.totris.zebra.Models.Message;
import com.totris.zebra.R;

import java.util.ArrayList;

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

    @BindView(R.id.messagesList) //TODO: bind all those stuff in a fragment
    RecyclerView messagesListRecyclerView;

    @BindView(R.id.messageInput)
    TextView messageInput;

    @BindView(R.id.messageSubmit)
    ImageButton messageSubmit;

    @BindView(R.id.fileUploadWrapper)
    FlexboxLayout fileUploadWrapper;

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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

    @OnClick(R.id.messageFile)
    public void onAddFileClick() {
        if(fileUploadWrapper.getVisibility() == View.VISIBLE) {
            fileUploadWrapper.setVisibility(View.GONE);
        } else {
            fileUploadWrapper.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.takePictureFromCamera)
    public void onTakePictureFromCameraClick() {
        if (listener != null) {
            listener.onTakePictureFromCameraClick();
        }
    }

    @OnClick(R.id.takePictureFromGallery)
    public void onTakePictureFromGalleryClick() {
        if (listener != null) {
            listener.onTakePictureFromGalleryClick();
        }
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
        void onTakePictureFromCameraClick();
        void onTakePictureFromGalleryClick();
    }

}
