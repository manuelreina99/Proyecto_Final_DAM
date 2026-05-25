package com.example.agenciadeviajes;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MisReservasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservaAdapter adapter;
    private List<Map<String, Object>> listaReservas;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas); // Asegúrate de crear este XML con un RecyclerView con id 'rv_reservas'

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.rv_reservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaReservas = new ArrayList<>();
        adapter = new ReservaAdapter(listaReservas);
        recyclerView.setAdapter(adapter);

        cargarReservas();
    }

    private void cargarReservas() {
        String uid = mAuth.getCurrentUser().getUid();

        // Consulta filtrando por el ID del usuario y ordenando por fecha
        db.collection("reservas")
                .whereEqualTo("id_usuario", uid)
                .orderBy("fecha", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaReservas.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            listaReservas.add(document.getData());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}