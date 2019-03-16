package com.cosmicode.roomie.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ChooseLocationActivity;
import com.cosmicode.roomie.MainActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.AddressService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.service.UploadPictureService;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Optional;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;


import java.math.BigDecimal;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MainEditProfileFragment extends Fragment implements UploadPictureService.OnUploadPictureListener, OnMapReadyCallback, AddressService.OnGetAdrressByIdListener, RoomieService.OnGetCurrentRoomieListener, Validator.ValidationListener {

    private static final String ROOMIE_KEY = "current_roomie";
    private Roomie currentRoomie;
    private OnFragmentInteractionListener mListener;
    private ImageView pfp;

    @Length(min = 4, max = 25)
    private EditText phone;

    @Length(min = 4, max = 750)
    private EditText bio;

    private ImageButton editButton, geoButton;
    private Button saveButton;
    private UploadPictureService uploadPictureService;
    private RoomieService roomieService;
    private Address address;
    AddressService addressService;
    public static final String CHOOSE_LOCATION_ADDRESS = "Address";
    public static final int REQUEST_MAP_CODE = 1;
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;
    private static final int LOCATION_PERMISSION = 1;
    private final String TAG = "Edit profile";
    private FusedLocationProviderClient fusedLocationClient;
    private Validator validator;


    public MainEditProfileFragment() {
        // Required empty public constructor
    }

    public static MainEditProfileFragment newInstance(Roomie roomie) {
        MainEditProfileFragment fragment = new MainEditProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROOMIE_KEY, roomie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            validator = new Validator(this);
            validator.setValidationListener(this);
            currentRoomie = getArguments().getParcelable(ROOMIE_KEY);
            uploadPictureService = new UploadPictureService(getContext(), this);
            addressService = new AddressService(getContext(), this);
            roomieService = new RoomieService(getContext(), this);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            createLocationRequest();
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
                .findFragmentById(R.id.map);
        pfp = getView().findViewById(R.id.profile_image);
        editButton = getView().findViewById(R.id.edit_picture_button);
        editButton.setOnClickListener(this::onClickEditPhoto);
        phone = getView().findViewById(R.id.phone_input);
        bio = getView().findViewById(R.id.bio_input);
        saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(this::onClickSave);
        geoButton = getView().findViewById(R.id.geo_button);
        geoButton.setOnClickListener(this::onClickGeo);
        addressService.getAddresById(currentRoomie.getAddressId());
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
        validator.validate();
    }

    public void cropImage(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMinCropResultSize(200, 200)
                .setMaxCropResultSize(2000, 2000)
                .setBorderLineColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .start(getContext(), this);
    }

    public void onClickGeo(View view) {


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double[] coordinates = {location.getLatitude(), location.getLongitude()};
                                Intent intent = new Intent(getContext(), ChooseLocationActivity.class);
                                intent.putExtra(CHOOSE_LOCATION_ADDRESS, coordinates);
                                startActivityForResult(intent, REQUEST_MAP_CODE);
                            }
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

                    Log.e(TAG, result.getError().toString());

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
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
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        Log.e(TAG, error);
    }

    @Override
    public void onGetAddressByIdSuccess(Address address) {
        this.address = address;
        mapFragment.getMapAsync(this);
        fillEditInfo();
    }

    @Override
    public void onGetAddressByIdError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        Log.e(TAG, error);
    }

    @Override
    public void onUpdateSuccess(Address address) {
        this.address = address;
        Toast toast = Toast.makeText(getContext(), R.string.update_success, Toast.LENGTH_SHORT);
        View view = toast.getView();

        view.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.toast_success), PorterDuff.Mode.SRC_IN);
        toast.show();
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {

    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        Log.e(TAG, error);
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
        gMap.addMarker(new MarkerOptions().position(location));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        gMap.animateCamera(CameraUpdateFactory.zoomIn());
        gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });
        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    @Override
    public void onValidationSucceeded() {
        currentRoomie.setPhone(phone.getText().toString());
        currentRoomie.setBiography(bio.getText().toString());
        roomieService.updateRoomie(currentRoomie);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

}
