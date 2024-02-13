package cl.tobar.boxq;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cl.tobar.boxq.Fragments_menu.Config;
import cl.tobar.boxq.Fragments_menu.Home;
import cl.tobar.boxq.Fragments_menu.Profile;
import cl.tobar.boxq.Fragments_menu.Stats;
import cl.tobar.boxq.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn_add_frag;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Home());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = getSupportFragmentManager().findFragmentByTag("Home");
                    if (fragment == null) {
                        fragment = new Home();
                    }
                    break;
                case R.id.profile:
                    fragment = getSupportFragmentManager().findFragmentByTag("Profile");
                    if (fragment == null) {
                        fragment = new Profile();
                    }
                    break;
                case R.id.stats:
                    fragment = getSupportFragmentManager().findFragmentByTag("Stats");
                    if (fragment == null) {
                        fragment = new Stats();
                    }
                    break;
                case R.id.config:
                    fragment = getSupportFragmentManager().findFragmentByTag("Config");
                    if (fragment == null) {
                        fragment = new Config();
                    }
                    break;
            }

            if (fragment != null) {
                replaceFragment(fragment);
            }
            return true; //Devuelve true para indicar que se ha manejado la selección del elemento
        });


        btn_add_frag = findViewById(R.id.btn_add_frag);

        //Button para agregar con CreateFragment
        btn_add_frag.setOnClickListener(view -> {
            Fragment fragment = new Home();
            replaceFragment(fragment);
            binding.bottomNavigationView.setSelectedItemId(R.id.home);
            CreateFragment fm = new CreateFragment();
            fm.show(getSupportFragmentManager(), "Navegar a fragment");
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Agregar una animación de desvanecimiento al entrar y salir del fragmento
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}