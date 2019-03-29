package com.cosmicode.roomie.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ChooseLocationActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.SearchFilter;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.service.RoomFeatureService;
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
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.cosmicode.roomie.util.GeoLocationUtil.getLocationText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSearchFragment extends Fragment implements RoomService.RoomServiceListener, RoomFeatureService.OnGetFeaturesListener {

    public static final String CHOOSE_LOCATION_ADDRESS = "Address";
    public static final String CHOOSE_LOCATION_CITY = "City";
    public static final String CHOOSE_LOCATION_STATE = "State";
    public static final int REQUEST_MAP_CODE = 1;
    private static final String TAG = "SearchFragment";
    private static final String ARG_SEARCH_FILTER = "search-filter";
    private static final int LOCATION_PERMISSION = 1;
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
    @BindView(R.id.search_filters)
    ImageButton searchFiltersButton;
    List<RoomFeature> lAmenities;
    List<RoomFeature> lRestrictions;
    private float density = (float) 1;
    private OnFragmentInteractionListener mListener;
    private RoomService roomService;
    private RoomFeatureService roomFeatureService;
    private FusedLocationProviderClient fusedLocationClient;
    private SearchFilter searchFilter;

    public MainSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchFilter Parameter searchFilter.
     * @return A new instance of fragment MainSearchFragment.
     */
    public static MainSearchFragment newInstance(SearchFilter searchFilter) {
        MainSearchFragment fragment = new MainSearchFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SEARCH_FILTER, searchFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getContext());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        createLocationRequest();
        if (getArguments() != null)
            searchFilter = getArguments().getParcelable(ARG_SEARCH_FILTER);

        roomFeatureService = new RoomFeatureService(getContext(), this);
        roomFeatureService.getAll();
        roomService = new RoomService(getContext(), this);
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
        density = getContext().getResources().getDisplayMetrics().density;

        searchView.setQueryHint("Search....");
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.WHITE);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = searchPlate.findViewById(searchTextId);
            if (searchText != null) {
                searchText.setTextColor(getActivity().getResources().getColor(R.color.light));
                searchText.setHintTextColor(getActivity().getResources().getColor(R.color.light));
            }
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showProgress(true);
                searchFilter.setQuery(query);
                mListener.onSearchFilterUpdated(searchFilter);
                roomService.searchRoomsAdvanced(searchFilter);
                showProgress(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(v -> {
            Log.d(TAG, "search expanded");
            searchView.setQuery(searchFilter.getQuery(), false);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) searchView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, (int) (57 * density), layoutParams.rightMargin, layoutParams.bottomMargin);
            layoutParams.horizontalBias = (float) 0.5;
            searchView.setLayoutParams(layoutParams);
            searchFiltersButton.setVisibility(View.VISIBLE);
        });

        searchView.setOnCloseListener(() -> {
            Log.d(TAG, "search closed");
            searchFiltersButton.setVisibility(View.INVISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) searchView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, (int) (13 * density), layoutParams.rightMargin, layoutParams.bottomMargin);
            layoutParams.horizontalBias = (float) 1;
            searchView.setLayoutParams(layoutParams);
            searchFiltersButton.setVisibility(View.INVISIBLE);
            return false;
        });

        showProgress(true);

        roomListRecyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                searchView.setIconified(false);
            }

            @Override
            public void hide() {
                searchView.setIconified(true);
                if (!searchView.isIconified()) searchView.setIconified(true);
            }
        });

        if (searchFilter.getLatitude() != null && searchFilter.getLongitude() != null) {
            Log.i(TAG, "Current user filters: " + searchFilter.toString());
            roomService.searchRoomsAdvanced(searchFilter);
        } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location access not granted");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "Asking for location");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            searchFilter.setLatitude(location.getLatitude());
                            searchFilter.setLongitude(location.getLongitude());
                            String[] locationText = getLocationText(location, getContext());
                            searchFilter.setCity(locationText[0]);
                            searchFilter.setState(locationText[1]);
                            mListener.onSearchFilterUpdated(searchFilter);
                            Log.i(TAG, "Current user filters: " + searchFilter.toString());
                            roomService.searchRoomsAdvanced(searchFilter);
                        }
                    });
        }
    }

    @OnClick(R.id.search_filters)
    public void searchWithFilters() {
        AlertDialog.Builder filtersDialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog filterDialog;

        filtersDialogBuilder.setTitle(R.string.search_filter_title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View searchFiltersView = inflater.inflate(R.layout.search_filters_layout, null);

        TextView searchLocationTextView = searchFiltersView.findViewById(R.id.search_location_view);
        searchLocationTextView.setText(String.format("%s, %s", searchFilter.getCity(), searchFilter.getState()));

        TextView searchLocationDistanceViewTV = searchFiltersView.findViewById(R.id.search_location_distance);
        searchLocationDistanceViewTV.setText(String.format("%s Km", searchFilter.getDistance()));

        RangeSeekBar distanceFilterSeekBar = searchFiltersView.findViewById(R.id.distance_filter);
        distanceFilterSeekBar.setTypeface(Typeface.DEFAULT_BOLD);
        distanceFilterSeekBar.getLeftSeekBar().setTypeface(Typeface.DEFAULT_BOLD);
        distanceFilterSeekBar.setIndicatorTextDecimalFormat("0");
        distanceFilterSeekBar.setValue(searchFilter.getDistance());

        distanceFilterSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float maxDistance, float rightValue, boolean isFromUser) {
                Log.d(TAG, "Distance: left: " + maxDistance);
                searchFilter.setDistance((int) maxDistance);
                searchLocationDistanceViewTV.setText(String.format("%s Km", searchFilter.getDistance()));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        Button usdButton = searchFiltersView.findViewById(R.id.filter_currency_usd);
        Button crcButton = searchFiltersView.findViewById(R.id.filter_currency_crc);

        TextView priceTitleTV = searchFiltersView.findViewById(R.id.search_filter_price_title);

        usdButton.setOnClickListener(v -> {
            usdButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
            usdButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            crcButton.setBackgroundTintList(null);
            crcButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            searchFilter.setCurrency(CurrencyType.DOLLAR);
            priceTitleTV.setText(R.string.price);
        });

        crcButton.setOnClickListener(v -> {
            crcButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
            crcButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            usdButton.setBackgroundTintList(null);
            usdButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            searchFilter.setCurrency(CurrencyType.COLON);
            priceTitleTV.setText(String.format("%s (x1000)", getResources().getString(R.string.price)));
        });

        if (searchFilter.getCurrency() == CurrencyType.COLON)
            crcButton.performClick();

        RangeSeekBar priceFilterSeekBar = searchFiltersView.findViewById(R.id.price_filter);
        priceFilterSeekBar.setTypeface(Typeface.DEFAULT_BOLD);
        priceFilterSeekBar.getLeftSeekBar().setTypeface(Typeface.DEFAULT_BOLD);
        priceFilterSeekBar.setIndicatorTextDecimalFormat("0");
        priceFilterSeekBar.setValue(searchFilter.getPriceMin(), searchFilter.getPriceMax());

        priceFilterSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float minPrice, float maxPrice, boolean isFromUser) {
                Log.d(TAG, "Price: Min: " + minPrice + " Max: " + maxPrice);
                searchFilter.setPriceMin((int) minPrice);
                searchFilter.setPriceMax((int) maxPrice);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        RecyclerView amenitiesRecyclerView = searchFiltersView.findViewById(R.id.amenities_recycler_view);
        RecyclerView restrictionsRecyclerView = searchFiltersView.findViewById(R.id.restrictions_recycler_view);

        amenitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        restrictionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView.Adapter mAdapter = new FeaturesAdapter(lAmenities);
        amenitiesRecyclerView.setAdapter(mAdapter);
        RecyclerView.Adapter mAdapter2 = new FeaturesAdapter(lRestrictions);
        restrictionsRecyclerView.setAdapter(mAdapter2);

        filtersDialogBuilder.setView(searchFiltersView);

        filtersDialogBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {
            mListener.onSearchFilterUpdated(searchFilter);
            roomService.searchRoomsAdvanced(searchFilter);
            searchView.setIconified(true);
            if (!searchView.isIconified()) searchView.setIconified(true);
            showProgress(true);
        });
        filterDialog = filtersDialogBuilder.create();

        searchLocationTextView.setOnClickListener(v -> {
            filterDialog.dismiss();
            changeSearchGeo();
        });

        searchFiltersView.findViewById(R.id.search_change_geo_dialog).setOnClickListener(v -> {
            filterDialog.dismiss();
            changeSearchGeo();
        });

        filterDialog.show();
    }

    private void showProgress(boolean show) {
        if (show) noResults.setVisibility(View.INVISIBLE);

        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        roomListRecyclerView.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));

        roomListRecyclerView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        roomListRecyclerView.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));
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

    @OnClick(R.id.search_change_geo)
    public void changeSearchGeo() {
        Log.d(TAG, searchFilter.toString());
        double[] coordinates = {searchFilter.getLatitude(), searchFilter.getLongitude()};
        Intent intent = new Intent(getContext(), ChooseLocationActivity.class);
        intent.putExtra(CHOOSE_LOCATION_ADDRESS, coordinates);
        startActivityForResult(intent, REQUEST_MAP_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_MAP_CODE == requestCode && RESULT_OK == resultCode) {
            searchFilter.setLatitude(data.getDoubleArrayExtra(CHOOSE_LOCATION_ADDRESS)[0]);
            searchFilter.setLongitude(data.getDoubleArrayExtra(CHOOSE_LOCATION_ADDRESS)[1]);
            searchFilter.setCity(data.getExtras().getString(CHOOSE_LOCATION_CITY));
            searchFilter.setState(data.getExtras().getString(CHOOSE_LOCATION_STATE));
            mListener.onSearchFilterUpdated(searchFilter);
            Log.d(TAG, searchFilter.toString());
            Toast.makeText(getContext(), String.format("%s, %s", searchFilter.getCity(), searchFilter.getState()), Toast.LENGTH_SHORT).show();
            roomService.searchRoomsAdvanced(searchFilter);
            searchView.setIconified(true);
            if (!searchView.isIconified()) searchView.setIconified(true);
            showProgress(true);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnCreateSuccess(Room room) {

    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {
        Log.d(TAG, "Success getting rooms");
        if (rooms.size() > 0) {
            noResults.setVisibility(View.GONE);
            roomListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            roomListRecyclerView.setAdapter(new SearchRoomRecyclerViewAdapter(rooms, searchFilter.getLocation(), mListener, getContext()));
        } else {
            roomListRecyclerView.setAdapter(null);
            noResults.setVisibility(View.VISIBLE);
        }
        showProgress(false);
    }

    @Override
    public void OnGetRoomsError(String error) {
        roomListRecyclerView.setAdapter(null);
        noResults.setVisibility(View.VISIBLE);
        showProgress(false);
    }

    @Override
    public void OnUpdateSuccess(Room room) {

    }

    @Override
    public void onGetFeaturesSuccess(List<RoomFeature> featureList) {
        lAmenities = new ArrayList<>();
        lRestrictions = new ArrayList<>();
        for (RoomFeature feature : featureList) {
            if (feature.getType() == FeatureType.AMENITIES) {
                lAmenities.add(feature);
            } else {
                if(feature.getType() == FeatureType.RESTRICTIONS){
                    lRestrictions.add(feature);
                }
            }
        }
    }

    @Override
    public void onGetFeaturesError(String error) {
        Log.e(TAG, error);
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

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();

        void onSearchFragmentInteraction(Room item);
        void onSearchFilterUpdated(SearchFilter searchFilter);
    }

    public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {
        final float MINIMUM = 100 * density;
        int scrollDist = 0;
        boolean isVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (isVisible && scrollDist > MINIMUM) {
                hide();
                scrollDist = 0;
                isVisible = false;
            } else if (!isVisible && scrollDist < -MINIMUM) {
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

    public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.IconViewHolder> {
        private List<RoomFeature> features;

        public FeaturesAdapter(List<RoomFeature> features) {
            this.features = features;
        }

        public int getItemCount() {
            return features.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.icon_item, viewGroup, false);
            IconViewHolder vh = new IconViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final IconViewHolder holder, int position) {

            RoomFeature feature = this.features.get(position);
            holder.iconText.setText(feature.getName());

            if (searchFilter.getFeatures().contains(feature)) {
                holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
            }

            Glide.with(holder.itemView).load(feature.getIcon()).centerCrop().into(holder.icon);
            holder.icon.setOnClickListener(v -> {
                if (holder.iconText.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.primary)) {
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                    searchFilter.getFeatures().remove(feature);
                } else {
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                    searchFilter.getFeatures().add(feature);
                }
            });
        }

        public class IconViewHolder extends RecyclerView.ViewHolder {

            private TextView iconText;
            private ImageButton icon;

            IconViewHolder(View view) {
                super(view);
                iconText = view.findViewById(R.id.icon_text);
                icon = view.findViewById(R.id.icon);
            }

        }
    }
}
