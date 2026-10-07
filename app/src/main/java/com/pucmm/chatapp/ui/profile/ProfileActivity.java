package com.pucmm.chatapp.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.pucmm.chatapp.data.repository.UserRepository;
import com.pucmm.chatapp.databinding.ActivityProfileBinding;
import com.pucmm.chatapp.viewmodel.AuthViewModel;
import com.pucmm.chatapp.viewmodel.UserViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;
    private ActivityResultLauncher<String> galleryLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
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

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.toolbarProfile.setNavigationOnClickListener(v -> {
            finish();
        });

        String currentUserId = authViewModel.getCurrentUserId();

        userViewModel.getUser(currentUserId, new UserRepository.UserCallback() {

            @Override
            public void onSuccess(com.pucmm.chatapp.data.model.User user) {
                binding.txtUserName.setText(user.getUserName());

                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    Glide.with(ProfileActivity.this)
                            .load(user.getProfileImage())
                            .circleCrop()
                            .into(binding.imgProfile);
                }
            }

            @Override
            public void onError(String error) {
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;

                        Glide.with(ProfileActivity.this)
                                .load(uri)
                                .circleCrop()
                                .into(binding.imgProfile);
                    }
                }
        );

        binding.txtChangePhoto.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        binding.btnSave.setOnClickListener(v -> {
            String userName = binding.txtUserName.getText().toString().trim();

            if (userName.isEmpty()) {
                binding.txtUserName.setError("Escribe tu nombre");
                return;
            }

            userViewModel.updateUserName(
                    currentUserId,
                    userName,
                    new UserRepository.TokenCallback() {

                        @Override
                        public void onSuccess() {
                            if (selectedImageUri != null) {
                                userViewModel.updateProfileImage(
                                        currentUserId,
                                        selectedImageUri,
                                        new UserRepository.TokenCallback() {

                                            @Override
                                            public void onSuccess() {
                                                finish();
                                            }

                                            @Override
                                            public void onError(String error) {
                                            }
                                        }
                                );
                            } else {
                                finish();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });
    }
}