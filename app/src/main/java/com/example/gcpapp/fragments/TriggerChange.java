package com.example.gcpapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.gcpapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TriggerChange extends Fragment {

    public TriggerChange() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trigger_change, container, false);
    }
}
