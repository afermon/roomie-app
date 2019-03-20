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
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.enumeration.RoomState;

public class ListingStepChooseType extends Fragment {

    @BindView(R.id.room_btn)
    TextView roommate;

    @BindView(R.id.rent_btn)
    TextView rent;

    private RoomCreate room;

    private OnFragmentInteractionListener mListener;

    public ListingStepChooseType() {
        // Required empty public constructor
    }


    public static ListingStepChooseType newInstance() {
        ListingStepChooseType fragment = new ListingStepChooseType();
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
        View view = inflater.inflate(R.layout.fragment_listing_step1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        room = new RoomCreate();
        room.setState(RoomState.SEARCH);
    }

    public void openFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
        transaction.replace(R.id.listing_container, ListingBasicInformation.newInstance(room) );
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
        room.setLookingForRoomie(false);
        activeButton(rent, roommate);
    }

    public void activeButton(TextView active, TextView inactive) {
        active.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
        active.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        inactiveButton(inactive);
    }

    public void inactiveButton(TextView inactive) {
        inactive.setBackgroundTintList(null);
        inactive.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        openFragment();
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
        BaseActivity getBaseActivity();
    }
}