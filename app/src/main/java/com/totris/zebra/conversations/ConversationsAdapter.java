package com.totris.zebra.conversations;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totris.zebra.groups.GroupUser;
import com.totris.zebra.messages.Message;
import com.totris.zebra.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private static List<GroupUser> conversations = new ArrayList<>();
    private static ConversationItemListener listener;

    private static DateFormat lastDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public ConversationsAdapter() {

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

        holder.usernames.setText(conversation.getTitle());

        // unused:
        Message lastMessage = conversation.getGroup().getLastMessage();

        if (lastMessage != null) {
            String lastMessageString = lastMessage.getContent();
            if (lastMessageString.length() > 20) {
                lastMessageString = lastMessageString.substring(0, 20) + "...";
            }

            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessage.setText(lastMessageString);

            if (lastMessage.getReceiveAt() != null) {
                String lastDate = lastDateFormat.format(lastMessage.getReceiveAt());
                holder.lastDate.setVisibility(View.VISIBLE);
                holder.lastDate.setText(lastDate);
            } else {
                holder.lastDate.setVisibility(View.GONE);
            }
        } else {
            holder.lastMessage.setVisibility(View.GONE);
            holder.lastDate.setVisibility(View.GONE);
        }
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
        conversations.add(conversation);
    }

    public interface ConversationItemListener {
        void onConversationItemClick(GroupUser conversation);
    }
}