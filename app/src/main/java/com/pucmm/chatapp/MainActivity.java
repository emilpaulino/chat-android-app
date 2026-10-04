package com.pucmm.chatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pucmm.chatapp.data.model.Chat;
import com.pucmm.chatapp.databinding.ActivityMainBinding;
import com.pucmm.chatapp.ui.chat.ChatAdapter;
import com.pucmm.chatapp.ui.users.UsersActivity;
import com.pucmm.chatapp.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Log.e("FCM", "Error al obtener el token", task.getException());
                return;
            }

            String token = task.getResult();

            Log.d("FCM", "Token: " + token);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> data = new HashMap<>();
            data.put("fcmToken", token);

            FirebaseFirestore.getInstance("chat-app")
                    .collection("users")
                    .document(userId)
                    .update(data)
                    .addOnSuccessListener(aVoid -> Log.d("FCM", "Token guardado en Firestore"))
                    .addOnFailureListener(error -> Log.e("FCM", "Error al guardar token", error));
        });

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