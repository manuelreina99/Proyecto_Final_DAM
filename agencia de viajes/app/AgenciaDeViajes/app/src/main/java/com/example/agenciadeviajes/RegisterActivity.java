package com.example.agenciadeviajes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // 1. Declaración de variables (Símbolos)
    private EditText nombreEt, nifEt, emailEt, passEt;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 2. Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 3. Vinculación con los IDs del XML (¡Revisa que coincidan!)
        nombreEt = findViewById(R.id.reg_nombre);
        nifEt = findViewById(R.id.reg_nif);
        emailEt = findViewById(R.id.reg_email);
        passEt = findViewById(R.id.reg_password);
        btnRegistrar = findViewById(R.id.btn_register_final);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String email = emailEt.getText().toString().trim();
        String password = passEt.getText().toString().trim();
        String nombre = nombreEt.getText().toString().trim();
        String nif = nifEt.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Paso A: Crear usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Paso B: Guardar datos adicionales en Firestore
                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> cliente = new HashMap<>();
                        cliente.put("id_cliente_auth", uid); // Referencia al usuario
                        cliente.put("nif", nif);
                        cliente.put("nombre", nombre);
                        cliente.put("email", email);

                        db.collection("clientes").document(uid).set(cliente)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Registro completado", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Error en Firestore", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}