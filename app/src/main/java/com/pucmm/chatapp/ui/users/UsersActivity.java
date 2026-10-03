package com.pucmm.chatapp.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucmm.chatapp.MainActivity;
import com.pucmm.chatapp.data.model.User;
import com.pucmm.chatapp.data.repository.UserRepository;
import com.pucmm.chatapp.databinding.ActivityUsersBinding;
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
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

        binding.recyclerContacts.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            binding.topBar.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        binding.imgChats.setOnClickListener(v -> {
            Intent intent = new Intent(UsersActivity.this, MainActivity.class);
            startActivity(intent);
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
                    ContactAdapter adapter = new ContactAdapter(users);
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