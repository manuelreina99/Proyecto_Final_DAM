package com.example.agenciadeviajes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservaVueloActivity extends AppCompatActivity {

    private EditText origenEt, destinoEt, fechaEt;
    private Button btnConfirmar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Variable para almacenar la fecha seleccionada
    private Date fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_vuelo);

        // 1. Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 2. Vincular vistas
        origenEt = findViewById(R.id.et_origen);
        destinoEt = findViewById(R.id.et_destino);
        fechaEt = findViewById(R.id.et_fecha);
        btnConfirmar = findViewById(R.id.btn_confirmar_vuelo);

        // 3. Configurar el selector de fecha (Calendario)
        fechaEt.setOnClickListener(v -> mostrarCalendario());

        // 4. Configurar botón de confirmación
        btnConfirmar.setOnClickListener(v -> guardarReservaEnFirestore());
    }

    private void mostrarCalendario() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Guardamos la fecha en el objeto Calendar
                    c.set(year1, monthOfYear, dayOfMonth);
                    fechaSeleccionada = c.getTime();

                    // Mostramos la fecha en el EditText
                    String fechaMostrar = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    fechaEt.setText(fechaMostrar);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void guardarReservaEnFirestore() {
        String origen = origenEt.getText().toString().trim();
        String destino = destinoEt.getText().toString().trim();
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        // Validaciones
        if (uid == null) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (origen.isEmpty() || destino.isEmpty() || fechaSeleccionada == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Crear el mapa de datos con Timestamp
        Map<String, Object> reserva = new HashMap<>();
        reserva.put("id_usuario", uid);
        reserva.put("origen", origen);
        reserva.put("destino", destino);
        reserva.put("tipo", "vuelo");
        // Convertimos la fecha de Java a Timestamp de Firebase
        reserva.put("fecha", new Timestamp(fechaSeleccionada));
        reserva.put("creado_el", Timestamp.now()); // Fecha de creación del registro

        // 6. Guardar en la colección "reservas"
        db.collection("reservas")
                .add(reserva)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ReservaVueloActivity.this, "¡Reserva de vuelo guardada!", Toast.LENGTH_LONG).show();
                    finish(); // Regresa al MainActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReservaVueloActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}