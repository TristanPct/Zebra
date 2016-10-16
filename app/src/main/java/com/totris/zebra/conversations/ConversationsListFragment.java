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
import com.totris.zebra.groups.GroupUserInstantiateEvent;
import com.totris.zebra.users.UserRegisterGroupEvent;
import com.totris.zebra.groups.GroupUser;
import com.totris.zebra.users.User;
import com.totris.zebra.R;
import com.totris.zebra.utils.EventBus;

import org.jdeferred.DoneCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsListFragment extends Fragment {
    private static final String TAG = "ConversationsListFragme";

    private ConversationsAdapter adapter;
    private boolean loaded = false;

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
        User.getCurrent().getInstantiatedGroupUsers().done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                loaded = true;

                adapter.clear();
                for (GroupUser conversation : (List<GroupUser>)result) {
                    adapter.addConversation(conversation);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);

        ButterKnife.bind(this, view);

        /**
         * Setup RecyclerView
         */

        conversationsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ConversationsAdapter();

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
//        if (!loaded) {
//            Log.d(TAG, "onUserRegisterGroupEvent: not loaded");
//            return;
//        }

        Log.d(TAG, "onUserRegisterGroupEvent");
        event.getGroupUser().getInstantiatedGroupUsers();
    }

    @Subscribe
    public void onGroupUserInstantiateEvent(GroupUserInstantiateEvent event) {
//        if (!loaded) {
//            Log.d(TAG, "onGroupUserInstantiateEvent: not loaded");
//            return;
//        }

        Log.d(TAG, "onGroupUserInstantiateEvent");
        adapter.addConversation(event.getGroupUser());

        adapter.notifyDataSetChanged();
    }
}
