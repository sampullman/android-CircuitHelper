package com.threeDBJ.circuithelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

import net.simonvt.numberpicker.NumberPicker;

public class CapacitorFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.capacitor, container, false);
        return v;
    }
}