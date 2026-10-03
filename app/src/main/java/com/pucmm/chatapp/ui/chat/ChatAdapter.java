package com.pucmm.chatapp.ui.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pucmm.chatapp.databinding.ItemChatBinding;
import com.pucmm.chatapp.data.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    //Viewholder
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        final ItemChatBinding binding;
        public ChatViewHolder(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private List<Chat> chatList;

    public ChatAdapter(List<Chat> chatList){
        this.chatList = chatList;
    }

    // Inflamos el diseño de la fila item chat
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    // Ponemos los datos de la lista en los TextViews correspondientes
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.binding.txtUserName.setText(chat.getUser().getUserName());
        holder.binding.txtMessageDesc.setText(chat.getLastMessage().getText());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


}