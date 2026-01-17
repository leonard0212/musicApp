package com.example.musicplayer;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText etUsername, etEmail, etPassword;
    Button btnRegister;
    TextView tvGoToLogin;
    PocketBaseHelper pbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pbHelper = new PocketBaseHelper(this);

        etUsername = findViewById(R.id.etRegUsername);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) return;

            pbHelper.register(username, email, password, success -> {
                if (success) {
                    Toast.makeText(this, "Registered! Please Login.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
        tvGoToLogin.setOnClickListener(v -> finish());
    }
}
