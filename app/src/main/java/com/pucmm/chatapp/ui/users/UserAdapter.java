package com.pucmm.chatapp.ui.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.databinding.ItemUserBinding;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private final List<User> userList;
    private final OnUserClickListener listener;

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.binding.txtUserName.setText(user.getUserName());
        holder.binding.txtUserEmail.setText(user.getEmail());

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfileImage())
                    .circleCrop()
                    .into(holder.binding.imgProfile);
        } else {
                holder.binding.imgProfile.setImageResource(R.drawable.ic_acc_circle);
                holder.binding.imgProfile.setColorFilter(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.primary)
                );
            }

        holder.itemView.setOnClickListener(v -> {
            listener.onUserClick(user);
        });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
}
