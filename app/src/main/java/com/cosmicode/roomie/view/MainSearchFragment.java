package com.cosmicode.roomie.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.service.RoomService;
import com.cosmicode.roomie.util.adapters.SearchRoomRecyclerViewAdapter;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSearchFragment extends Fragment implements RoomService.RoomServiceListener, SearchView.OnCloseListener {

    private static final String TAG = "SearchFragment";
    private static final String ARG_SEARCH_QUERY = "search-query";
    private String searchQuery;

    @BindView(R.id.room_list)
    RecyclerView roomListRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.search_layout)
    ConstraintLayout searchLayout;
    @BindView(R.id.no_results)
    TextView noResults;

    private OnFragmentInteractionListener mListener;
    private RoomService roomService;
    private Location currentUserLocation;
    private static final int LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    public MainSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchQuery Parameter 1.
     * @return A new instance of fragment MainSearchFragment.
     */
    public static MainSearchFragment newInstance(String searchQuery) {
        MainSearchFragment fragment = new MainSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getContext());
        roomService = new RoomService(getContext(), this);
        if (getArguments() != null) {
            searchQuery = getArguments().getString(ARG_SEARCH_QUERY);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        createLocationRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_search, container, false);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        searchView.setIconified(false);
        searchView.setOnCloseListener(this);
        searchView.setQueryHint("Search....");
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(Color.WHITE);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = searchPlate.findViewById(searchTextId);
            if (searchText!=null) {
                searchText.setTextColor(getActivity().getResources().getColor(R.color.light));
                searchText.setHintTextColor(getActivity().getResources().getColor(R.color.light));
            }
        }

        showProgress(true);

        roomListRecyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                //searchLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                searchLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                //searchLayout.animate().translationY(searchLayout.getHeight() - 8).setInterpolator(new AccelerateInterpolator(2)).start();
                searchLayout.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: Submit search
                showProgress(true);
                roomService.serachRooms(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO: Typeahead
                return false;
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location access not granted");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            Log.e(TAG, "Asking for location");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            currentUserLocation = location;
                            Log.i(TAG, "Current user location: " + currentUserLocation.toString());
                            roomService.getAllRooms();
                        }
                    });
        }
    }

    private void showProgress(boolean show) {
        if(show) noResults.setVisibility(View.INVISIBLE);

        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        roomListRecyclerView.setVisibility(((show) ? View.GONE : View.VISIBLE));

        roomListRecyclerView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        roomListRecyclerView.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

        progressBar.setVisibility(((show) ? View.VISIBLE : View.GONE));
        progressBar.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 1 : 0))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setVisibility(((show) ? View.VISIBLE : View.GONE));
                    }
                });
    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {
        if (rooms.size() > 0){
            noResults.setVisibility(View.INVISIBLE);
            roomListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            roomListRecyclerView.setAdapter(new SearchRoomRecyclerViewAdapter(rooms, currentUserLocation, mListener, getContext()));
        } else
            noResults.setVisibility(View.VISIBLE);

        showProgress(false);
    }

    @Override
    public void OnGetRoomsError(String error) {

    }

    @Override //Search view close
    public boolean onClose() {
        return true;
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
        void onSearchFragmentInteraction(Room item);
    }


    public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {
        static final float MINIMUM = 100;
        int scrollDist = 0;
        boolean isVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (isVisible && scrollDist > MINIMUM) {
                hide();
                scrollDist = 0;
                isVisible = false;
            }
            else if (!isVisible && scrollDist < -MINIMUM) {
                show();
                scrollDist = 0;
                isVisible = true;
            }

            if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                scrollDist += dy;
            }
        }

        public abstract void show();
        public abstract void hide();
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

        task.addOnSuccessListener(getActivity(), locationSettingsResponse -> Log.i(TAG, locationSettingsResponse.toString()));
        task.addOnFailureListener(getActivity(), e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(getActivity(),
                            1);
                } catch (IntentSender.SendIntentException sendEx) {
                    Log.e(TAG, sendEx.getMessage());
                }
            }
        });
    }
}
