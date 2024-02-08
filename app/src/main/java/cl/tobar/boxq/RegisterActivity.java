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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText name, email, password;

    FirebaseDatabase database;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


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

                if (nameUser.isEmpty() && emailUser.isEmpty() && passUser.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Completa los datos", Toast.LENGTH_SHORT).show();

                }else{
                    registerUser(nameUser, emailUser, passUser);
                }
            }

            private void registerUser(String nameUser, String emailUser, String passUser) {
                mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Obtener el ID único del usuario registrado
                                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                    // Crear un mapa con los datos del usuario
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("id", userId);
                                    map.put("name", nameUser);
                                    map.put("email", emailUser);
                                    map.put("password", passUser);

                                    database.getReference().child("users").child(userId).setValue(map);
                                    mFirestore.collection("user").document(userId).set(map);

                                    // Guardar los datos del usuario en Firestore bajo una colección con el ID del usuario
                                    mFirestore.collection(userId).document("profile").set(map)


                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Éxito al guardar en Firestore
                                                    finish();
                                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    Toast.makeText(RegisterActivity.this, "Usuario Registrado con éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error al guardar en Firestore
                                                    Toast.makeText(RegisterActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Error al registrar al usuario
                                    Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}