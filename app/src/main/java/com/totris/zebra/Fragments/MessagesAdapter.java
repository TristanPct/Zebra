package com.totris.zebra.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totris.zebra.Models.Message;
import com.totris.zebra.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomaslecoeur on 11/10/2016.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Message> messages;
    private static MessageItemListener listener;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
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

    public interface MessageItemListener {
        void onMessageItemClick(int position, View v);
    }
}
