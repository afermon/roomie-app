package com.cosmicode.roomie;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.AddressService;
import com.cosmicode.roomie.service.UploadPictureService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.app.Activity.RESULT_OK;


public class EditProfile extends Fragment implements UploadPictureService.OnUploadPictureListener, AddressService.OnGetAdrressByIdListener {

    private static final String ROOMIE_KEY = "current_roomie";
    private Roomie currentRoomie;
    private OnFragmentInteractionListener mListener;
    private ImageView pfp;
    private ImageButton editButton;
    private TextInputEditText phone, city, state, bio;
    private TextView phoneError, bioError;
    private Button saveButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private UploadPictureService uploadPictureService;
    private Address address;
    private final int LOCATION_PERMISSION = 1;
    AddressService addressService;

    public EditProfile() {
        // Required empty public constructor
    }

    public static EditProfile newInstance(Roomie roomie) {
        EditProfile fragment = new EditProfile();
        Bundle args = new Bundle();
        args.putParcelable(ROOMIE_KEY, roomie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentRoomie = getArguments().getParcelable(ROOMIE_KEY);
            uploadPictureService = new UploadPictureService(getContext(), this);
            addressService = new AddressService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pfp = getView().findViewById(R.id.profile_image);
        editButton = getView().findViewById(R.id.edit_picture_button);
        editButton.setOnClickListener(this::onClickEditPhoto);
        phone = getView().findViewById(R.id.phone_text);
        city = getView().findViewById(R.id.city_text);
        state = getView().findViewById(R.id.state_text);
        bio = getView().findViewById(R.id.bio_text);
        phoneError = getView().findViewById(R.id.error_phone);
        bioError = getView().findViewById(R.id.bio_error);
        saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(this::onClickGeo);
        addressService.getAddresById(currentRoomie.getId());
    }

    public void fillEditInfo(){
        Glide.with(getActivity().getApplicationContext()).load(currentRoomie.getPicture()).centerCrop().into(pfp);
        phone.setText(currentRoomie.getPhone());
        city.setText(address.getCity());
        state.setText(address.getState());
        bio.setText(currentRoomie.getBiography());
    }

    public void onClickEditPhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    public void onClickSave(View view){

    }

    public void onClickGeo(View view){
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(getContext());
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }else{
            client.getLastLocation()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            city.setText(""+task.getResult().getLatitude());
                            state.setText(""+task.getResult().getLongitude());
                        }
                    });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pfp.setImageBitmap(imageBitmap);
            uploadPictureService.uploadFile(imageBitmap, currentRoomie.getId(), UploadPictureService.PictureType.PROFILE);
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
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
    public void onUploaddSuccess(String url) {
        currentRoomie.setPicture(url);
    }

    @Override
    public void onUploadError(String error) {
        Log.e("Edit Profile", error);
    }

    @Override
    public void onGetAddressByIdSuccess(Address address) {
        this.address = address;
        fillEditInfo();
    }

    @Override
    public void onGetAddressByIdError(String error) {
        Log.e("Edit Profile", error);
    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
