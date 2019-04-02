package com.cosmicode.roomie.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.cosmicode.roomie.AddLifestylesActivity;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ChooseLocationActivity;
import com.cosmicode.roomie.MainActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.AddressService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.service.UploadPictureService;
import com.google.android.flexbox.FlexboxLayout;
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


import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MainEditProfileFragment extends Fragment implements BSImagePicker.OnSingleImageSelectedListener, UploadPictureService.OnUploadPictureListener, OnMapReadyCallback, AddressService.OnGetAdrressByIdListener, RoomieService.OnGetCurrentRoomieListener, Validator.ValidationListener {

    private static final String ROOMIE_KEY = "current_roomie";
    private Roomie currentRoomie;
    private OnFragmentInteractionListener mListener;
    private ImageView pfp;

    @Length(min = 4, max = 25)
    private EditText phone;

    @Length(min = 4, max = 750)
    private EditText bio;

    private FlexboxLayout lifestyleContainer;
    private TextView noLifeStyles;
    private ProgressBar progress;
    private boolean locationChanged;
    private ScrollView scrollView;
    private ImageButton editButton, geoButton;
    private Button saveButton;
    private UploadPictureService uploadPictureService;
    private RoomieService roomieService;
    private Address address;
    private ImageButton back;
    private AddressService addressService;
    private static final String CHOOSE_LOCATION_ADDRESS = "Address";
    private static final int REQUEST_MAP_CODE = 1;
    private static final int REQUEST_LIFESTYLES_CODE = 2;
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;
    private static final int LOCATION_PERMISSION = 1;
    private final String TAG = "Edit profile";
    private FusedLocationProviderClient fusedLocationClient;
    private Validator validator;
    private BSImagePicker singleSelectionPicker;
    private static final String LIFESTYLES = "lifestyles";


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
            singleSelectionPicker = new BSImagePicker.Builder("com.yourdomain.yourpackage.fileprovider")
                    .setMaximumDisplayingImages(24) //Default: Integer.MAX_VALUE. Don't worry about performance :)
                    .setSpanCount(3) //Default: 3. This is the number of columns
                    .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                    .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                    .hideCameraTile() //Default: show. Set this if you don't want user to take photo.
                    .build();
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
        back = getView().findViewById(R.id.back_button);
        back.setOnClickListener(this::goBack);
        progress = getView().findViewById(R.id.progress);
        scrollView = getView().findViewById(R.id.edit_scroll);
        lifestyleContainer = getView().findViewById(R.id.lifestyle_container);
        noLifeStyles = getView().findViewById(R.id.no_lifestyles);
        fillLifeStyles();
        ImageButton lf = getView().findViewById(R.id.life);
        lf.setOnClickListener(l -> {
            Intent intent = new Intent(getContext(), AddLifestylesActivity.class);
            intent.putExtra(LIFESTYLES, currentRoomie);
            startActivityForResult(intent, REQUEST_LIFESTYLES_CODE);
        });
        showProgress(true);
        addressService.getAddresById(currentRoomie.getAddressId());
    }

    private void goBack(View view) {
        getFragmentManager().popBackStack();
    }

    public void fillEditInfo() {
        Glide.with(getActivity().getApplicationContext()).load(currentRoomie.getPicture()).into(pfp);
        phone.setText(currentRoomie.getPhone());
        bio.setText(currentRoomie.getBiography());
        showProgress(false);
    }

    public void onClickEditPhoto(View view) {
        singleSelectionPicker.show(getChildFragmentManager(), "picker");
    }


    public void onClickSave(View view) {
        boolean isPhoneValid, isBioValid;

        isPhoneValid = validatePhone();
        isBioValid = validateBio();
        if (isBioValid && isPhoneValid) {
            if (checkFieldEmpty(phone)) {
                currentRoomie.setPhone(null);
            } else {
                currentRoomie.setPhone(phone.getText().toString());
            }

            if (checkFieldEmpty(bio)) {
                currentRoomie.setBiography(null);
            } else {
                currentRoomie.setBiography(bio.getText().toString());

            }
            roomieService.updateRoomie(currentRoomie);
            showProgress(true);
        }
    }

    public boolean checkFieldEmpty(EditText text) {
        return text.getText().toString().equals("");
    }

    private boolean validatePhone() {
        if (checkFieldEmpty(phone)) {
            return true;
        } else {
            if (phone.getText().toString().length() < 4) {
                phone.setError("Phone is too short");
                return false;
            } else {
                if (phone.getText().toString().length() > 25) {
                    phone.setError("Phone is too long");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateBio() {
        if (checkFieldEmpty(bio)) {
            return true;
        } else {
            if (bio.getText().toString().length() < 4) {
                bio.setError("Bio is too short");
                return false;
            } else {
                if (bio.getText().toString().length() > 750) {
                    bio.setError("Bio is too long");
                    return false;
                }
            }
        }
        return true;
    }

    private void fillLifeStyles() {
        List<RoomFeature> lifeStyles = currentRoomie.getLifestyles();
        lifestyleContainer.removeAllViews();

        if (lifeStyles.isEmpty()) {
            noLifeStyles.setVisibility(View.VISIBLE);
        } else {
            Iterator iterator = lifeStyles.iterator();
            TextView tag;
            noLifeStyles.setVisibility(View.GONE);
            while (iterator.hasNext()) {
                RoomFeature element = (RoomFeature) iterator.next();
                tag = new TextView(new ContextThemeWrapper(getContext(), R.style.RoomieTags), null, 0);
                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams
                        (FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 5, 5, 5);
                tag.setText(element.getName());
                tag.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary));
                tag.setLayoutParams(params);
                lifestyleContainer.addView(tag);
            }
        }
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

        switch (requestCode) {
            case REQUEST_MAP_CODE:
                if (RESULT_OK == resultCode) {
                    address.setLocation(data.getDoubleArrayExtra("Address")[0] + "," + data.getDoubleArrayExtra("Address")[1]);
                    address.setState(data.getStringExtra("State"));
                    address.setCity(data.getStringExtra("City"));
                    locationChanged = true;
                    mapFragment.getMapAsync(this);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    pfp.setImageURI(result.getUri());
                    uploadPictureService.uploadFile(result.getUri(), currentRoomie.getId(), UploadPictureService.PictureType.PROFILE);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Log.e(TAG, result.getError().toString());

                }
                break;
            case REQUEST_LIFESTYLES_CODE:
                if (resultCode == 2) {
                    currentRoomie.setLifestyles(data.getParcelableArrayListExtra("newLifestyles"));
                    fillLifeStyles();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);

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
        if(address.getCity().equals("Default")){
            locationChanged = false;
        }else {
            locationChanged = true;
        }

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
        Toast.makeText(getContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
        showProgress(false);
        getFragmentManager().popBackStack();

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
        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

        if (locationChanged) {
            gMap.addMarker(new MarkerOptions().position(location));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            gMap.animateCamera(CameraUpdateFactory.zoomIn());
            gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        } else {
            gMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        }
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        cropImage(uri);
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
