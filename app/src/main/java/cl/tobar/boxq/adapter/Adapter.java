package cl.tobar.boxq.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cl.tobar.boxq.Create;
import cl.tobar.boxq.CreateFragment;
import cl.tobar.boxq.R;
import cl.tobar.boxq.model.Box;

public class Adapter extends FirestoreRecyclerAdapter<Box, Adapter.ViewHolder>{

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;
    //Este es el fragment
    FragmentManager fm;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     */
    public Adapter(@NonNull FirestoreRecyclerOptions<Box> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }



    //Lee los datos en la base de datos
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Box Box) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.name.setText(Box.getName());
        viewHolder.weight.setText(Box.getWeight());
        viewHolder.repe.setText(Box.getRepe());
        viewHolder.mod.setText(Box.getMod());

        //Aqui se crea el metodo de editar con nueva layout (Desactivado para usar Fragment)
        viewHolder.btn_edit.setOnClickListener(v -> {
            Intent i1 = new Intent(activity, Create.class);
            i1.putExtra("id_ejer", id);
            //activity.startActivity(i);

            CreateFragment createFragment = new CreateFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id_ejer", id);
            createFragment.setArguments(bundle);
            createFragment.show(fm, "open fragment");
        });




        //Aqui se crea el metodo para eliminar
        viewHolder.btn_delete.setOnClickListener(v -> deleteBox(id));

    }

    //Aqui muestra el mensaje correspondiente, se compara el activity por si es null
    private void deleteBox(String id) {
        //Excepcion de errores
        mFirestore.collection("Pet").document(id).delete().addOnSuccessListener(unused -> {
            if (activity != null) {
            Toast.makeText(activity, "Eliminado Correctamente", Toast.LENGTH_SHORT).show();
        }}).addOnFailureListener(e -> Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }

    //Muestra los datos obtenidos en view_single.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_single, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, repe, weight, mod;

        ImageView btn_delete, btn_edit;

        //View pet single id
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nombre);
            repe = itemView.findViewById(R.id.repe);
            weight = itemView.findViewById(R.id.weight);
            mod = itemView.findViewById(R.id.modalidad);

            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editar);
        }
    }
}

