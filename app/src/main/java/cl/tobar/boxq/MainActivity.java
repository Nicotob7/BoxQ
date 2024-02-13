package cl.tobar.boxq;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cl.tobar.boxq.Fragments_menu.HomeFragment;
import cl.tobar.boxq.Fragments_menu.LibraryFragment;
import cl.tobar.boxq.Fragments_menu.ShortsFragment;
import cl.tobar.boxq.Fragments_menu.SubscriptionFragment;
import cl.tobar.boxq.adapter.Adapter;
import cl.tobar.boxq.databinding.ActivityMainBinding;
import cl.tobar.boxq.model.Box;

public class MainActivity extends AppCompatActivity {

    Button btn_add, btn_exit;
    FloatingActionButton btn_add_frag;
    RecyclerView mRecycler;
    Adapter mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    Query query;

    TextView date, name_user;
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = getSupportFragmentManager().findFragmentByTag("HomeFragment");
                    if (fragment == null) {
                        fragment = new HomeFragment();
                    }
                    break;
                case R.id.profile:
                    fragment = getSupportFragmentManager().findFragmentByTag("ShortsFragment");
                    if (fragment == null) {
                        fragment = new ShortsFragment();
                    }
                    break;
                case R.id.stats:
                    fragment = getSupportFragmentManager().findFragmentByTag("SubscriptionFragment");
                    if (fragment == null) {
                        fragment = new SubscriptionFragment();
                    }
                    break;
                case R.id.config:
                    fragment = getSupportFragmentManager().findFragmentByTag("LibraryFragment");
                    if (fragment == null) {
                        fragment = new LibraryFragment();
                    }
                    break;
            }

            if (fragment != null) {
                replaceFragment(fragment);
            }
            return true; // Devuelve true para indicar que se ha manejado la selección del elemento
        });


        date = findViewById(R.id.dia);
        name_user = findViewById(R.id.name_user);
        updateDateTextView();

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setUpRecyclerView();

        //Button malo
        btn_add = findViewById(R.id.btn_add);
        btn_add_frag = findViewById(R.id.btn_add_frag);
        btn_exit = findViewById(R.id.btn_close);

        //Cuando se hace click se abre la nueva actividad
        btn_add.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Create.class)));

        //Button para agregar con CreateFragment
        btn_add_frag.setOnClickListener(view -> {
            CreateFragment fm = new CreateFragment();
            fm.show(getSupportFragmentManager(), "Navegar a fragment");
        });

        //Cerrar sesion manda al Login
        btn_exit.setOnClickListener(view -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        // Escuchar cambios en el nombre del usuario
        listenToUserName();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

    private void updateDateTextView() {
        updateDateTextView(Calendar.getInstance());
    }

    //Actualiza el TextView con el día de la semana, el día del mes y el nombre del mes
    @SuppressLint("SetTextI18n")
    private void updateDateTextView(Calendar calendar) {
        //Se obtenemos el día de la semana actual y el día del mes
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        //Calcula el número del día lunes y el día domingo (Para decir semana desde tanto tanto)
        int mondayDayOfMonth = dayOfMonth - (dayOfWeek - Calendar.MONDAY);
        int sundayDayOfMonth = dayOfMonth + (Calendar.SUNDAY - dayOfWeek + (dayOfWeek == Calendar.MONDAY ? 0 : 7));



        //Nombre del mes
        String monthName = getMonthName(calendar.get(Calendar.MONTH));

        //Texto en el TextView (dia)
        date.setText("Semana del " + mondayDayOfMonth + " - al " + sundayDayOfMonth + " de " + monthName);
    }

    //Método para obtener el nombre del mes (De seguro hay algo mas facil)
    private String getMonthName(int month) {
        String[] monthNames = new String[]{"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return monthNames[month];
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Verifica si mAdapter no es nulo antes de llamar a startListening()
        if (mAdapter != null) {
            mAdapter.startListening();
        }
        Log.d("MainActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Verifica si mAdapter no es nulo antes de llamar a stopListening()
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        Log.d("MainActivity", "onStop");
    }

    private void setUpRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(null);

        // Obtener la fecha actual
        Date currentDate = Calendar.getInstance().getTime();

        // Formatear la fecha si es necesario
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // ID del usuario actualmente autenticado en la APP
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Filtra la colección del usuario actual (por su id), excluye el documento "profile"
        // y solo trae los documentos con la fecha actual
        query = mFirestore.collection(userId)
                .document("Ejercicios")
                .collection("Ejercicios")
                .whereNotEqualTo(FieldPath.documentId(), "profile")
                .whereEqualTo("date", formattedDate);

        FirestoreRecyclerOptions<Box> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Box>()
                        .setQuery(query, Box.class)
                        .build();

        mAdapter = new Adapter(firestoreRecyclerOptions, this, getSupportFragmentManager(), userId);
        mRecycler.setAdapter(mAdapter);
    }


    // Escuchar cambios en el nombre del usuario
    private void listenToUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mFirestore.collection("user").document(userId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e("MainActivity", "Listen failed.", e);
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                name_user.setText(name); // Actualizar el nombre en el TextView
                            }
                        }
                    });
        }
    }
}