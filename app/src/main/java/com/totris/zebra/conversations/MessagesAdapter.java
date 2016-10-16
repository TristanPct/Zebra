package com.totris.zebra.conversations;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.totris.zebra.messages.Message;
import com.totris.zebra.messages.MessageType;
import com.totris.zebra.users.User;
import com.totris.zebra.R;
import com.totris.zebra.Utils.OnlineStorage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    private static MessageItemListener listener;

    private List<Message> messages = new ArrayList<>();
    private Context context;

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
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);


        if (User.getCurrent().getUid().equals(message.getSenderId())) {
            holder.container.setGravity(Gravity.END);
        } else {
            holder.container.setGravity(Gravity.START);
        }

        if (message.getType() == MessageType.TEXT) {
            holder.content.setText(message.getContent());
            holder.imageView.setVisibility(View.GONE);
        }

        if (message.getType() == MessageType.IMAGE) {
            holder.content.setVisibility(View.GONE);

            Log.d(TAG, "onBindViewHolder: " + OnlineStorage.getImageReference(message.getContent()));

            Glide
                    .with(context)
                    .using(new FirebaseImageLoader())
                    .load(OnlineStorage.getImageReference(message.getContent()))
                    .placeholder(R.drawable.ic_camera)
                    .crossFade()
                    .into(holder.imageView);
        }
    }

    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.messageContainer)
        LinearLayout container;

        @BindView(R.id.messageContent)
        TextView content;

        @BindView(R.id.messageImage)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onMessageItemClick(getAdapterPosition(), v);
                    }
                }
            });
        }
    }

    public void clear() {
        messages.clear();
    }

    public void setMessages(List<Message> messages) {
        clear();

        addMessages(messages);
    }

    public void addMessage(Message message) {
        messages.add(message);

        notifyDataSetChanged();
    }

    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);

        notifyDataSetChanged();
    }

    public interface MessageItemListener {
        void onMessageItemClick(int position, View v);
    }
}
