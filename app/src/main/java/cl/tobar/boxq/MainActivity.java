package cl.tobar.boxq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

import cl.tobar.boxq.adapter.Adapter;
import cl.tobar.boxq.model.Box;

public class MainActivity extends AppCompatActivity {

    Button btn_add, btn_add_frag, btn_exit;
    ImageButton btn_back, btn_next;
    RecyclerView mRecycler;
    Adapter mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    Query query;
    int contador = 0;

    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Agregar ejercicio");


        dateTextView = findViewById(R.id.dia);
        updateDateTextView();

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setUpRecyclerView();

        //Button malo
        btn_add = findViewById(R.id.btn_add);
        btn_add_frag = findViewById(R.id.btn_add_frag);
        btn_exit = findViewById(R.id.btn_close);

        btn_back = findViewById(R.id.back);
        btn_next = findViewById(R.id.next);

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

        //Button para ver registro en relacion a la fecha con maximo de 7 dias
        btn_back.setOnClickListener(view -> {
            if (contador > -6) {
                contador--;
                adjustDate(contador);
                Log.d("Contador", "Contador actual: " + contador);
            }
        });

        //Button para ver registro, Contador limitado a 0 para actualizarlo al dia
        btn_next.setOnClickListener(view -> {
            if (contador < 0) { //Contador con limite de 7 dias
                contador++;
                adjustDate(contador);
                Log.d("Contador", "Contador actual: " + contador);
            }
        });
    }

    private void adjustDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        updateDateTextView(calendar);
    }

    private void updateDateTextView() {
        updateDateTextView(Calendar.getInstance());
    }

    //Actualiza el TextView con el día de la semana, el día del mes y el nombre del mes
    @SuppressLint("SetTextI18n")
    private void updateDateTextView(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayName = getDayName(dayOfWeek);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String monthName = getMonthName(calendar.get(Calendar.MONTH));

        //Establece el texto en el TextView (dia)
        dateTextView.setText(dayName + ", " + dayOfMonth + " de " + monthName);
    }

    //Método para obtener el nombre del mes a partir del número de mes
    private String getMonthName(int month) {
        String[] monthNames = new String[]{"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return monthNames[month];
    }

    private String getDayName(int dayOfWeek) {
        String[] dayNames = new String[]{"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        return dayNames[dayOfWeek - 1];
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifica si mAdapter no es nulo antes de llamar a startListening()
        if (mAdapter != null) {
            mAdapter.startListening();
        }
        Log.d("MainActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Verifica si mAdapter no es nulo antes de llamar a stopListening()
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        Log.d("MainActivity", "onStop");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(null);
        //Query query = mFirestore.collection("pet").whereEqualTo("id_user", mAuth.getCurrentUser().getUid());
        query = mFirestore.collection("Pet");
        FirestoreRecyclerOptions<Box> firestoreRecyclerOptions =
               new FirestoreRecyclerOptions.Builder<Box>()
                        .setQuery(query, Box.class)
                        .build();


        mAdapter = new Adapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }
}