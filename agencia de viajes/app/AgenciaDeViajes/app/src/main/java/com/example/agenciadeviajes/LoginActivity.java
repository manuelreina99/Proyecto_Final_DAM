package com.example.agenciadeviajes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEt, passEt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicializarmusica
        MusicaManager.reproducirMusica(this);

        mAuth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.email_login);
        passEt = findViewById(R.id.password_login);

        // Botón de Login
        findViewById(R.id.btn_login).setOnClickListener(v -> login());

        // Botón para ir al Registro
        findViewById(R.id.btn_go_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = emailEt.getText().toString().trim();
        String pass = passEt.getText().toString().trim();

        // 1. Validaciones previas para evitar llamadas innecesarias a Firebase
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Introduce tu email");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            passEt.setError("Introduce tu contraseña");
            return;
        }

        // 2. Intento de Login
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Éxito: Ir al MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Error: Gestionamos el fallo para que no pete
                String errorMsg = "Fallo al iniciar sesión";

                if (task.getException() != null) {
                    Exception e = task.getException();

                    if (e instanceof FirebaseAuthInvalidUserException) {
                        errorMsg = "Este usuario no está registrado";
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMsg = "Contraseña incorrecta";
                    } else {
                        errorMsg = "Error: " + e.getLocalizedMessage();
                    }
                }

                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}