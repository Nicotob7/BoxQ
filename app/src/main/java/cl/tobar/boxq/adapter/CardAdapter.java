package cl.tobar.boxq.adapter;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cl.tobar.boxq.Fragments_menu.Stats_perso;
import cl.tobar.boxq.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private String[] names;
    private Map<String, String> dateMap; // Map para almacenar las fechas asociadas a los nombres
    private FirebaseFirestore firestore;

    public CardAdapter(String[] names) {
        this.names = names;
        this.dateMap = new HashMap<>();
        this.firestore = FirebaseFirestore.getInstance();
        // Llamar al método para cargar las fechas

        mAuth = FirebaseAuth.getInstance();
        loadDates();
    }

    // Método para cargar las fechas asociadas a los nombres
    private void loadDates() {
        // ID del usuario actualmente autenticado en la APP
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Obtener una referencia a la colección en Firestore donde se encuentran los datos
        CollectionReference collectionRef = firestore.collection(userId).document("Ejercicios").collection("Ejercicios");

        // Realizar la consulta para obtener todos los registros ordenados por fecha descendente
        collectionRef.orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Iterar sobre los documentos para obtener la fecha más reciente para cada nombre
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String name = document.getString("name");
                        String date = document.getString("date");
                        // Si el nombre aún no está en el mapa o la fecha es más reciente que la que ya está en el mapa
                        if (!dateMap.containsKey(name) || date.compareTo(dateMap.get(name)) > 0) {
                            dateMap.put(name, date);
                        }
                    }
                    notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error que ocurra al realizar la consulta
                    Log.e(TAG, "Error al obtener fechas: " + e.getMessage());
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = names[position];
        holder.textViewName.setText(name);
        // Establecer la fecha correspondiente al nombre
        if (dateMap.containsKey(name)) {
            holder.dateReg.setText(dateMap.get(name));
        } else {
            holder.dateReg.setText("Sin registro");
        }

        // Agregar un OnClickListener al botón btnProg
        holder.btnProg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el nombre del textViewName
                String name = holder.textViewName.getText().toString();
                // Navegar al fragmento Stats_perso cuando se hace clic en el botón
                FragmentManager fragmentManager = ((FragmentActivity) view.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Pasar el nombre al fragmento Stats_perso
                Stats_perso stats_perso = new Stats_perso(name);
                fragmentTransaction.replace(R.id.frame_layout, stats_perso);
                fragmentTransaction.addToBackStack(null); // Agregar a la pila de retroceso para poder volver atrás
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView dateReg;
        Button btnProg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            dateReg = itemView.findViewById(R.id.date_reg);
            btnProg = itemView.findViewById(R.id.btn_prog);
        }
    }
}

