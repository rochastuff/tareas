package com.escuela.tareas;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText emailTutorInput;
    private Button loginButton;
    private Button singupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsuarios;
    private Boolean esAlumnoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_Tareas);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        emailTutorInput = findViewById(R.id.emailTutorInput);
        loginButton = findViewById(R.id.loginButton);
        singupButton = findViewById(R.id.singupButton);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        singupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String tutor = emailTutorInput.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Usuario usuario = new Usuario(email, tutor);
                                        mDatabase.child("usuarios").child(user.getUid()).setValue(usuario);

                                        if (tutor.isEmpty()) {
                                            Toast.makeText(LoginActivity.this, "Tutor registrado!\nIngrese con su email y contraseña", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Alumno registrado!\nIngrese con su email y contraseña", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Usuario ya existente", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Por favor, ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUsuario = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!emailUsuario.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(emailUsuario, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this, "Bienvenido!", Toast.LENGTH_SHORT).show();

                                        // Redirigir a MainActivity
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Por favor, ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
