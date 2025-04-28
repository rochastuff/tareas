package com.escuela.tareas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TareaAdapter extends ArrayAdapter<Tarea> {
    private DatabaseReference mDatabaseTareas;
    public Boolean esAlumno;
    public String usuarioEmail;

    public TareaAdapter(Context context, List<Tarea> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_tarea, parent, false);
        }

        Tarea tarea = getItem(position);
        TextView itemTextoTarea = convertView.findViewById(R.id.textoTarea);
        Button itemBotonTerminar = convertView.findViewById(R.id.botonTerminar);
        Button itemBotonPunto = convertView.findViewById(R.id.botonPunto);

        itemTextoTarea.setText(tarea.descripcion);

        if(esAlumno) {
            //es Alumno
            itemBotonPunto.setVisibility(View.GONE);
            if(tarea.terminada) {
                itemBotonTerminar.setEnabled(false);
                itemBotonTerminar.setText("✓");
            } else {
                itemBotonTerminar.setEnabled(true);
            }

        } else {
            //es Tutor
            itemBotonTerminar.setVisibility(View.GONE);
            if(tarea.terminada) {
                if(tarea.puntoDado) {
                    itemBotonPunto.setEnabled(false);
                    itemBotonPunto.setText("+Punto Agregado");
                } else {
                    itemBotonPunto.setEnabled(true);
                }
            } else {
                itemBotonPunto.setEnabled(false);
            }
        }

        itemBotonTerminar.setOnClickListener(v -> {
            mDatabaseTareas = FirebaseDatabase.getInstance().getReference("tareas");
            mDatabaseTareas.child(tarea.tareaId).child("terminada").setValue(true);
            itemBotonTerminar.setEnabled(false);
            itemBotonTerminar.setText("✓");
            Toast.makeText(getContext(), "Tarea marcada como terminada!", Toast.LENGTH_SHORT).show();
        });

        itemBotonPunto.setOnClickListener(v -> {
            DatabaseReference mDatabaseUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
            mDatabaseUsuarios.orderByChild("tutor").equalTo(usuarioEmail).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                mDatabaseUsuarios.child(dataSnapshot.getKey()).child("puntos").setValue(usuario.puntos+1);
                                mDatabaseTareas = FirebaseDatabase.getInstance().getReference("tareas");
                                mDatabaseTareas.child(tarea.tareaId).child("puntoDado").setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }
            );

            itemBotonPunto.setEnabled(false);
            itemBotonPunto.setText("+Punto Agregado");
        });

        return convertView;
    }
}
