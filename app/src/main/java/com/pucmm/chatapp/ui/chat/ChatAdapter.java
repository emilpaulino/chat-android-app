package com.pucmm.chatapp.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pucmm.chatapp.R;
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

        if (chat.getUser().getProfileImage() != null && !chat.getUser().getProfileImage().isEmpty()) {

            holder.binding.imgProfile.clearColorFilter();

            Glide.with(holder.itemView.getContext())
                    .load(chat.getUser().getProfileImage())
                    .circleCrop()
                    .into(holder.binding.imgProfile);

        } else {
            holder.binding.imgProfile.setImageResource(R.drawable.ic_acc_circle);
            holder.binding.imgProfile.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.primary)
            );
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onChatClick(chat);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}