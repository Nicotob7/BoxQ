package cl.tobar.boxq.Fragments_menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.tobar.boxq.R;
import cl.tobar.boxq.adapter.CardAdapter;


public class Stats extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String[] names = getResources().getStringArray(R.array.op_nombre);
        CardAdapter adapter = new CardAdapter(names);
        recyclerView.setAdapter(adapter);

        return view;
    }
}