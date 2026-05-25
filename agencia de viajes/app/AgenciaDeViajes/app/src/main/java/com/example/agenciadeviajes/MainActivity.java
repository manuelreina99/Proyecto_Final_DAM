package com.example.agenciadeviajes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout; // IMPORTANTE: Cambiado de CardView a LinearLayout
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvWelcome;
    private ImageButton btnLogout;

    // Ahora usamos LinearLayout porque el ID en el XML está en el contenedor interno
    private LinearLayout btnVuelo, btnHotel, btnReservas, btnPerfil, btnMapa, btnOfertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 2. Vincular Vistas
        tvWelcome = findViewById(R.id.tv_welcome);
        btnLogout = findViewById(R.id.btn_logout);

        btnVuelo = findViewById(R.id.card_vuelo);
        btnHotel = findViewById(R.id.card_hotel);
        btnReservas = findViewById(R.id.card_reservas);
        btnPerfil = findViewById(R.id.card_perfil);
        btnMapa = findViewById(R.id.card_mapa);
        btnOfertas = findViewById(R.id.cardOfertas);

        // 3. Personalizar Bienvenida
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String nombre = (email != null && email.contains("@")) ? email.split("@")[0] : "Aventurero";
            tvWelcome.setText("Hola, " + nombre);
        }

        // 4. Configurar Eventos de Clic
        setupListeners();
    }

    private void setupListeners() {
        // Ofertas (Vuelos disponibles para reservar)
        btnOfertas.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, VuelosDisponiblesActivity.class));
        });

        // Mis Viajes (Lista de lo que ya he reservado)
        btnReservas.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MisReservasActivity.class));
        });

        // Mapa (Aviones en vivo)
        btnMapa.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, VuelosDirecto.class));
        });

        // Mi Perfil (Datos del usuario)
        btnPerfil.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PerfilActivity.class));
        });

        // Vuelos (Búsqueda manual - opcional)
        btnVuelo.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReservaVueloActivity.class));
        });

        // Hoteles (Búsqueda manual - opcional)
        btnHotel.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReservaHotelActivity.class));
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}