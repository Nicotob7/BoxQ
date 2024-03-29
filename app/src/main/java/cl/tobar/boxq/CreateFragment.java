package cl.tobar.boxq;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CreateFragment extends DialogFragment {

    String id_ejer, name_box, formattedDate;
    Button btn_agregar;
    EditText repe, weight;

    Spinner spinnerNombre, spinnerModalidad;

    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle args = getArguments();
        if (args != null) {
            id_ejer = args.getString("id_ejer");
        }



        View v =  inflater.inflate(R.layout.fragment_create, container, false);
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        spinnerNombre = v.findViewById(R.id.Nombre);
        repe = v.findViewById(R.id.Reps);
        weight = v.findViewById(R.id.Carga);
        spinnerModalidad = v.findViewById(R.id.Modalidad);
        btn_agregar = v.findViewById(R.id.btn_agregar);

        //Adaptadores para los Spinners
        ArrayAdapter<CharSequence> nombreAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.op_nombre, android.R.layout.simple_spinner_item);
        nombreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNombre.setAdapter(nombreAdapter);

        ArrayAdapter<CharSequence> modalidadAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.op_modalidad, android.R.layout.simple_spinner_item);
        modalidadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModalidad.setAdapter(modalidadAdapter);

        // Obtener el ID de usuario actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Si no hay usuario autenticado, salir del fragmento
            dismiss();
        }
        String userId = currentUser.getUid();

        if (id_ejer == null || id_ejer.equals("")) {
            // Aquí le decimos que los datos que nos dio el usuario se pasan a string y sin espacios
            btn_agregar.setOnClickListener(view -> {
                String name_pet = spinnerNombre.getSelectedItem().toString();
                String color_pet = repe.getText().toString().trim();
                String weight_box = weight.getText().toString().trim();
                String mod_pet = spinnerModalidad.getSelectedItem().toString();

                // Si algún campo está vacío se pide que ingrese los datos, si no, se hace un post
                if (name_pet.isEmpty() && color_pet.isEmpty() && weight_box.isEmpty() && mod_pet.isEmpty()) {
                    Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                } else {
                    postBox(name_pet, color_pet, weight_box, mod_pet, userId);
                }
            });
        } else {
            getBox(userId);
            //Bloquea el spinner (name_box)
            spinnerNombre.setEnabled(false);
            btn_agregar.setText("Actualizar");
            // Aquí le decimos que los datos que nos dio el usuario se pasan a string y sin espacios
            btn_agregar.setOnClickListener(view -> {
                String name_box = spinnerNombre.getSelectedItem().toString();
                String repe_box = repe.getText().toString().trim();
                String weight_box = weight.getText().toString().trim();
                String mod_box = spinnerModalidad.getSelectedItem().toString();

                // Si algún campo está vacío se pide que ingrese los datos, si no, se hace un post
                if (name_box.isEmpty() && repe_box.isEmpty() && weight_box.isEmpty() && mod_box.isEmpty()) {
                    Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                } else {
                    updateBox(name_box, repe_box, weight_box, mod_box, userId);
                }
            });
        }
        return v;
    }

    //No incluir el campo "name" en la actualización
    private void updateBox(String name_box, String repe_box, String weight_box, String mod_box, String userId) {

        spinnerNombre.setEnabled(false);

        //Validar que los valores no estén vacíos
        if (repe_box.isEmpty() || weight_box.isEmpty()) {
            Toast.makeText(getContext(), "Por favor ingresa valores para Repeticiones y Carga", Toast.LENGTH_SHORT).show();
            return;
        }

        //Validar que los valores sean números (Igual estan definido en el xml que debe ser int)
        try {
            int reps = Integer.parseInt(repe_box);
            double weight = Double.parseDouble(weight_box);

            // Validar que los valores estén dentro del rango permitido
            if (reps <= 1000 && weight <= 1000) {
                Map<String, Object> map = new HashMap<>();

                map.put("repe", repe_box);
                map.put("weight", weight_box + " LB");
                map.put("mod", mod_box);
                map.put("userId", userId);

                // Actualizar el documento con el ID proporcionado
                mfirestore.collection(userId)
                        .document("Ejercicios")
                        .collection("Ejercicios")
                        .document(id_ejer)
                        .update(map)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getDialog()).dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al Actualizar", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "El número máximo permitido para Repeticiones y Carga es 1000", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor ingresa valores numéricos válidos para Repeticiones y Carga", Toast.LENGTH_SHORT).show();
        }
    }



    private void postBox(String name_box, String repe_box, String weight_box, String mod_box, String userId) {
        // Validar que los valores no estén vacíos
        if (repe_box.isEmpty() || weight_box.isEmpty()) {
            Toast.makeText(getContext(), "Por favor ingresa valores para Repeticiones y Carga", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que los valores sean números
        try {
            int reps = Integer.parseInt(repe_box);
            double weight = Double.parseDouble(weight_box);

            // Validar que los valores estén dentro del rango permitido
            if (reps <= 1000 && weight <= 1000) {
                createBoxCollection(name_box, repe_box, weight_box, mod_box, userId);
            } else {
                Toast.makeText(getContext(), "El número máximo permitido para Repeticiones y Carga es 1000", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor ingresa valores numéricos válidos para Repeticiones y Carga", Toast.LENGTH_SHORT).show();
        }
    }


    private void createBoxCollection(String name_box, String repe_box, String weight_box, String mod_box, String userId) {
        // Obtener la fecha actual
        Date currentDate = Calendar.getInstance().getTime();

        // Formatear la fecha si es necesario
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Consultar Firestore para verificar si ya existe un documento con el mismo nombre y la misma fecha
        mfirestore.collection(userId)
                .document("Ejercicios")
                .collection("Ejercicios")
                .whereEqualTo("userId", userId)
                .whereEqualTo("name", name_box)
                .whereEqualTo("date", formattedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Ya existe un documento con el mismo nombre y fecha, mostrar advertencia
                            Toast.makeText(getContext(), "Ya se ha registrado '" + name_box + "' para hoy", Toast.LENGTH_SHORT).show();
                        } else {
                            // No existe un documento con el mismo nombre y fecha, crear uno nuevo
                            Map<String, Object> map = new HashMap<>();
                            map.put("userId", userId);
                            map.put("name", name_box);
                            map.put("repe", repe_box);
                            map.put("weight", weight_box + " LB");
                            map.put("mod", mod_box);
                            map.put("date", formattedDate); // Agregar la fecha al mapa

                            // Guarda los datos en Firestore
                            mfirestore.collection(userId)
                                    .document("Ejercicios")
                                    .collection("Ejercicios")
                                    .document()
                                    .set(map)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                                        Objects.requireNonNull(getDialog()).dismiss();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al ingresar", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al consultar Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void getBox(String userId) { // Agregar userId como parámetro
        mfirestore.collection(userId).document("Ejercicios").collection("Ejercicios").document(id_ejer).get().addOnSuccessListener(documentSnapshot -> {
            String nameBox = documentSnapshot.getString("name");
            String weightbox = documentSnapshot.getString("weight");
            String repeBox = documentSnapshot.getString("repe");
            String modBox = documentSnapshot.getString("mod");

            //Elimina "LB" del peso
            if (weightbox != null) {
                weightbox = weightbox.replaceAll(" LB", "");
            }

            setSpinnerSelection(spinnerNombre, nameBox);
            weight.setText(weightbox);
            repe.setText(repeBox);
            setSpinnerSelection(spinnerModalidad, modBox);

        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).toString().equals(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }
}
