package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Appointment;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.AppointmentService;
import com.cosmicode.roomie.service.RoomieService;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsListFragment extends Fragment implements AppointmentService.OnAppointmentListener, RoomieService.OnGetCurrentRoomieListener {
    private static final String TAG = "AppointmentsListFragment";
    private AppointmentService appointmentService;
    private RoomieService roomieService;

    @BindView(R.id.appointments_list)
    RecyclerView appointmentsListRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_results)
    TextView noResults;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private Roomie currentRoomie;

    public AppointmentsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_appointments_list, container, false);
        appointmentService = new AppointmentService(getContext(), this);
        roomieService = new RoomieService(getContext(), this);
        roomieService.getCurrentRoomie();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        showProgress(true);
        appointmentService.getAllAppointmentsRoomie();
        super.onViewCreated(view, savedInstanceState);
    }


    private void showProgress(boolean show) {
        if (show) noResults.setVisibility(View.INVISIBLE);

        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        appointmentsListRecyclerView.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));

        appointmentsListRecyclerView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        appointmentsListRecyclerView.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));
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
    public void onCreateAppointmentSuccess(Appointment appointment) {

    }

    @Override
    public void onUpdateAppointmentSuccess(Appointment appointment) {

    }

    @Override
    public void onGetAppointmentSuccess(Appointment appointment) {

    }

    @Override
    public void onGetAppointmentListSuccess(List<Appointment> appointments) {
        if (appointments.size() > 0) {
            List<Appointment> myRoomsRequests = new ArrayList<>();
            List<Appointment> myRequests = new ArrayList<>();

            for (Appointment appointment: appointments){
                if(appointment.getRoom().getOwnerId() == currentRoomie.getId()) {
                    appointment.isOwner(true);
                    myRoomsRequests.add(appointment);
                }
                else {
                    appointment.isOwner(false);
                    myRequests.add(appointment);
                }
            }

            noResults.setVisibility(View.GONE);
            sectionAdapter = new SectionedRecyclerViewAdapter();

            if(myRoomsRequests.size() > 0)
            sectionAdapter.addSection(new AppointmentSection(getString(R.string.my_rooms_requests), myRoomsRequests));

            if(myRequests.size() > 0)
            sectionAdapter.addSection(new AppointmentSection(getString(R.string.my_appointments_requests), myRequests));

            appointmentsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            appointmentsListRecyclerView.setAdapter(sectionAdapter);

        } else {
            appointmentsListRecyclerView.setAdapter(null);
            noResults.setVisibility(View.VISIBLE);
        }
        showProgress(false);
    }

    @Override
    public void onAppointmentError(String error) {
        ((BaseActivity) getContext()).showUserMessage(getString(R.string.appointment_created_message), BaseActivity.SnackMessageType.SUCCESS);
        showProgress(false);
        noResults.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        currentRoomie = roomie;
    }

    @Override
    public void onGetCurrentRoomieError(String error) {

    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }

    private class AppointmentSection extends StatelessSection {

        final String title;
        final List<Appointment> appointmentList;

        AppointmentSection(String title, List<Appointment> appointmentList) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.appointment_list_item)
                    .headerResourceId(R.layout.appointment_list_group)
                    .build());

            this.title = title;
            this.appointmentList = appointmentList;
        }

        @Override
        public int getContentItemsTotal() {
            return this.appointmentList.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final AppointmentViewHolder itemHolder = (AppointmentViewHolder) holder;

            Appointment appointment = this.appointmentList.get(position);
            itemHolder.description.setText(appointment.getDescription());
            itemHolder.state.setText(appointment.getState().name());
            itemHolder.userName.setText(appointment.getPetitioner().getUser().getFirstName());
            itemHolder.date.setText(formattDateString(appointment.getDateTime()));
            Glide.with(getContext()).load(appointment.getPetitioner().getPicture()).centerCrop().into(itemHolder.profileImage);

            if(appointment.isOwner()){

            } else {

            }

        }

        public String formattDateString(String pdate){
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withLocale(Locale.ROOT)
                    .withChronology(ISOChronology.getInstanceUTC());
            String dt = new String();
            if (pdate !=null){
                DateTime date = format.parseDateTime(pdate);
                dt = date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear() + " " + date.getHourOfDay() + ":" + date.getMinuteOfHour();
            }
            return dt;
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new AppointmentGroupViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            AppointmentGroupViewHolder headerHolder = (AppointmentGroupViewHolder) holder;

            headerHolder.groupTitle.setText(this.title);
        }
    }

    private class AppointmentGroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupTitle;

        AppointmentGroupViewHolder(View view) {
            super(view);
            groupTitle = view.findViewById(R.id.appointment_group);
        }
    }

    private class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final CircleImageView profileImage;
        private final TextView userName;
        private final TextView date;
        private final TextView state;
        private final TextView description;
        private final CardView container;

        AppointmentViewHolder(View view) {
            super(view);
            rootView = view;
            profileImage = view.findViewById(R.id.profile_image);
            userName = view.findViewById(R.id.appointment_user_name);
            date = view.findViewById(R.id.appointment_date);
            state = view.findViewById(R.id.appointment_state);
            description = view.findViewById(R.id.appointment_description);
            container = view.findViewById(R.id.appointment_card);
        }
    }

}
