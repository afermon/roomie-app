package com.cosmicode.roomie;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.RoomieUser;
import com.cosmicode.roomie.domain.enumeration.Gender;
import com.cosmicode.roomie.service.RoomieService;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ProfileFragment extends Fragment implements RoomieService.OnGetCurrentRoomieListener, OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private GoogleMap gMap;
    private Roomie currentRoomie;
    private RoomieService roomieService;
    private FlexboxLayout lifeStyleContainer;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomieService = new RoomieService(getContext(), this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, null, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mListener != null) {
            mListener.getBaseActivity().getJhiUsers().getLogedUser(user -> fillProfileInfo(user));
            lifeStyleContainer = getView().findViewById(R.id.lifestyle_container);
            FloatingActionButton fab = getView().findViewById(R.id.floatingActionButton);
            fab.setOnClickListener(this::openEdit);
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(this);
            roomieService.getCurrentRoomie();
        }
    }

    public void openEdit(View view) {
        EditProfile editProfile = EditProfile.newInstance("", "");
        openFragment(editProfile);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void fillProfileInfo(RoomieUser roomieUser) {
        TextView name = getView().findViewById(R.id.nameTxt);
        name.setText(roomieUser.getFullName());

        TextView email = getView().findViewById(R.id.mailTxt);
        email.setText(roomieUser.getEmail());

    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        this.currentRoomie = roomie;
        fillRoomieInfo();
    }

    public void fillRoomieInfo() {
        ImageView pfp = getView().findViewById(R.id.profileImg);
        Glide.with(getActivity().getApplicationContext()).load(currentRoomie.getPicture()).centerCrop().into(pfp);

        TextView phone = getView().findViewById(R.id.phoneTxt);
        phone.setText(getString(R.string.profile_phone, currentRoomie.getPhone()));

        TextView age = getView().findViewById(R.id.ageTxt);
        age.setText(getString(R.string.profile_age, calculateAge(currentRoomie.getBirthDate(), new Date())));

        TextView gender = getView().findViewById(R.id.genderTxt);
        gender.setText(getString(R.string.profile_gender, getEnumString(currentRoomie.getGender())));

        TextView bio = getView().findViewById(R.id.bioTxt);
        bio.setText(currentRoomie.getBiography());

        fillLifeStyleInfo();
    }

    public void fillLifeStyleInfo() {
        List<RoomFeature> lifeStyles = currentRoomie.getLifestyles();
        Iterator iterator = lifeStyles.iterator();
        TextView tag;
        while (iterator.hasNext()) {
            RoomFeature element = (RoomFeature) iterator.next();
            tag = new TextView(new ContextThemeWrapper(getContext(), R.style.RoomieTags), null, 0);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams
                    (FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            tag.setText(element.getName());
            tag.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary));
            tag.setLayoutParams(params);
            lifeStyleContainer.addView(tag);
        }
    }

    public String calculateAge(Date birthDate, Date currentDate) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(formatter.format(birthDate));
        int d2 = Integer.parseInt(formatter.format(currentDate));
        int age = (d2 - d1) / 10000;
        return Integer.toString(age);
    }

    public String getEnumString(Gender gender) {
        String stringId = "";
        switch (gender) {
            case MALE:
                stringId = getResources().getString(R.string.enum_male);
                break;
            case FEMALE:
                stringId = getResources().getString(R.string.enum_female);
                break;
            case OTHER:
                stringId = getResources().getString(R.string.enum_other);
                break;
        }
        return stringId;
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        Log.e("Profile", error);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        LatLng location = new LatLng(9.9878557 ,-84.0977742);
        gMap.addMarker(new MarkerOptions().position(location).title("Your location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
