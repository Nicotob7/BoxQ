package cl.tobar.boxq;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Create extends AppCompatActivity {

    Button btn_agregar;
    EditText name, repe, weight, mod;

    private FirebaseFirestore mfirestore;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //Titulo del layout + boton para retroceder a home
        this.setTitle("Agregar ejercicio");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra("id_ejer");

        //Apuntamos hacia la base de datos para obtener la instancia
        mfirestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.Nombre);
        repe = findViewById(R.id.Reps);
        weight = findViewById(R.id.Carga);

        btn_agregar = findViewById(R.id.btn_agregar);

        if (id == null || id.equals("")){
            //Si se hace click en el boton de agregar se ejecuta esto, si es nulo se crea
            btn_agregar.setOnClickListener(view -> {
                String name_box = name.getText().toString().trim();
                String repe_box = repe.getText().toString().trim();
                String weight_box = weight.getText().toString().trim();
                String mod_box = mod.getText().toString().trim();

                // Si algún campo está vacío, se pide que ingrese los datos; de lo contrario, se realiza un post
                if (name_box.isEmpty() && repe_box.isEmpty() && weight_box.isEmpty() && mod_box.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                } else {
                    postBox(name_box, repe_box, weight_box, mod_box);
                }
            });
            //Es el caso contrario, se actualiza la informacion
        }else {
            btn_agregar.setText("Actualizado");
            getBox(id);
            btn_agregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name_box = name.getText().toString().trim();
                    String color_pet = repe.getText().toString().trim();
                    String weight_box = weight.getText().toString().trim();
                    String mod_box = mod.getText().toString().trim();

                    // Si algún campo está vacío, se pide que ingrese los datos; de lo contrario, se realiza un post
                    if (name_box.isEmpty() && color_pet.isEmpty() && weight_box.isEmpty() && mod_box.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    } else {
                        updateBox(name_box, color_pet, weight_box, mod_box, id);
                    }
                }

                private void updateBox(String name_pet, String color_pet, String weight_box, String mod_pet, String id) {
                    Map <String, Object> map = new HashMap<>();
                    map.put("name", name_pet);
                    map.put("color", color_pet);
                    map.put("weight", weight_box);
                    map.put("mod", mod_pet);

                    //En caso de que tenga algun error
                    mfirestore.collection("Pet").document(id).update(map).addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al Actualizar", Toast.LENGTH_SHORT).show());
                }
            });
            }

        }

    private void postBox(String name_pet, String color_pet, String weight_box, String mod_box) {

        //Se crea map para pasarle todos los datos y asi crearlos en la DB
        Map <String, Object> map = new HashMap<>();
        map.put("name", name_pet);
        map.put("color", color_pet);
        map.put("weight", weight_box);
        map.put("mod", mod_box);

        mfirestore.collection("Pet").add(map)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                //En caso de que tenga algun error
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show()
                );
    }

    //Obtiene los datos de la db
    private void getBox(String id){
        mfirestore.collection("Pet").document(id).get().addOnSuccessListener(documentSnapshot -> {
            String namePet = documentSnapshot.getString("name");
            String weightbox = documentSnapshot.getString("weight");
            String colorPet = documentSnapshot.getString("color");
            String modPet = documentSnapshot.getString("mod");

            name.setText(namePet);
            weight.setText(weightbox);
            repe.setText(colorPet);
            mod.setText(modPet);

        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Cierra la actividad actual
        return super.onSupportNavigateUp();
    }
}


//Sacare esta ventana seguramente

//Aca podria hacer un validador con el numero de celular o podria ser con el correro para que no se creen cuentas de mas