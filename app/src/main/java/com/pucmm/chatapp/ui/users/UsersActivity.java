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

import com.google.firebase.auth.FirebaseAuth;
import com.pucmm.chatapp.MainActivity;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.data.repository.UserRepository;
import com.pucmm.chatapp.databinding.ActivityUsersBinding;
import com.pucmm.chatapp.ui.auth.LoginActivity;
import com.pucmm.chatapp.ui.chat.ChatActivity;
import com.pucmm.chatapp.viewmodel.UserViewModel;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private UserViewModel userViewModel;

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

        binding.recyclerContacts.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(0, 0, 0, bottomPadding);
            binding.topBar.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        binding.imgChats.setOnClickListener(v -> {
            Intent intent = new Intent(UsersActivity.this, MainActivity.class);
            startActivity(intent);
        });

        binding.imageView2.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(UsersActivity.this, binding.imageView2);

            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        cargarUsuarios();
    }

    private void cargarUsuarios() {

        userViewModel.getUsers(new UserRepository.UsersCallback() {

            @Override
            public void onSuccess(List<User> users) {

                if (users.isEmpty()) {
                    binding.recyclerContacts.setVisibility(View.GONE);
                    binding.txtEmptyContacts.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerContacts.setVisibility(View.VISIBLE);
                    binding.txtEmptyContacts.setVisibility(View.GONE);
                    ContactAdapter adapter = new ContactAdapter(users, user -> {

                        Intent intent = new Intent(UsersActivity.this, ChatActivity.class);

                        intent.putExtra("userId", user.getUserId());
                        intent.putExtra("userName", user.getUserName());
                        intent.putExtra("profileImage", user.getProfileImage());

                        startActivity(intent);
                    });
                    binding.recyclerContacts.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String error) {
                binding.recyclerContacts.setVisibility(View.GONE);
                binding.txtEmptyContacts.setVisibility(View.VISIBLE);
                binding.txtEmptyContacts.setText(error);
            }
        });
    }
}