package com.pucmm.chatapp.ui.auth;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.pucmm.chatapp.R;
import com.pucmm.chatapp.data.repository.AuthRepository;
import com.pucmm.chatapp.databinding.ActivityRegisterBinding;
import com.pucmm.chatapp.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerChats), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.imgBack.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {

        String name = binding.txtName.getText().toString().trim();
        String email = binding.txtEmail.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();

        if (!validarCampos(name, email, password)) {
            return;
        }

        authViewModel.register(name, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validarCampos(String name, String email, String password) {

        if(name.isEmpty()){
            binding.txtName.setError("Ingrese su nombre");
            binding.txtName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            binding.txtEmail.setError("Ingrese su correo");
            binding.txtEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmail.setError("Ingrese un correo electrónico válido");
            binding.txtEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.txtPassword.setError("Ingrese su contraseña");
            binding.txtPassword.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            binding.txtPassword.setError("La contraseña debe tener al menos 8 caracteres");
            binding.txtPassword.requestFocus();
            return false;
        }

        return true;
    }
}