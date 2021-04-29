package com.example.gcpapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.gcpapp.R;
import com.example.gcpapp.activity.AccountActivity;
import com.example.gcpapp.tutorials.TutorialActivity;

public class HomeFragment extends Fragment {

    private CardView cardTutorialView;
    private CardView cardPersonalDataView;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        cardTutorialView = root.findViewById(R.id.cardTutorial);
        cardPersonalDataView = root.findViewById(R.id.cardPersonalData);
        addListeners();
        return root;
    }

    private void addListeners() {
        cardTutorialView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TutorialActivity.class));
            }
        });

        cardPersonalDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountActivity.class));
            }
        });
    }
}
