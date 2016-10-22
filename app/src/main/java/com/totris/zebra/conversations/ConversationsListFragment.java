package com.totris.zebra.conversations;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.totris.zebra.groups.Group;
import com.totris.zebra.groups.GroupUserInstantiateEvent;
import com.totris.zebra.groups.events.GroupAddedEvent;
import com.totris.zebra.groups.events.GroupRemovedEvent;
import com.totris.zebra.users.UserRegisterGroupEvent;
import com.totris.zebra.groups.GroupUser;
import com.totris.zebra.users.User;
import com.totris.zebra.R;
import com.totris.zebra.utils.EventBus;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsListFragment extends Fragment {
    private static final String TAG = "ConversationsListFragme";

    private ConversationsAdapter adapter;

    private ArrayList<String> conversationsIds = new ArrayList<>();

    @BindView(R.id.conversationsList)
    RecyclerView conversationsListRecyclerView;

    public ConversationsListFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);

        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            conversationsIds = savedInstanceState.getStringArrayList("conversationsIds");
        }

        conversationsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ConversationsAdapter(Group.getAll());

        try {
            adapter.setOnConversationItemListener((ConversationsAdapter.ConversationItemListener) getActivity());
        } catch (ClassCastException exception) {
            throw new ClassCastException(getActivity().toString() + " must implement ConversationItemListener");
        }

        conversationsListRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("conversationsIds", conversationsIds);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onUserRegisterGroupEvent(UserRegisterGroupEvent event) {
        Log.d(TAG, "onUserRegisterGroupEvent");
//        event.getGroupUser().getInstantiatedGroupUsers();
    }

//    @Subscribe
//    public void onGroupUserInstantiateEvent(GroupUserInstantiateEvent event) {
//        String uid = event.getGroupUser().getUid();
//
//        if (conversationsIds.contains(uid)) {
//            Log.d(TAG, "onGroupUserInstantiateEvent: already added");
//            return;
//        }
//
//        conversationsIds.add(uid);
//
//        Log.d(TAG, "onGroupUserInstantiateEvent");
//        adapter.addConversation(event.getGroupUser());
//
//        adapter.notifyDataSetChanged();
//    }

    @Subscribe
    public void onGroupAddedEvent(GroupAddedEvent event) {
        adapter.refresh();
    }

    @Subscribe
    public void onGroupRemovedEvent(GroupRemovedEvent event) {
        adapter.refresh();
    }
}
