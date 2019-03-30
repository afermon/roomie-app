package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Appointment;
import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.AppointmentState;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.service.AppointmentService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnGetRoomieByIdListener;
import com.cosmicode.roomie.util.listeners.OnGetUserByIdListener;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainRoomFragment extends Fragment implements OnGetUserByIdListener, OnGetRoomieByIdListener, OnMapReadyCallback, AppointmentService.OnAppointmentListener {

    private static final String TAG = "MainRoomFragment";
    private static final String ROOM = "room";
    private Room room;
    private RoomieService roomieService;
    private AppointmentService appointmentService;
    private Roomie roomie;
    private RecyclerView.Adapter mAdapterA, mAdapterR;
    private SupportMapFragment map;
    private JhiAccount user;

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
    @BindView(R.id.room_price)
    TextView roomPrice;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.room_scroll)
    ScrollView scrollView;
    @BindView(R.id.no_amenities)
    TextView noAmenties;
    @BindView(R.id.no_restrictions)
    TextView noRestrictions;


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
            appointmentService = new AppointmentService(getContext(), this);
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
        showProgress(true);
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
        if(room.getPictures().isEmpty()){
            RoomPicture p = new RoomPicture();
            p.setUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTW4YgDei5TJj0hNHD5HOuSAS7VFw0eP3zIKzjqE3efC0c7R46trg");
            room.getPictures().add(p);
        }
        carousel.setImageListener(imageListener);
        carousel.setPageCount(room.getPictures().size());

        Glide.with(getActivity().getApplicationContext())
                .load(roomie.getPicture())
                .into(profile);
        profile.bringToFront();

        profile.setOnClickListener( l -> {
            openFragment(MainProfileFragment.newInstance(roomie));
        });

        amount.setText(String.format("%s", room.getRooms()));

        Double priceUser = room.getPrice().getAmount() / room.getRooms(); // Price per user
        if (room.getPrice().getCurrency() == CurrencyType.DOLLAR) {
            roomPrice.setText(String.format("%s %s %s %s", getString(R.string.amount) + ": ", "$", priceUser.intValue(), "USD"));
        } else {
            roomPrice.setText(String.format("%s %s %s %s", getString(R.string.amount) + ": ", "₡", priceUser.intValue(), "CRC"));
        }

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

        if(lAmenities.isEmpty()){
            noAmenties.setVisibility(View.VISIBLE);
        }else {
            noAmenties.setVisibility(View.GONE);
        }

        if(lRestrictions.isEmpty()){
            noRestrictions.setVisibility(View.VISIBLE);
        }else {
            noRestrictions.setVisibility(View.GONE);
        }

        mAdapterA = new AmenitiesAdapter(lAmenities);
        amenities.setAdapter(mAdapterA);
        mAdapterR = new RestrictionsAdapter(lRestrictions);
        restrictions.setAdapter(mAdapterR);
        showProgress(false);
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
        mListener.getBaseActivity().getJhiUsers().findById(roomie.getUserId(), this);
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

    @Override
    public void onGetUserSuccess(JhiAccount user) {
        this.user = user;
        fillRoomInfo();
    }

    @Override
    public void onGetUserError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateAppointmentSuccess(Appointment appointment) {
        ((BaseActivity) getContext()).showUserMessage(getString(R.string.appointment_created_message), BaseActivity.SnackMessageType.SUCCESS);
        showProgress(false);
    }

    @Override
    public void onUpdateAppointmentSuccess(Appointment appointment) {

    }

    @Override
    public void onGetAppointmentSuccess(Appointment appointment) {

    }

    @Override
    public void onGetAppointmentListSuccess(Appointment appointment) {

    }

    @Override
    public void onAppointmentError(String error) {
        ((BaseActivity) getContext()).showUserMessage(String.format("Error: %s", error), BaseActivity.SnackMessageType.ERROR);
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


    @OnClick(R.id.appointment_btn)
    public void newAppointment(){
        DateTime currentTime = DateTime.now();
        int mYear = currentTime.getYear();
        int mMonth = currentTime.getMonthOfYear() - 1;
        int mDay = currentTime.getDayOfMonth();
        int mHour = currentTime.getHourOfDay();
        int mMinute = currentTime.getMinuteOfHour();

        Appointment appointment = new Appointment();
        appointment.setRoomId(room.getId());
        appointment.setState(AppointmentState.PENDING);
        //Petitioner will be set in backend

        AlertDialog.Builder newAppointmentDialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View newAppointmentLayout = inflater.inflate(R.layout.new_appointment_dialog, null);

        Button pickDateButton = newAppointmentLayout.findViewById(R.id.pick_appointment_date_btn);
        TextView appointmentDateTV = newAppointmentLayout.findViewById(R.id.appointment_date_tv);
        EditText appointmentDescriptionET = newAppointmentLayout.findViewById(R.id.appointment_description);

        pickDateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.RoomieDialogTheme,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        Log.d( TAG, dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.RoomieDialogTheme,
                                (view2, hourOfDay, minute) -> {
                                    Log.d(TAG, hourOfDay + ":" + minute);
                                    appointmentDateTV.setText(String.format("%s/%s/%s %s:%s", mDay, (mMonth + 1), mYear, mHour, mMinute));
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(currentTime.getMillis());
            datePickerDialog.show();
        });

        newAppointmentDialogBuilder.setTitle(R.string.new_appointment_title)
                .setView(newAppointmentLayout)
                .setPositiveButton(R.string.send, (dialog, which) -> { })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog newAppointmentDialog = newAppointmentDialogBuilder.create();
        newAppointmentDialog.show();

        newAppointmentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if(!appointmentDateTV.getText().toString().equals(getString(R.string.appointment_date))) {

                DateTime appointmentDateTime = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").parseDateTime(appointmentDateTV.getText().toString());

                DateTimeFormatter roomieInstantFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZoneUTC();

                appointment.setDateTime(roomieInstantFormatter.print(appointmentDateTime));
                appointment.setDesciption(appointmentDescriptionET.getText().toString());

                Log.d(TAG, appointment.toString());

                appointmentService.createAppointment(appointment);
                showProgress(true);

                newAppointmentDialog.dismiss();
            } else {
                pickDateButton.performClick();
            }
        });

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


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
