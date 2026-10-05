package com.pucmm.chatapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.pucmm.chatapp.MainActivity;
import com.pucmm.chatapp.R;
import com.pucmm.chatapp.data.repository.AuthRepository;
import com.pucmm.chatapp.databinding.ActivityLoginBinding;
import com.pucmm.chatapp.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializando viewmodel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String email = binding.txtEmailLogin.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();

        if (!validarCampos(email, password)) {
            return;
        }

        authViewModel.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(LoginActivity.this, "Se ha iniciado sesión correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validarCampos(String email, String password) {
        if (email.isEmpty()) {
            binding.txtEmailLogin.setError("Ingrese su correo");
            binding.txtEmailLogin.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmailLogin.setError("Ingrese un correo electrónico válido");
            binding.txtEmailLogin.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            binding.txtPassword.setError("Ingrese su contraseña");
            binding.txtPassword.requestFocus();
            return false;
        }
        return true;
    }
}