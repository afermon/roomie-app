package com.cosmicode.roomie.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.service.AppointmentService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsListFragment extends Fragment {
    private static final String TAG = "AppointmentsListFragment";
    private AppointmentService appointmentService;

    public AppointmentsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_appointments_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
