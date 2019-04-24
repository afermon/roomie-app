package com.cosmicode.roomie.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;

public class MainEditRoom extends Fragment {

    private final static String ROOM = "room";
    private Room room;
    private RoomCreate roomCreate;

    private OnFragmentInteractionListener mListener;

    public MainEditRoom() {
    }

    public static MainEditRoom newInstance(Room room) {
        MainEditRoom fragment = new MainEditRoom();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = getArguments().getParcelable(ROOM);
            roomCreate = new RoomCreate();
            roomCreate.setId(room.getId());
            roomCreate.setState(room.getState());
            roomCreate.setCreated(room.getCreated());
            roomCreate.setPublished(room.getPublished());
            roomCreate.setTitle(room.getTitle());
            roomCreate.setDescription(room.getDescription());
            roomCreate.setRooms(room.getRooms());
            roomCreate.setRoomType(room.getRoomType());
            roomCreate.setApoinmentsNotes(room.getApoinmentsNotes());
            roomCreate.setLookingForRoomie(room.getLookingForRoomie());
            roomCreate.setAvailableFrom(room.getAvailableFrom());
            roomCreate.setPremium(room.getPremium());
            roomCreate.setFeatures(room.getFeatures());
            roomCreate.setOwnerId(room.getOwnerId());
            roomCreate.setAddressId(room.getAddressId());
            roomCreate.setPriceId(room.getPrice().getId());
            roomCreate.setPictures(room.getPictures());
            roomCreate.setExpenses(room.getExpenses());
            roomCreate.setRoomies(room.getRoomies());
            roomCreate.setAddress(room.getAddress());
            roomCreate.setMonthly(room.getPrice());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_edit_room, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.basic_text)
    public void editBasic(View view){
        mListener.openFragment(ListingBasicInformation.newInstance(roomCreate, true), "right");
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
        void openFragment(Fragment fragment, String start);
    }

    @OnClick(R.id.cost_text)
    public void editCost(View view){
        mListener.openFragment(ListingCost.newInstance(roomCreate, true), "right");
    }

    @OnClick(R.id.location_text)
    public void editLocation(View view){
        mListener.openFragment(ListingChooseLocation.newInstance(roomCreate, true), "right");
    }

    @OnClick(R.id.state_text)
    public void editState(View view){
        mListener.openFragment(RoomStateFragment.newInstance(roomCreate), "right");
    }

    @OnClick(R.id.back_button)
    public void goBack(View view){
        getFragmentManager().popBackStack();
    }
}
