package com.escuela.tareas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseTareas;
    private DatabaseReference mDatabaseUsuarios;
    private EditText textoNuevaTarea;
    private Button botonAgregarTarea;
    private Button botonSalir;
    private TextView textPuntos;
    private ListView listViewTareas;
    private TareaAdapter tareaAdapter;
    private ArrayList<Tarea> listaTareas;
    private String usuarioActual;
    private String tutorActual;
    private Boolean esAlumno;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirigir a LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseTareas = FirebaseDatabase.getInstance().getReference("tareas");
        mDatabaseUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        textoNuevaTarea = findViewById(R.id.textNuevaTarea);
        botonAgregarTarea = findViewById(R.id.botonAgregarTarea);
        listViewTareas = findViewById(R.id.listaTareas);
        botonSalir = findViewById(R.id.botonSalir);
        textPuntos = findViewById(R.id.textPuntos);

        listaTareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(this, listaTareas);
        listViewTareas.setAdapter(tareaAdapter);

        FirebaseUser user = mAuth.getCurrentUser();
        mDatabaseUsuarios.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        DataSnapshot ds = task.getResult();
                        usuarioActual = String.valueOf(ds.child("usuario").getValue());
                        tutorActual = String.valueOf(ds.child("tutor").getValue());
                        textPuntos.setText("Puntos: " + String.valueOf(ds.child("puntos").getValue()));

                        if(!usuarioActual.isEmpty() && !tutorActual.isEmpty()) {
                            esAlumno = true;
                        } else {
                            esAlumno = false;
                            textPuntos.setVisibility(View.GONE);
                            textoNuevaTarea.setVisibility(View.GONE);
                            botonAgregarTarea.setVisibility(View.GONE);
                            listViewTareas.getLayoutParams().height =
                                    listViewTareas.getLayoutParams().height +
                                    textoNuevaTarea.getLayoutParams().height +
                                    botonAgregarTarea.getLayoutParams().height;
                        }
                        tareaAdapter.esAlumno = esAlumno;
                        tareaAdapter.usuarioEmail = usuarioActual;
                    }
                }
            }
        });

        botonAgregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoTarea = textoNuevaTarea.getText().toString();
                if (!textoTarea.isEmpty()) {
                    String tareaId = mDatabase.push().getKey();
                    Tarea tarea = new Tarea(tareaId, textoTarea, usuarioActual, tutorActual, false, false);
                    mDatabaseTareas.child(tareaId).setValue(tarea);
                    textoNuevaTarea.setText("");
                }
            }
        });

        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mDatabase.child("tareas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaTareas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Tarea tarea = new Tarea(
                            String.valueOf(ds.child("tareaId").getValue()),
                            String.valueOf(ds.child("descripcion").getValue()),
                            String.valueOf(ds.child("alumnoEmail").getValue()),
                            String.valueOf(ds.child("tutorEmail").getValue()),
                            Boolean.valueOf(String.valueOf(ds.child("terminada").getValue())),
                            Boolean.valueOf(String.valueOf(ds.child("puntoDado").getValue()))
                    );

                    if(esAlumno != null) {
                        //Para alumnos
                        if (esAlumno && tarea.alumnoEmail.equals(usuarioActual)) {
                            listaTareas.add(tarea);
                        }

                        //Para tutores
                        if (!esAlumno && tarea.tutorEmail.equals(usuarioActual)) {
                            listaTareas.add(tarea);
                        }
                    }
                }
                tareaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error en la lectura de datos
            }
        });
    }
}
