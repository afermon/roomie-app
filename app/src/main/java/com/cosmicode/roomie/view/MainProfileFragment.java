package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.Gender;
import com.cosmicode.roomie.service.AddressService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnGetUserByIdListener;
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
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainProfileFragment extends Fragment implements RoomieService.OnGetCurrentRoomieListener, OnGetUserByIdListener, OnMapReadyCallback, AddressService.OnGetAdrressByIdListener {

    private OnFragmentInteractionListener mListener;
    private AddressService addressService;
    private Roomie currentRoomie;
    private FlexboxLayout lifeStyleContainer;
    private TextView name, email, phone, genderAge, bio, noLife;
    private ImageView pfp;
    private SupportMapFragment mapFragment;
    private Address userAddress;
    private ProgressBar progress;
    private ScrollView scrollView;
    private RoomieService roomieService;
    private ImageButton settings;
    private final String TAG = "Profile";
    private final static String ROOMIE = "roomie";
    private Roomie roomie;
    private JhiAccount user;
    private TextView addressText;

    public MainProfileFragment() {
    }

    public static MainProfileFragment newInstance(Roomie roomie) {
        MainProfileFragment fragment = new MainProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROOMIE, roomie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.roomie = getArguments().getParcelable(ROOMIE);
            addressService = new AddressService(getContext(), this);
            roomieService = new RoomieService(getContext(), this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null, false);
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
        lifeStyleContainer = getView().findViewById(R.id.lifestyle_container);
        settings = getView().findViewById(R.id.settings_button);
        settings.setOnClickListener(this::openEdit);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        name = getView().findViewById(R.id.name_text);
        email = getView().findViewById(R.id.mail_text);
        pfp = getView().findViewById(R.id.profile_image);
        phone = getView().findViewById(R.id.phone_text);
        genderAge = getView().findViewById(R.id.gender_age_text);
        bio = getView().findViewById(R.id.bio_text);
        progress = getView().findViewById(R.id.progress);
        scrollView = getView().findViewById(R.id.profile_scroll);
        noLife = getView().findViewById(R.id.text_no_life);
        addressText = getView().findViewById(R.id.address_text);
        showProgress(true);

        mListener.getBaseActivity().getJhiUsers().findById(roomie.getUserId(), this);
    }

    public void openEdit(View view) {
        MainEditProfileFragment mainEditProfileFragment = MainEditProfileFragment.newInstance(roomie);
        openFragment(mainEditProfileFragment);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void fillProfileInfo() {
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        email.setText(getString(R.string.email_profile, user.getEmail()));
    }

    public void fillRoomieInfo() {

        if(!roomie.getId().equals(currentRoomie.getId())){
            settings.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);
            addressText.setVisibility(View.VISIBLE);
            if(userAddress.getCity().equals("Default")){
                addressText.setText(getString(R.string.no_address));
            }else{
                addressText.setText(String.format("%s, %s", userAddress.getState(), userAddress.getCity()));
            }
        }else {
            settings.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            addressText.setVisibility(View.GONE);
        }

        Glide.with(getActivity().getApplicationContext()).load(roomie.getPicture()).centerCrop().into(pfp);
        if (roomie.getPhone() == null) {
            phone.setVisibility(View.GONE);
        } else {
            phone.setText(getString(R.string.profile_phone, roomie.getPhone()));
            phone.setVisibility(View.VISIBLE);
        }
        genderAge.setText(getString(R.string.profile_gender_age, getEnumString(roomie.getGender()), calculateAge(roomie.getBirthDate(), new Date())));
        if (roomie.getBiography() == null) {
            bio.setText(R.string.no_bio);
        } else {
            bio.setText(roomie.getBiography());
        }
        fillLifeStyleInfo();
    }

    public void fillLifeStyleInfo() {
        List<RoomFeature> lifeStyles = roomie.getLifestyles();
        if (lifeStyles.isEmpty()) {
            noLife.setVisibility(View.VISIBLE);
        } else {
            Iterator iterator = lifeStyles.iterator();
            TextView tag;
            noLife.setVisibility(View.GONE);
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
        showProgress(false);
    }

    public String calculateAge(String birthDate, Date currentDate) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String[] dateSplit = birthDate.split("-");
        String concatDate = dateSplit[0] + dateSplit[1] + dateSplit[2];
        int d1 = Integer.parseInt(concatDate);
        int d2 = Integer.parseInt(formatter.format(currentDate));
        String longAge = Integer.toString(d2 - d1);
        return longAge.substring(0, longAge.length() - 4);
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
    public void onMapReady(GoogleMap map) {
        GoogleMap gMap = map;
        LatLng location = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

        if(userAddress.getCity().equals("Default")){
            gMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }else {
            gMap.addMarker(new MarkerOptions().position(location).title("Your location"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            gMap.animateCamera(CameraUpdateFactory.zoomIn());
            gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        }

    }

    @Override
    public void onGetAddressByIdSuccess(Address address) {
        this.userAddress = address;
        roomieService.getCurrentRoomie();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onGetAddressByIdError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        Log.e(TAG, error);
    }

    @Override
    public void onUpdateSuccess(Address address) {

    }

    @Override
    public void onGetUserSuccess(JhiAccount user) {
        this.user = user;
        addressService.getAddresById(roomie.getAddressId());
    }

    @Override
    public void onGetUserError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        this.currentRoomie = roomie;
        fillRoomieInfo();
        fillProfileInfo();
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        scrollView.setVisibility(((show) ? View.GONE : View.VISIBLE));

        scrollView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scrollView.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
        progress.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 1 : 0))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
                    }
                });
    }
}
