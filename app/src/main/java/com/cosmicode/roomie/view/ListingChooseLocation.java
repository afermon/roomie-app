package com.cosmicode.roomie.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ChooseLocationActivity;
import com.cosmicode.roomie.MainActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.RoomType;
import com.cosmicode.roomie.service.RoomPictureService;
import com.cosmicode.roomie.service.RoomService;
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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;

import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ListingChooseLocation extends Fragment implements Validator.ValidationListener, RoomPictureService.OnCreatePictureListener, RoomieService.OnGetCurrentRoomieListener, RoomService.RoomServiceListener, OnMapReadyCallback, UploadPictureService.OnUploadPictureListener {


    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private RoomCreate room;
    public static final String CHOOSE_LOCATION_ADDRESS = "Address";
    public static final int REQUEST_MAP_CODE = 1;
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;
    private Address address;
    private static final int LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationChanged = false;
    private ImageButton geoButton;
    private UploadPictureService uploadPictureService;
    private RoomService roomService;
    private RoomieService roomieService;
    private RoomPictureService roomPictureService;
    private static int picAmount;
    private Validator validator;
    private Room newRoom;

    @NotEmpty
    @Length(min = 4, max = 200)
    @BindView(R.id.appointment_notes)
    EditText notes;
    @BindView(R.id.progress)
    ProgressBar progress;
    @NotEmpty
    @Length(min = 4, max = 500)
    @BindView(R.id.address_desc)
    EditText desc;
    @BindView(R.id.back_location)
    ImageButton back;
    @BindView(R.id.btn_finished)
    Button finish;
    @BindView(R.id.scroll_location)
    ScrollView scrollView;

    public ListingChooseLocation() {
        // Required empty public constructor
    }


    public static ListingChooseLocation newInstance(RoomCreate room) {
        ListingChooseLocation fragment = new ListingChooseLocation();
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
            uploadPictureService = new UploadPictureService(getContext(), this);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            roomService = new RoomService(getContext(), this);
            roomieService = new RoomieService(getContext(), this);
            roomPictureService = new RoomPictureService(getContext(), this);
            picAmount = room.getPicturesUris().size();
            validator = new Validator(this);
            validator.setValidationListener(this);
            createLocationRequest();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        geoButton = getView().findViewById(R.id.geo_button);
        geoButton.setOnClickListener(this::onClickGeo);
        if(room.getAddress() == null){
            address = new Address();
            address.setLocation("10.3704815,-83.9526349");
            address.setCity("No city");
            address.setState("No state");
            room.setAddress(address);
            locationChanged = false;
        }else{
            desc.setText(room.getAddress().getDescription());
            notes.setText(room.getApoinmentsNotes());
            locationChanged = true;
        }

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_MAP_CODE == requestCode) {
            if (RESULT_OK == resultCode) {
                room.getAddress().setLocation(data.getDoubleArrayExtra("Address")[0] + "," + data.getDoubleArrayExtra("Address")[1]);
                room.getAddress().setState(data.getStringExtra("State"));
                room.getAddress().setCity(data.getStringExtra("City"));
                locationChanged = true;
                mapFragment.getMapAsync(this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
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

    private void saveState(){
        room.getAddress().setDescription(desc.getText().toString());
        room.setApoinmentsNotes(notes.getText().toString());
    }

    @OnClick(R.id.cancel_location)
    public void finish(View view){ getActivity().finish();}

    @OnClick(R.id.back_location)
    public void back(View view) {
        saveState();
        mListener.openFragment(ListingChoosePictures.newInstance(room), "left");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listing_choose_location, container, false);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng location = new LatLng(room.getAddress().getLatitude(), room.getAddress().getLongitude());

        if (locationChanged) {
            gMap.addMarker(new MarkerOptions().position(location));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            gMap.animateCamera(CameraUpdateFactory.zoomIn());
            gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        } else {
            gMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        }
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

    @OnClick(R.id.btn_finished)
    public void onClickFinish(View view) {
        validator.validate();
    }


    @Override
    public void onUploaddSuccess(String url) {
        if (picAmount != 0) {
            RoomPicture roomPicture = new RoomPicture();
            if (picAmount == room.getPicturesUris().size()) {
                roomPicture.setIsMain(true);
            } else {
                roomPicture.setIsMain(false);
            }
            roomPicture.setUrl(url);
            roomPicture.setRoomId(room.getId());
            roomPictureService.createPic(roomPicture);
        }

        picAmount--;
    }

    @Override
    public void onUploadError(String error) {

    }

    @Override
    public void OnCreateSuccess(Room room) {
        this.room.setId(room.getId());
        int i = 1;
        String id;
        for (Uri picturesUris : this.room.getPicturesUris()) {
            id = room.getId() + "-" + i;
            uploadPictureService.uploadFileRoom(picturesUris, id, UploadPictureService.PictureType.PROFILE);
            i++;
        }
    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {

    }

    @Override
    public void OnGetRoomsError(String error) {
        showProgress(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnUpdateSuccess(Room room) {
        showProgress(false);
        Toast.makeText(getContext(), "Room created successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        room.setRoomType(RoomType.ROOM);
        room.setPremium(false);
        saveState();
        DateTime now = DateTime.now();
        String month, day, hour, minutes, seconds;
        month = Integer.toString(now.getMonthOfYear());
        day = Integer.toString(now.getDayOfMonth());
        hour = Integer.toString(now.getHourOfDay());
        minutes = Integer.toString(now.getMinuteOfHour());
        seconds = Integer.toString(now.getSecondOfMinute());

        if (now.getMonthOfYear() < 10) {
            month = "0" + now.getMonthOfYear();
        }
        if (now.getDayOfMonth() < 10) {
            day = "0" + now.getDayOfMonth();
        }
        if (now.getHourOfDay() < 10) {
            hour = "0" + now.getHourOfDay();
        }
        if (now.getMinuteOfHour() < 10) {
            minutes = "0" + now.getMinuteOfHour();
        }
        if (now.getSecondOfMinute() < 10) {
            seconds = "0" + now.getSecondOfMinute();
        }

        String date = String.format("%s-%s-%sT%s:%s:%sZ", now.getYear(), month, day, hour, minutes, seconds);
        room.setPublished(date);
        room.setCreated(date);
        room.setOwnerId(roomie.getId());
        roomService.createRoom(room);
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        showProgress(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }

    @Override
    public void onCreatePicSuccess() {
        if (picAmount == 0) {
            roomService.updateRoomIndexing(room, room.getAddress(), room.getMonthly());
        }
    }

    @Override
    public void onPictureError(String error) {
        showProgress(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        showProgress(true);
        roomieService.getCurrentRoomie();
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

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
        void openFragment(Fragment fragment, String start);
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
