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

import com.google.firebase.firestore.ListenerRegistration;
import com.pucmm.chatapp.data.model.Chat;
import com.pucmm.chatapp.data.repository.UserRepository;
import com.pucmm.chatapp.databinding.ActivityMainBinding;
import com.pucmm.chatapp.ui.auth.LoginActivity;
import com.pucmm.chatapp.ui.chat.ChatActivity;
import com.pucmm.chatapp.ui.chat.ChatAdapter;
import com.pucmm.chatapp.ui.users.UsersActivity;
import com.pucmm.chatapp.viewmodel.AuthViewModel;
import com.pucmm.chatapp.viewmodel.ChatViewModel;
import com.pucmm.chatapp.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ChatViewModel chatViewModel;
    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(0, 0, 0, bottomPadding);
            binding.topBar.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        // Inicializando viewmodel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Permisos para notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        // Configurando el toolbar
        binding.toolbarChat.inflateMenu(R.menu.menu_main);
        binding.toolbarChat.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                authViewModel.logout();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                return true;
            }

            return false;
        });

        actualizarTokenFCM();

        // Configurando el RecyclerView
        binding.recyclerChats.setLayoutManager(new LinearLayoutManager(this));
        configurarChatAdapter();
        escucharChats();

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

    private void actualizarTokenFCM(){
        userViewModel.updateFcmToken(
                authViewModel.getCurrentUserId(),
                new UserRepository.TokenCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("FCM", "Token guardado en Firestore");
                    }
                    @Override
                    public void onError(String error) {
                        Log.e("FCM", "Error al guardar token: " + error);
                    }
                }
        );
    }

    private void configurarChatAdapter() {
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, chat -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("userId", chat.getUser().getUserId());
            intent.putExtra("userName", chat.getUser().getUserName());
            intent.putExtra("profileImage", chat.getUser().getProfileImage());
            startActivity(intent);
        });
        binding.recyclerChats.setAdapter(chatAdapter);
    }

    private void escucharChats() {
        String currentUserId = authViewModel.getCurrentUserId();
        chatListener = chatViewModel.listenChats(currentUserId, new ChatViewModel.ChatsCallback() {
            @Override
            public void onSuccess(List<Chat> chats) {
                chatList.clear();
                chatList.addAll(chats);
                chatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String error) {
            }
        });
    }
}