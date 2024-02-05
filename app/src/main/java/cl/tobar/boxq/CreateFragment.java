package cl.tobar.boxq;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateFragment extends DialogFragment {

    String id_ejer;
    Button btn_agregar;
    EditText name, repe, weight, modo;

    private FirebaseFirestore mfirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica si el bundle no es nulo y contiene la clave "id_pet"
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("id_ejer")) {
            id_ejer = arguments.getString("id_ejer");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_create, container, false);
        mfirestore = FirebaseFirestore.getInstance();

        name = v.findViewById(R.id.Nombre);
        repe = v.findViewById(R.id.Reps);
        weight = v.findViewById(R.id.Carga);
        modo = v.findViewById(R.id.Modalidad);
        btn_agregar = v.findViewById(R.id.btn_agregar);

        if (id_ejer == null || id_ejer.equals("")){
            //Aqui le decimos que los datos que nos duo el user se pasa a string y sin espacios
            btn_agregar.setOnClickListener(view -> {
                String name_pet = name.getText().toString().trim();
                String color_pet = repe.getText().toString().trim();
                String weight_box = weight.getText().toString().trim();
                String mod_pet = modo.getText().toString().trim();

                //Si algun campo esta vacio se pide que ingrese los datos si no hace un post
                if(name_pet.isEmpty() && color_pet.isEmpty() && weight_box.isEmpty() && mod_pet.isEmpty()){
                    Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();

                }else{
                    postBox(name_pet, color_pet, weight_box, mod_pet);
                }
            });
        }else {
            getBox();
            btn_agregar.setText("Actualizar");
            //Aqui le decimos que los datos que nos dui el user se pasa a string y sin espacios
            btn_agregar.setOnClickListener(view -> {
                String name_pet = name.getText().toString().trim();
                String repe_box = repe.getText().toString().trim();
                String weight_box = weight.getText().toString().trim();
                String mod_box = modo.getText().toString().trim();


                //Si algun campo esta vacio se pide que ingrese los datos si no hace un post
                if(name_pet.isEmpty() && repe_box.isEmpty() && weight_box.isEmpty() && mod_box.isEmpty()){
                    Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();

                }else{
                    updateBox(name_pet, repe_box, weight_box, mod_box);
                }
            });
        }
        return v;
    }
    private void updateBox(String name_box, String repe_box, String weight_box, String mod_box) {
        Map <String, Object> map = new HashMap<>();
        map.put("name", name_box);
        map.put("repe", repe_box);
        map.put("weight", weight_box);
        map.put("mod", mod_box);

        //En caso de que tenga algun error
        mfirestore.collection("Pet").document(id_ejer).update(map).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getDialog()).dismiss();
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al Actualizar", Toast.LENGTH_SHORT).show());
    }
    private void postBox(String name_box, String repe_box, String weight_box, String mod_box) {

        //Se crea map para pasarle todos los datos y asi crearlos en la DB
        Map<String, Object> map = new HashMap<>();
        map.put("name", name_box);
        map.put("repe", repe_box);
        map.put("weight", weight_box);
        map.put("mod", mod_box);

        mfirestore.collection("Pet").add(map).addOnSuccessListener(documentReference -> {
            Toast.makeText(getContext(),"Creado exitosamente", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getDialog()).dismiss();

        }).addOnFailureListener(e -> Toast.makeText(getContext(),"Error al ingresar", Toast.LENGTH_SHORT).show());

    }

    private void getBox(){
        mfirestore.collection("Pet").document(id_ejer).get().addOnSuccessListener(documentSnapshot -> {
            String nameBox = documentSnapshot.getString("name");
            String weightbox = documentSnapshot.getString("weight");
            String repeBox = documentSnapshot.getString("repe");
            String modBox = documentSnapshot.getString("mod");

            name.setText(nameBox);
            weight.setText(weightbox);
            repe.setText(repeBox);
            modo.setText(modBox);

        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());
    }
}