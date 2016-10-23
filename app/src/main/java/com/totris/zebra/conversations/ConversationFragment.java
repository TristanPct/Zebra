package com.totris.zebra.conversations;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.squareup.otto.Subscribe;
import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;
import com.totris.zebra.R;
import com.totris.zebra.messages.events.MessageAddedEvent;
import com.totris.zebra.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {
    private static final String TAG = "ConversationFragment";

    private ConversationListener listener;

    private MessagesAdapter adapter;

    @BindView(R.id.activity_conversation)
    ViewGroup conversationFragmentWrapper;

    @BindView(R.id.messagesList)
    RecyclerView messagesListRecyclerView;

    @BindView(R.id.messageInput)
    TextView messageInput;

    @BindView(R.id.messageSubmit)
    ImageButton messageSubmit;

    @BindView(R.id.fileUploadWrapper)
    FlexboxLayout fileUploadWrapper;

    @BindView(R.id.messageFile)
    ImageButton messageFile;

    private boolean messagesHidden = false;

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

        List<Message> messages = listener.getGroup().getMessages();
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                Log.d(TAG, "compare: " + m1.getCreatedAt() + " to " + m2.getCreatedAt());
                return m1.getCreatedAt().compareTo(m2.getCreatedAt());
            }
        });

        adapter = new MessagesAdapter(listener.getGroup().getMessages());

        adapter.setOnMessageItemListener(new MessagesAdapter.MessageItemListener() {
            @Override
            public void onMessageItemClick(int position, View v) {
                Log.d(TAG, "onMessageItemClick: " + position);
            }
        });

        messagesListRecyclerView.setAdapter(adapter);

        return view;
    }

    public boolean getMessagesHidden() {
        return messagesHidden;
    }

    public void addMessage(Message message) {
        //if(!User.getCurrent().getUid().equals(message.getSenderId()) || message.getCreatedAt() == null) {
            adapter.addMessage(message);
        //}
    }

    public void showMessages() {
        Log.d(TAG, "showMessages: delete blur");

        if(messagesHidden) {
            messagesHidden = false;

            toggleTouchEvents(true);

            conversationFragmentWrapper.setVisibility(View.VISIBLE);

//            Blurry.delete(conversationFragmentWrapper);
        }
    }

    public void hideMessages() {
        if(!messagesHidden) {
            messagesHidden = true;

            toggleTouchEvents(false);

            conversationFragmentWrapper.setVisibility(View.INVISIBLE);

            /*Blurry.with(getContext())
                    .radius(25)
                    .animate(400)
                    .onto(conversationFragmentWrapper);*/
        }
    }

    public void toggleTouchEvents(boolean enable) {
        messagesListRecyclerView.setEnabled(enable);
        messageInput.setEnabled(enable);
        messageSubmit.setEnabled(enable);
        messageFile.setEnabled(enable);
    }

    @Subscribe
    public void onMessageAddedEvent(MessageAddedEvent event) {
        Log.d(TAG, "onChildAdded: new message");

        if (!event.getGroup().getUid().equals(listener.getGroup().getUid()))
            return;

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
    public void onSubmitMessage(ImageButton button) {
        ViewUtils.closeKeyboard(getActivity());

        if (listener != null) {
            listener.onSubmitMessage(messageInput.getText().toString());
        }

        messageInput.setText("");
    }

    public interface ConversationListener {
        void onSubmitMessage(String message);
        void onTakePictureFromCameraClick();
        void onTakePictureFromGalleryClick();

        Group getGroup();
    }

}
