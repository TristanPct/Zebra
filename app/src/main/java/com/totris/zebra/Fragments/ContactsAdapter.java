package com.totris.zebra.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomaslecoeur on 12/10/2016.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements SectionTitleProvider {
    private static List<User> users;
    private static ContactsListMode mode;
    private static ContactItemListener listener;


    public ContactsAdapter(List<User> users) {
        ContactsAdapter.users = new ArrayList<>(users);
    }

    public void setMode(ContactsListMode mode) {
        ContactsAdapter.mode = mode;
    }

    public void setOnContactItemListener(ContactItemListener listenerArg) {
        listener = listenerArg;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        holder.username.setText(user.getUsername());
        holder.mail.setText(user.getMail());
    }

    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contactUsername)
        TextView username;

        @BindView(R.id.contactMail)
        TextView mail;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onContactItemClick(users.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        return getItem(position).getUsername().substring(0, 1);
    }

    public void clear() {
        users.clear();
    }

    public void swap(List list){
        if (users != null) {
            users.clear();
            users.addAll(list);
        }
        else {
            users = list;
        }
        notifyDataSetChanged();
    }

    public void addContact(User user) {
        if(users != null) {
            users.add(user);
        } else {
            users = new ArrayList<User>();
            users.add(user);
        }
    }

    public interface ContactItemListener {
        void onContactItemClick(User user);
    }
}
