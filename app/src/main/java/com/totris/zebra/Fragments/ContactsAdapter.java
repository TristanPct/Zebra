package com.totris.zebra.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements SectionTitleProvider {
    private static final String TAG = "ContactsAdapter";

    private static List<User> users;
    private static ArrayList<String> selection = new ArrayList<>();
    private static ContactsListMode mode;
    private static ContactItemListener listener;

    public ContactsAdapter(List<User> users) {
        ContactsAdapter.users = new ArrayList<>(users);
    }

    public void setMode(ContactsListMode mode) {
        ContactsAdapter.mode = mode;
    }

    public void setOnContactItemListener(ContactItemListener listener) {
        ContactsAdapter.listener = listener;
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

        holder.setUid(user.getUid());

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
        @BindView(R.id.contactItem)
        RelativeLayout contactItem;

        @BindView(R.id.contactUsername)
        TextView username;

        @BindView(R.id.contactMail)
        TextView mail;

        @BindView(R.id.contactImageAdd)
        ImageView contactImageAdd;

        @BindView(R.id.contactImageRemove)
        ImageView contactImageRemove;

        String uid;
        boolean selected;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        setSelected(!selected);

                        listener.onContactItemClick(users.get(getAdapterPosition()), selected);
                    }
                }
            });
        }

        public void setUid(String uid) {
            this.uid = uid;

            setSelected(selection.contains(uid));
        }

        public void setSelected(boolean selected) {
            this.selected = selected;

            if (mode == ContactsListMode.SELECTABLE) {
                if (selected) {
                    selection.add(uid);
                    contactImageAdd.setVisibility(View.GONE);
                    contactImageRemove.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        contactItem.setBackground(contactItem.getResources().getDrawable(R.drawable.list_item_selected_bg));
                    }
                } else {
                    selection.remove(uid);
                    contactImageAdd.setVisibility(View.VISIBLE);
                    contactImageRemove.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        contactItem.setBackground(contactItem.getResources().getDrawable(R.drawable.list_item_bg));
                    }
                }
            }
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
        void onContactItemClick(User user, boolean selected);
    }
}
