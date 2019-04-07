package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Appointment;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.AppointmentState;
import com.cosmicode.roomie.service.AppointmentService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.RoomieTimeUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

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
        ((BaseActivity) getContext()).showUserMessage(getString(R.string.appointment_updated), BaseActivity.SnackMessageType.SUCCESS);
        appointmentService.getAllAppointmentsRoomie();
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

    @OnClick(R.id.back_button)
    public void back(){
        getFragmentManager().popBackStack();
    }

    @SuppressLint("RestrictedApi")
    private void appointmentPopupMenu(View v, Appointment appointment) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.appointments_menu, popup.getMenu());

        Menu menu = popup.getMenu();

        if (!appointment.getState().equals(AppointmentState.PENDING) || !appointment.isOwner()){
            menu.removeItem(R.id.appointment_accept);
            menu.removeItem(R.id.appointment_decline);
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.appointment_accept:
                    appointment.setState(AppointmentState.ACCEPTED);
                    appointmentService.updateAppointment(appointment);
                    return true;
                case R.id.appointment_decline:
                    appointment.setState(AppointmentState.DECLINED);
                    appointmentService.updateAppointment(appointment);
                    return true;
                case R.id.appointment_user:
                    MainProfileFragment roomieView = MainProfileFragment.newInstance(appointment.getPetitioner());
                    FragmentTransaction transaction2 = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.main_container, roomieView);
                    transaction2.addToBackStack(null);
                    transaction2.commit();
                    return true;
                case R.id.appointment_room:
                    MainRoomFragment roomView = MainRoomFragment.newInstance(appointment.getRoom());
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_container, roomView);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }

            return false;
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(v.getContext(), (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();

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
            itemHolder.date.setText(RoomieTimeUtil.instantUTCStringToLocalDateTimeString(appointment.getDateTime()));
            Glide.with(getContext()).load(appointment.getPetitioner().getPicture()).centerCrop().into(itemHolder.profileImage);
            itemHolder.container.setOnClickListener(v -> appointmentPopupMenu(itemHolder.settings, appointment));
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
        private final TextView date;
        private final TextView state;
        private final TextView description;
        private final ImageView settings;
        private final CardView container;

        AppointmentViewHolder(View view) {
            super(view);
            rootView = view;
            profileImage = view.findViewById(R.id.profile_image);
            date = view.findViewById(R.id.appointment_date);
            state = view.findViewById(R.id.appointment_state);
            description = view.findViewById(R.id.appointment_description);
            settings = view.findViewById(R.id.appointment_settings);
            container = view.findViewById(R.id.appointment_card);
        }
    }

}
