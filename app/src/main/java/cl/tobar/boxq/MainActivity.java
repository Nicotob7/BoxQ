package cl.tobar.boxq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import cl.tobar.boxq.adapter.Adapter;
import cl.tobar.boxq.model.Box;

public class MainActivity extends AppCompatActivity {

    Button btn_add, btn_add_frag, btn_exit;
    RecyclerView mRecycler;
    Adapter mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        //Esto de la collection se ve en Firebase (Tengo "Box" sin usar)
        Query query = mFirestore.collection("Pet");

        FirestoreRecyclerOptions<Box> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Box>().setQuery(query, Box.class).build();

        mAdapter = new Adapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        mRecycler.setAdapter(mAdapter);

        btn_add = findViewById(R.id.btn_add);
        btn_add_frag = findViewById(R.id.btn_add_frag);
        btn_exit = findViewById(R.id.btn_close);

        //Cuando se hace click se abre la nueva actividad
        btn_add.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Create.class)));

        btn_add_frag.setOnClickListener(view -> {
            CreateFragment fm = new CreateFragment();
            fm.show(getSupportFragmentManager(),"Navegar a fragment");
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        Log.d("MainActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
        Log.d("MainActivity", "onStop");
    }
}