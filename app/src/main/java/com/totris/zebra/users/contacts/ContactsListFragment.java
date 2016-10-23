package com.totris.zebra.users.contacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.totris.zebra.users.User;
import com.totris.zebra.R;
import com.totris.zebra.users.contacts.ContactsAdapter;
import com.totris.zebra.users.contacts.ContactsListMode;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsListFragment extends Fragment {
    private static String TAG = "ContactsListFragment";

    private ContactsListMode mode = ContactsListMode.NORMAL;
    private ContactsAdapter adapter;

    @BindView(R.id.contactsList)
    RecyclerView contactsListRecyclerView;

    @BindView(R.id.fastScroller)
    FastScroller fastScroller;

    public ContactsListFragment() {
        // Required empty public constructor
    }

    public ArrayList<User> getSelectedUsers() {
        return adapter.getSelectedUsers();
    }

    public void setMode(ContactsListMode mode) {
        this.mode = mode;

        if (this.adapter != null) {
            this.adapter.setMode(mode);
        }
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

        List<User> contacts = User.getAll();
        Collections.sort(contacts, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getUsername().compareTo(u2.getUsername());
            }
        });

        adapter = new ContactsAdapter(contacts);
        adapter.setMode(mode);

        try {
            adapter.setOnContactItemListener((ContactsAdapter.ContactItemListener) getActivity());
        } catch (ClassCastException exception) {
            throw new ClassCastException(getActivity().toString() + " must implement ContactItemListener");
        }

        contactsListRecyclerView.setAdapter(adapter);
        // FastScroller
        fastScroller.setRecyclerView(contactsListRecyclerView);

        return view;
    }
}
