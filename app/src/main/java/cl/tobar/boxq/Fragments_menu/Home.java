package cl.tobar.boxq.Fragments_menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

import cl.tobar.boxq.Create;
import cl.tobar.boxq.LoginActivity;
import cl.tobar.boxq.R;
import cl.tobar.boxq.adapter.Adapter;
import cl.tobar.boxq.databinding.ActivityMainBinding;
import cl.tobar.boxq.model.Box;

public class Home extends Fragment {


    private RecyclerView mRecycler;
    private Adapter mAdapter;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Query query;

    private TextView date, name_user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        date = view.findViewById(R.id.dia);
        name_user = view.findViewById(R.id.name_user);
        //setUpRecyclerView(view);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //Escuchar cambios en el nombre del usuario
        listenToUserName();
        setUpRecyclerView(view);
        updateDateTextView();
        listenToUserName();

        return view;
    }

    private void setUpRecyclerView(View rootView) {
        mRecycler = rootView.findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        //Obtener la fecha actual
        Date currentDate = Calendar.getInstance().getTime();

        //Formatear la fecha si es necesario
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // ID del usuario actualmente autenticado en la APP
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Filtra la colección del usuario actual (por su id), excluye el documento "profile"
        //y solo trae los documentos con la fecha actual
        query = mFirestore.collection(userId)
                .document("Ejercicios")
                .collection("Ejercicios")
                .whereNotEqualTo(FieldPath.documentId(), "profile")
                .whereEqualTo("date", formattedDate);

        FirestoreRecyclerOptions<Box> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Box>()
                        .setQuery(query, Box.class)
                        .build();

        mAdapter = new Adapter(firestoreRecyclerOptions, getActivity(), getChildFragmentManager(), userId);
        mRecycler.setAdapter(mAdapter);
    }

    private void updateDateTextView() {
        updateDateTextView(Calendar.getInstance());
    }

    //Actualiza el TextView con el día de la semana, el día del mes y el nombre del mes
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
    public void onStart() {
        super.onStart();
        //Verifica si mAdapter no es nulo antes de llamar a startListening()
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Verifica si mAdapter no es nulo antes de llamar a stopListening()
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    // Escuchar cambios en el nombre del usuario
    private void listenToUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mFirestore.collection("user").document(userId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
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