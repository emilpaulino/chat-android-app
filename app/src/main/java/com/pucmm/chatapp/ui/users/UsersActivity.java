package com.pucmm.chatapp.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucmm.chatapp.MainActivity;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.data.repository.UserRepository;
import com.pucmm.chatapp.databinding.ActivityUsersBinding;
import com.pucmm.chatapp.ui.auth.LoginActivity;
import com.pucmm.chatapp.ui.chat.ChatActivity;
import com.pucmm.chatapp.ui.profile.ProfileActivity;
import com.pucmm.chatapp.viewmodel.AuthViewModel;
import com.pucmm.chatapp.viewmodel.UserViewModel;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private UserViewModel userViewModel;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUsersBinding.inflate(getLayoutInflater());
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

        // Inicializando viewmodels
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Configurando RecyclerView
        binding.recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        // Navegacion
        binding.imgChats.setOnClickListener(v -> {
            Intent intent = new Intent(UsersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Logout
        binding.imageView2.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(UsersActivity.this, binding.imageView2);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    authViewModel.logout();
                    Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.edit_profile) {
                    Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                    startActivity(intent);

                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        String currentUserId = authViewModel.getCurrentUserId();

        userViewModel.getUsers(currentUserId, new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                if (users.isEmpty()) {
                    binding.recyclerUsers.setVisibility(View.GONE);
                    binding.txtEmptyContacts.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerUsers.setVisibility(View.VISIBLE);
                    binding.txtEmptyContacts.setVisibility(View.GONE);

                    UserAdapter adapter = new UserAdapter(users, user -> {
                        Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
                        intent.putExtra("userId", user.getUserId());
                        intent.putExtra("userName", user.getUserName());
                        intent.putExtra("profileImage", user.getProfileImage());
                        startActivity(intent);
                    });

                    binding.recyclerUsers.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String error) {
                binding.recyclerUsers.setVisibility(View.GONE);
                binding.txtEmptyContacts.setVisibility(View.VISIBLE);
                binding.txtEmptyContacts.setText(error);
            }
        });
    }
}