package com.pucmm.chatapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;

        if (viewType == 1) {
            layout = R.layout.item_message_sent;
        } else {
            layout = R.layout.item_message_received;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Message message = messageList.get(position);

        if ("image".equals(message.getType())) {
            holder.txtMessage.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.VISIBLE);

            Glide.with(holder.itemView.getContext())
                    .load(message.getImageUrl())
                    .into(holder.imgMessage);

        } else {
            holder.imgMessage.setVisibility(View.GONE);
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setText(message.getText());
        }

        if (message.getTimestamp() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.txtTime.setText(dateFormat.format(message.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        TextView txtTime;
        ImageView imgMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgMessage = itemView.findViewById(R.id.imgMessage);
        }
    }
}