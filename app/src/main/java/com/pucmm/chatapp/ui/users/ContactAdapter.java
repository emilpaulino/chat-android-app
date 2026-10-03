package com.pucmm.chatapp.ui.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.databinding.ItemContactBinding;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        final ItemContactBinding binding;
        public ContactViewHolder(@NonNull ItemContactBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    List<User> userList;

    public ContactAdapter(List<User> userList){
        this.userList = userList;
    }


    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactBinding binding = ItemContactBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactAdapter.ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.txtUserName.setText(user.getUserName());
        holder.binding.txtContactEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
