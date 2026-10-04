package com.pucmm.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.pucmm.chatapp.data.model.Chat;
import com.pucmm.chatapp.databinding.ActivityMainBinding;
import com.pucmm.chatapp.ui.chat.ChatAdapter;
import com.pucmm.chatapp.ui.users.UsersActivity;
import com.pucmm.chatapp.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private ListenerRegistration chatListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightNavigationBars(true);

        binding.recyclerChats.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(0, 0, 0, bottomPadding);
            binding.topBar.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        chatList = new ArrayList<>();

        chatAdapter = new ChatAdapter(chatList, chat -> {
            Intent intent = new Intent(MainActivity.this, com.pucmm.chatapp.ui.chat.ChatActivity.class);
            intent.putExtra("userId", chat.getUser().getUserId());
            intent.putExtra("userName", chat.getUser().getUserName());
            intent.putExtra("profileImage", chat.getUser().getProfileImage());
            startActivity(intent);
        });

        binding.recyclerChats.setAdapter(chatAdapter);

        String currentUserId =
                FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatListener = chatViewModel.listenChats(
                currentUserId,
                new ChatViewModel.ChatsCallback() {

                    @Override
                    public void onSuccess(List<Chat> chats) {

                        chatList.clear();
                        chatList.addAll(chats);

                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String error) {
                    }
                }
        );

        binding.imgContacts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (chatListener != null) {
            chatListener.remove();
        }
    }
}