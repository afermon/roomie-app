package com.cosmicode.roomie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.AddressService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.service.UploadPictureService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.math.BigDecimal;

import static android.app.Activity.RESULT_OK;


public class EditProfile extends Fragment implements UploadPictureService.OnUploadPictureListener, OnMapReadyCallback, AddressService.OnGetAdrressByIdListener, RoomieService.OnGetCurrentRoomieListener {

    private static final String ROOMIE_KEY = "current_roomie";
    private Roomie currentRoomie;
    private OnFragmentInteractionListener mListener;
    private ImageView pfp;
    private ImageButton editButton, geoButton;
    private TextInputEditText phone, bio;
    private TextView phoneError, bioError;
    private Button saveButton;
    private UploadPictureService uploadPictureService;
    private RoomieService roomieService;
    private Address address;
    private final int LOCATION_PERMISSION = 1;
    AddressService addressService;
    public static final String CHOOSE_LOCATION_ADDRESS = "Address";
    public static final int REQUEST_MAP_CODE = 1;
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;


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
            roomieService = new RoomieService(getContext(), this);
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
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        pfp = getView().findViewById(R.id.profile_image);
        editButton = getView().findViewById(R.id.edit_picture_button);
        editButton.setOnClickListener(this::onClickEditPhoto);
        phone = getView().findViewById(R.id.phone_text);
        bio = getView().findViewById(R.id.bio_text);
        phoneError = getView().findViewById(R.id.error_phone);
        bioError = getView().findViewById(R.id.bio_error);
        saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(this::onClickSave);
        geoButton = getView().findViewById(R.id.geo_button);
        geoButton.setOnClickListener(this::onClickGeo);
        addressService.getAddresById(currentRoomie.getId());
    }

    public void fillEditInfo() {
        Glide.with(getActivity().getApplicationContext()).load(currentRoomie.getPicture()).into(pfp);
        phone.setText(currentRoomie.getPhone());
        bio.setText(currentRoomie.getBiography());
    }

    public void onClickEditPhoto(View view) {

        PickImageDialog.build(new PickSetup())
                .show(getActivity())
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        cropImage(r.getUri());
                    }
                });
    }


    public void onClickSave(View view) {
        phoneError.setVisibility(View.INVISIBLE);
        bioError.setVisibility(View.INVISIBLE);
        if (validatePhone(phone.getText().toString()) && validateBio(bio.getText().toString())) {
            currentRoomie.setPhone(phone.getText().toString());
            currentRoomie.setBiography(bio.getText().toString());
            roomieService.updateRoomie(currentRoomie);
        }
    }

    public void cropImage(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMinCropResultSize(200, 200)
                .setMaxCropResultSize(1000,1000)
                .setBorderLineColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .start(getContext(),this);
    }

    public boolean validateBio(String bioText) {

        boolean isValid = true;

        if (bioText.length() > 750) {
            phoneError.setText("The text is too long");
            phoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (bioText.length() < 4 && bioText.length() > 0) {
            phoneError.setText("The text is too short");
            phoneError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

    public boolean validatePhone(String phoneText) {
        boolean isValid = true;

        if (phoneText.length() > 25) {
            phoneError.setText("The number is too long");
            phoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (phoneText.length() < 4 && phoneText.length() > 0) {
            phoneError.setText("The number is too short");
            phoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (phoneText.length() == 0) {
            phoneError.setText("The number can't be empty");
            phoneError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }


    public void onClickGeo(View view) {
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(getContext());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        } else {
            client.getLastLocation()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            double[] coordinates = {task.getResult().getLatitude(), task.getResult().getLongitude()};
                            Intent intent = new Intent(getContext(), ChooseLocationActivity.class);
                            intent.putExtra(CHOOSE_LOCATION_ADDRESS, coordinates);
                            startActivityForResult(intent, REQUEST_MAP_CODE);
                        }
                    });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_MAP_CODE == requestCode) {
            if (RESULT_OK == resultCode) {
                address.setLatitude(BigDecimal.valueOf(data.getDoubleArrayExtra("Address")[0]));
                address.setLongitude(BigDecimal.valueOf(data.getDoubleArrayExtra("Address")[1]));
                mapFragment.getMapAsync(this);
            }
        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    pfp.setImageURI(result.getUri());
                    uploadPictureService.uploadFile(result.getUri(), currentRoomie.getId(), UploadPictureService.PictureType.PROFILE);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Log.e("Edit profile", result.getError().toString());

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
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
        mapFragment.getMapAsync(this);
        fillEditInfo();
    }

    @Override
    public void onGetAddressByIdError(String error) {
        Log.e("Edit Profile", error);
    }

    @Override
    public void onUpdateSuccess(Address address) {
        this.address = address;
        Toast.makeText(getContext(), "Update success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {

    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        Log.e("Edit profile", error);
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {
        currentRoomie = roomie;
        addressService.updateAddress(address);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        LatLng location = new LatLng(address.getLatitude().doubleValue(), address.getLongitude().doubleValue());
        gMap.addMarker(new MarkerOptions().position(location).title("Your location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        gMap.animateCamera(CameraUpdateFactory.zoomIn());
        gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
