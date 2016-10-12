package com.totris.zebra.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.totris.zebra.Models.Message;
import com.totris.zebra.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomaslecoeur on 11/10/2016.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> implements SectionTitleProvider {
    private List<Message> messages;
    private static MessageItemListener listener;

    public MessagesAdapter(List<Message> messages) {
        this.messages = new ArrayList<Message>(messages);
    }

    public void setOnMessageItemListener(MessageItemListener listenerArg) {
        listener = listenerArg;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.title.setText(message.getContent());
    }

    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.messageContent)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onMessageItemClick(getAdapterPosition(), v);
                    }
                }
            });
        }
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        return getItem(position).getContent().substring(0, 1);
    }

    public void clear() {
        messages.clear();
    }

    public void swap(List list){
        if (messages != null) {
            messages.clear();
            messages.addAll(list);
        }
        else {
            messages = list;
        }
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        if(messages != null) {
            messages.add(message);
        } else {
            messages = new ArrayList<Message>();
            messages.add(message);
        }
    }

    public interface MessageItemListener {
        void onMessageItemClick(int position, View v);
    }
}
