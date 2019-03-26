package com.cosmicode.roomie.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnGetRoomieByIdListener;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainRoomFragment extends Fragment implements OnGetRoomieByIdListener, OnMapReadyCallback {

    private static final String ROOM = "room";
    private Room room;
    private RoomieService roomieService;
    private Roomie roomie;
    private RecyclerView.Adapter mAdapterA, mAdapterR;
    private SupportMapFragment map;

    @BindView(R.id.room_title)
    TextView title;
    @BindView(R.id.room_address)
    TextView addressDesc;
    @BindView(R.id.pfp)
    ImageView profile;
    @BindView(R.id.move_in)
    TextView moveIn;
    @BindView(R.id.move_out)
    TextView moveOut;
    @BindView(R.id.room_description)
    TextView roomDesc;
    @BindView(R.id.amenities_recycler)
    RecyclerView amenities;
    @BindView(R.id.restrictions_recycler)
    RecyclerView restrictions;
    @BindView(R.id.roomie_number)
    TextView amount;
    @BindView(R.id.address_description)
    TextView addressAddDesc;
    @BindView(R.id.appointment_btn)
    ImageButton appointment;
    @BindView(R.id.mail_btn)
    ImageButton mail;
    @BindView(R.id.room_pics)
    CarouselView carousel;


    private OnFragmentInteractionListener mListener;

    public MainRoomFragment() {
        // Required empty public constructor
    }

    public static MainRoomFragment newInstance(Room room) {
        MainRoomFragment fragment = new MainRoomFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.room = getArguments().getParcelable(ROOM);
            roomieService = new RoomieService(getContext());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);

        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(getContext());
        layoutManager2.setFlexDirection(FlexDirection.ROW);
        layoutManager2.setJustifyContent(JustifyContent.FLEX_START);

        amenities.setLayoutManager(layoutManager);
        restrictions.setLayoutManager(layoutManager2);
        map = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        loadOwner();
    }


    private void loadOwner() {
        roomieService.getRoomieById(room.getOwnerId(), this);
    }

    private void fillRoomInfo() {
        map.getMapAsync(this);
        title.setText(room.getTitle());
        addressDesc.setText(String.format("%s, %s", room.getAddress().getCity(), room.getAddress().getState()));
        roomDesc.setText(room.getDescription());
        addressAddDesc.setText(room.getAddress().getDescription());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime available = dateTimeFormatter.parseDateTime(room.getAvailableFrom());
        DateTime now = new DateTime();
        if (now.isAfter(available)) {
            moveIn.setText(getString(R.string.available_now));
        } else {
            moveIn.setText(String.format("%s/%s/%s", available.getDayOfMonth(), available.getMonthOfYear(), available.getYear()));
        }

        if (room.getPrice().getFinishDate() == null) {
            moveOut.setText(getString(R.string.no_move_out));
        } else {
            DateTime finish = dateTimeFormatter.parseDateTime(room.getAvailableFrom());
            moveOut.setText(String.format("%s/%s/%s", finish.getDayOfMonth(), finish.getMonthOfYear(), finish.getYear()));

        }
        carousel.setImageListener(imageListener);
        carousel.setPageCount(room.getPictures().size());

        Glide.with(getActivity().getApplicationContext())
                .load(roomie.getPicture())
                .into(profile);
        profile.bringToFront();

        amount.setText(String.format("%s", room.getRooms()));

        fillFeatures();
    }

    private void fillFeatures() {
        List<RoomFeature> lAmenities = new ArrayList<>();
        List<RoomFeature> lRestrictions = new ArrayList<>();
        for (RoomFeature feature : room.getFeatures()) {
            if (feature.getType() == FeatureType.AMENITIES) {
                lAmenities.add(feature);
            } else if (feature.getType() == FeatureType.RESTRICTIONS) {
                lRestrictions.add(feature);
            }
        }

        mAdapterA = new AmenitiesAdapter(lAmenities);
        amenities.setAdapter(mAdapterA);
        mAdapterR = new RestrictionsAdapter(lRestrictions);
        restrictions.setAdapter(mAdapterR);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(getActivity().getApplicationContext())
                    .load(room.getPictures().get(position).getUrl())
                    .into(imageView);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_room, container, false);
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
    public void OnGetRoomieByIdSuccess(Roomie roomie) {
        this.roomie = roomie;
        fillRoomInfo();
    }

    @Override
    public void onGetRoomieError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap gMap = googleMap;
        LatLng location = new LatLng(room.getAddress().getLatitude(), room.getAddress().getLongitude());
        gMap.addMarker(new MarkerOptions().position(location).title("Room location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        gMap.animateCamera(CameraUpdateFactory.zoomIn());
        gMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.IconViewHolder> {
        private List<RoomFeature> features;

        public class IconViewHolder extends RecyclerView.ViewHolder {

            private TextView iconText;
            private ImageButton icon;

            IconViewHolder(View view) {
                super(view);
                iconText = view.findViewById(R.id.icon_text);
                icon = view.findViewById(R.id.icon);
            }

        }

        public AmenitiesAdapter(List<RoomFeature> features) {
            this.features = features;
        }

        public int getItemCount() {
            return features.size();
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
            Glide.with(holder.itemView).load(feature.getIcon()).centerCrop().into(holder.icon);
        }
    }

    public class RestrictionsAdapter extends RecyclerView.Adapter<RestrictionsAdapter.IconViewHolder> {
        private List<RoomFeature> features;

        public class IconViewHolder extends RecyclerView.ViewHolder {

            private TextView iconText;
            private ImageButton icon;

            IconViewHolder(View view) {
                super(view);
                iconText = view.findViewById(R.id.icon_text);
                icon = view.findViewById(R.id.icon);
            }

        }

        public RestrictionsAdapter(List<RoomFeature> features) {
            this.features = features;
        }

        public int getItemCount() {
            return features.size();
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
            Glide.with(holder.itemView).load(feature.getIcon()).centerCrop().into(holder.icon);
        }
    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
