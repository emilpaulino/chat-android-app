package com.pucmm.chatapp.ui.chat;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.ListenerRegistration;
import com.pucmm.chatapp.data.model.Message;
import com.pucmm.chatapp.databinding.ActivityChatBinding;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.viewmodel.AuthViewModel;
import com.pucmm.chatapp.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatViewModel chatViewModel;
    private AuthViewModel authViewModel;
    private String currentUserId;
    private String chatId;
    private MessageAdapter messageAdapter;
    private ListenerRegistration messageListener;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;

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

        // Inicializando ViewModels
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        currentUserId = authViewModel.getCurrentUserId();

        // Configurando la conversación
        chatId = chatViewModel.generateChatId(currentUserId, userId);
        List<Message> messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);

        // Configurando RecyclerView
        binding.recyclerConversation.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerConversation.setAdapter(messageAdapter);

        // Escuchando mensajes en tiempo real
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
                Log.e("CHAT", "Error al cargar mensajes: " + error);
            }
        });

        // Enviando mensaje
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.txtMessage.getText().toString().trim();

            if (text.isEmpty()) {
                return;
            }

            chatViewModel.sendMessage(chatId, currentUserId, userId, text, new ChatViewModel.MessageCallback() {
                @Override
                public void onSuccess() {
                    binding.txtMessage.setText("");
                }
                @Override
                public void onError(String error) {
                }
            });
        });

        // Seleccionando imagen desde la galeria
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                chatViewModel.sendImage(chatId, currentUserId, userId, uri, new ChatViewModel.ImageCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(String error) {
                       Log.e("CHAT_IMAGE", "Error al subir imagen: " + error);
                    }
                });
            }
        });

        // Tomando imagen desde la camar
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap != null) {
                Uri imageUri = saveBitmapToGallery(bitmap);
                if (imageUri == null) {
                    return;
                }

                chatViewModel.sendImage(chatId, currentUserId, userId, imageUri, new ChatViewModel.ImageCallback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(String error) {
                        android.util.Log.e("CHAT_IMAGE", "Error al subir imagen: " + error);
                    }
                });
            }
        });

        // Mostrando opciones para enviar imagenes
        binding.btnImage.setOnClickListener(v -> {
            String[] options = {"Cámara", "Galería"};

            new androidx.appcompat.app.AlertDialog.Builder(ChatActivity.this).setTitle("Seleccionar imagen").setItems(options, (dialog, which) -> {
                if (which == 0) {
                    cameraLauncher.launch(null);
                } else {
                    galleryLauncher.launch("image/*");
                }
            }).show();
        });

        // Volver atras
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

    private Uri saveBitmapToGallery(Bitmap bitmap) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "chat_image_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri == null) {
            return null;
        }

        try {
            java.io.OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
            if (outputStream == null) {
                return null;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();
            return imageUri;
        } catch (Exception e) {
            return null;
        }
    }

}