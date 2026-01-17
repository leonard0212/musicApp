package com.example.musicplayer;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;
    PocketBaseHelper pbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pbHelper = new PocketBaseHelper(this);

        if (pbHelper.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields empty", Toast.LENGTH_SHORT).show();
                return;
            }

            pbHelper.login(email, password, success -> {
                if (success) {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}
