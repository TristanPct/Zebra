package com.totris.zebra.Fragments;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totris.zebra.Models.GroupUser;
import com.totris.zebra.Models.User;
import com.totris.zebra.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private static List<GroupUser> conversations;
    private static ConversationItemListener listener;

    private static DateFormat lastDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public ConversationsAdapter(List<GroupUser> conversations) {
        ConversationsAdapter.conversations = new ArrayList<>(conversations);
    }

    public void setOnConversationItemListener(ConversationItemListener listener) {
        ConversationsAdapter.listener = listener;
    }

    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
        ConversationsAdapter.ViewHolder viewHolder = new ConversationsAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        GroupUser conversation = conversations.get(position);

        String usernames = "";
        for (User u : conversation.getUsers()) {
            usernames += u.getUsername() + ", ";
        }
        if (usernames.length() != 0) {
            usernames = usernames.substring(0, usernames.length() - 2);
        }

        String lastMessage = conversation.getGroup().getLastMessage().getContent();
        if (lastMessage.length() > 20) {
            lastMessage = lastMessage.substring(0, 20) + "...";
        }

        String lastDate = lastDateFormat.format(conversation.getGroup().getLastMessage().getReceiveAt());

        holder.usernames.setText(usernames);
        holder.lastMessage.setText(lastMessage);
        holder.lastDate.setText(lastDate);
    }

    public GroupUser getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.conversationUsernames)
        TextView usernames;

        @BindView(R.id.conversationLastMessage)
        TextView lastMessage;

        @BindView(R.id.conversationLastDate)
        TextView lastDate;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onConversationItemClick(conversations.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public void clear() {
        conversations.clear();
    }

    public void addConversation(GroupUser conversation) {
        if(conversations != null) {
            conversations.add(conversation);
        } else {
            conversations = new ArrayList<>();
            conversations.add(conversation);
        }
    }

    public interface ConversationItemListener {
        void onConversationItemClick(GroupUser conversation);
    }
}
