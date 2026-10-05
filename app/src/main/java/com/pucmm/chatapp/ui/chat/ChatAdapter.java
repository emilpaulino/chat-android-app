package com.pucmm.chatapp.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.chatapp.data.model.Chat;
import com.pucmm.chatapp.databinding.ItemChatBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        final ItemChatBinding binding;

        public ChatViewHolder(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private final List<Chat> chatList;
    private final OnChatClickListener listener;

    public ChatAdapter(List<Chat> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    // Creando viewholder
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    // Mostrando datos del chat
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.binding.txtUserName.setText(chat.getUser().getUserName());
        holder.binding.txtMessageDesc.setText(chat.getLastMessage().getText());
        holder.itemView.setOnClickListener(v -> {
            listener.onChatClick(chat);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}