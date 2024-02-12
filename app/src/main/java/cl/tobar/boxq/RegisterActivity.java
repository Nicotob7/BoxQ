package cl.tobar.boxq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText name, email, password;

    DatabaseReference databaseReference;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        name = findViewById(R.id.name_correo);
        email = findViewById(R.id.correo);
        password = findViewById(R.id.contraseña);

        btn_register = findViewById(R.id.btn_registro);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameUser = name.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();

                if (nameUser.isEmpty() || emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Completa los datos", Toast.LENGTH_SHORT).show();
                } else if (passUser.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(nameUser, emailUser, passUser);
                }
            }

            private void registerUser(String nameUser, String emailUser, String passUser) {
                mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailTask) {
                                                if (emailTask.isSuccessful()) {
                                                    // Datos del usuario
                                                    String userId = user.getUid();
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("id", userId);
                                                    map.put("name", nameUser);
                                                    map.put("email", emailUser);
                                                    map.put("password", passUser);

                                                    // Guardar datos del usuario en Firebase Realtime Database
                                                    databaseReference.child(userId).setValue(map);
                                                    mFirestore.collection(userId).document("profile").set(map)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> dbTask) {
                                                                    if (dbTask.isSuccessful()) {
                                                                        // Guardar datos del usuario en Cloud Firestore
                                                                        mFirestore.collection("user").document(userId).set(map)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> firestoreTask) {
                                                                                        if (firestoreTask.isSuccessful()) {
                                                                                            Toast.makeText(RegisterActivity.this, "Se ha enviado un correo de verificación a " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                                                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                                            finish();
                                                                                        } else {
                                                                                            Toast.makeText(RegisterActivity.this, "Error al guardar datos en Firestore", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        Toast.makeText(RegisterActivity.this, "Error al guardar datos en Realtime Database", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Error al enviar correo de verificación", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });
    }
}
