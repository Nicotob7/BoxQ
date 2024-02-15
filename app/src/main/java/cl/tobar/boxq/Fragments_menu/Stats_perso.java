package cl.tobar.boxq.Fragments_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Objects;
import cl.tobar.boxq.R;
import cl.tobar.boxq.adapter.Adapter;
import cl.tobar.boxq.model.Box;

public class Stats_perso extends Fragment {

    private String names;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private Adapter adapter;

    public Stats_perso(String names) {
        this.names = names;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_perso, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void setupAdapter() {
        CollectionReference exercisesRef = firestore.collection(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .document("Ejercicios")
                .collection("Ejercicios");

        Query query = exercisesRef.whereEqualTo("name", names).orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Box> options = new FirestoreRecyclerOptions.Builder<Box>()
                .setQuery(query, Box.class)
                .build();

        adapter = new Adapter(options, getActivity(), getParentFragmentManager(), mAuth.getCurrentUser().getUid());
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}