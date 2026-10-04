package com.pucmm.chatapp.ui.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.databinding.ActivityChatBinding;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatViewModel chatViewModel;
    private String currentUserId;
    private String chatId;
    private MessageAdapter messageAdapter;
    private ListenerRegistration messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightNavigationBars(true);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(0, 0, 0, bottomPadding);
            binding.toolbarConversation.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        String userId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profileImage = getIntent().getStringExtra("profileImage");

        binding.txtUserName.setText(userName);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        chatId = chatViewModel.generateChatId(currentUserId, userId);
        List<Message> messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);

        binding.recyclerConversation.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerConversation.setAdapter(messageAdapter);

        messageListener = chatViewModel.listenMessages(chatId, new ChatViewModel.MessagesCallback() {

            @Override
            public void onSuccess(List<Message> messages) {

                messageList.clear();
                messageList.addAll(messages);

                messageAdapter.notifyDataSetChanged();

                if (!messages.isEmpty()) {
                    binding.recyclerConversation.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onError(String error) {
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.txtMessage.getText().toString().trim();

            if (text.isEmpty()) {
                return;
            }

            chatViewModel.sendMessage(chatId, currentUserId, text, new ChatViewModel.MessageCallback() {
                @Override
                public void onSuccess() {
                    binding.txtMessage.setText("");
                }

                @Override
                public void onError(String error) {
                }
            });
        });

        binding.toolbarConversation.setNavigationOnClickListener(v -> {
            finish();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (messageListener != null) {
            messageListener.remove();
        }
    }

}