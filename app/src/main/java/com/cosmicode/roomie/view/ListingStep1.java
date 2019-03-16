package com.cosmicode.roomie.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.enumeration.RoomState;

public class ListingStep1 extends Fragment {

    @BindView(R.id.room_btn)
    Button roommate;

    @BindView(R.id.rent_btn)
    Button rent;

    @BindView(R.id.next_step)
    Button next;

    private Room room;

    private OnFragmentInteractionListener mListener;

    public ListingStep1() {
        // Required empty public constructor
    }


    public static ListingStep1 newInstance() {
        ListingStep1 fragment = new ListingStep1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listing_step1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        room = new Room();
        room.setState(RoomState.SEARCH);
        ButterKnife.bind(getActivity());
        next.setEnabled(false);
    }

    @OnClick(R.id.next_step)
    public void openFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
        transaction.replace(R.id.listing_container, ListingStep2.newInstance(room) );
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.room_btn)
    public void onClickRoom(View view){
        room.setLookingForRoomie(true);
        activeButton(roommate, rent);
    }

    @OnClick(R.id.rent_btn)
    public void onClickRent(View view){
        room.setLookingForRoomie(true);
        activeButton(rent, roommate);
    }

    public void activeButton(Button active, Button inactive) {
        next.setEnabled(true);
        active.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
        active.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        inactiveButton(inactive);
    }

    public void inactiveButton(Button inactive) {
        inactive.setBackgroundTintList(null);
        inactive.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
