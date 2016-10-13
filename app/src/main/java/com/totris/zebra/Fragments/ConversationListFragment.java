package com.totris.zebra.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.totris.zebra.Events.GroupUserInstantiateEvent;
import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.GroupUser;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

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

    private ConversationsAdapter adapter;

    @BindView(R.id.conversationsList)
    RecyclerView conversationsListRecyclerView;

    public ConversationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

        ButterKnife.bind(this, view);

        /**
         * Setup RecyclerView
         */

        conversationsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<GroupUser> conversations = new ArrayList<>();

        Promise conversationsPromise = User.getCurrent().getInstantiatedGroupUsers();

        adapter = new ConversationsAdapter(conversations);

        try {
            adapter.setOnConversationItemListener((ConversationsAdapter.ConversationItemListener) getActivity());
        } catch (ClassCastException exception) {
            throw new ClassCastException(getActivity().toString() + " must implement ConversationItemListener");
        }

        conversationsPromise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                adapter.clear();

                for (GroupUser groupUser : (List<GroupUser>) result) {
                    adapter.addConversation(groupUser);
                }

                adapter.notifyDataSetChanged();
            }
        });

        conversationsListRecyclerView.setAdapter(adapter);

        return view;
    }

    @Subscribe
    public void onGroupUserInstantiateEvent(GroupUserInstantiateEvent event) {
        adapter.addConversation(event.getGroupUser());

        adapter.notifyDataSetChanged();
    }
}
