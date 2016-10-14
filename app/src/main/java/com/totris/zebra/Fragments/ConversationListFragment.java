package com.totris.zebra.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.totris.zebra.Events.GroupUserInstantiateEvent;
import com.totris.zebra.Events.UserRegisterGroupEvent;
import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.GroupUser;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;
import com.totris.zebra.Utils.EventBus;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationListFragment extends Fragment {

    private static final String TAG = "ConversationListFragmen";
    private ConversationsAdapter adapter;

    @BindView(R.id.conversationsList)
    RecyclerView conversationsListRecyclerView;

    public ConversationListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.register(this);
        User.getCurrent().getInstantiatedGroupUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

        ButterKnife.bind(this, view);

        if (savedInstanceState == null){

        }

        /**
         * Setup RecyclerView
         */

        conversationsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<GroupUser> conversations = new ArrayList<>();

        adapter = new ConversationsAdapter(conversations);

        try {
            adapter.setOnConversationItemListener((ConversationsAdapter.ConversationItemListener) getActivity());
        } catch (ClassCastException exception) {
            throw new ClassCastException(getActivity().toString() + " must implement ConversationItemListener");
        }

        conversationsListRecyclerView.setAdapter(adapter);

        return view;
    }

    @Subscribe
    public void onUserRegisterGroupEvent(UserRegisterGroupEvent event) {
        Log.d(TAG, "onUserRegisterGroupEvent");
        event.getGroupUser().getInstantiatedGroupUsers();
    }

    @Subscribe
    public void onGroupUserInstantiateEvent(GroupUserInstantiateEvent event) {
        Log.d(TAG, "onGroupUserInstantiateEvent");
        adapter.addConversation(event.getGroupUser());

        adapter.notifyDataSetChanged();
    }
}
