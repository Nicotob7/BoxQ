package cl.tobar.boxq.Fragments_menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import cl.tobar.boxq.LoginActivity;
import cl.tobar.boxq.R;




public class Config extends Fragment {

    private ImageView btn_exit;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflar el diseño del fragmento de configuración
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Buscar el ImageView btn_exit dentro del diseño del fragmento de configuración
        btn_exit = view.findViewById(R.id.btn_close);

        // Configurar el OnClickListener para el botón de salida
        btn_exit.setOnClickListener(v -> {
            mAuth.signOut();
            requireActivity().finish();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        });

        // Devolver la vista inflada del fragmento de configuración
        return view;
    }
}
