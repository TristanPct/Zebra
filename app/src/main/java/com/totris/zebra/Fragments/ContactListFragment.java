package com.totris.zebra.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.totris.zebra.Fragments.ContactsAdapter;
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
public class ContactListFragment extends Fragment {
    private static String TAG = "ContactListFragment";

    private ContactsAdapter adapter;

    @BindView(R.id.contactsList)
    RecyclerView contactsListRecyclerView;

    @BindView(R.id.fastScroller)
    FastScroller fastScroller;

    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        ButterKnife.bind(this, view);

        /**
         * Setup RecyclerView
         */

        contactsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<User> contacts = new ArrayList<>();

        Promise contactsPromise = User.getList();

        adapter = new ContactsAdapter(contacts);

        try {
            adapter.setOnContactItemListener((ContactsAdapter.ContactItemListener) getActivity());
        } catch (ClassCastException exception) {
            throw new ClassCastException(getActivity().toString() + " must implement ContactItemListener");
        }

        contactsPromise.done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                adapter.clear();

                for (User user : (List<User>) result) {
                    adapter.addContact(user);
                }

                adapter.notifyDataSetChanged();
            }
        });

        contactsListRecyclerView.setAdapter(adapter);
        // FastScroller

        fastScroller.setRecyclerView(contactsListRecyclerView);

        return view;
    }
}
