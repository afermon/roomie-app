package com.cosmicode.roomie.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.RoomieUser;
import com.cosmicode.roomie.domain.UserPreferences;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.domain.enumeration.Lang;
import com.cosmicode.roomie.service.ConfigurationService;
import com.cosmicode.roomie.service.RoomieService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MainConfigurationFragment extends Fragment implements ConfigurationService.ConfigurationListener, RoomieService.OnGetCurrentRoomieListener {


    private OnFragmentInteractionListener mListener;
    private Switch todo,calendar,payment,appointments;
    private Button colones,dollars, espanol, english;
    private CurrencyType currency;
    private Lang language;
    private boolean todoB,calendarB,appointmentB,paymentB;
    private ConfigurationService configService;
    private  RoomieService roomieService;
    private  Roomie currentRoomie;
    private UserPreferences userPreferences;
    private Button saveButton;


    public MainConfigurationFragment() {
        // Required empty public constructor
    }

    public static MainConfigurationFragment newInstance(String param1, String param2) {
        MainConfigurationFragment fragment = new MainConfigurationFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todo = getView().findViewById(R.id.switch_todo);
        calendar = getView().findViewById(R.id.switch_calendar);
        payment = getView().findViewById(R.id.switch_payments);
        appointments = getView().findViewById(R.id.switch_appointments);
        colones = getView().findViewById(R.id.colones_button);
        dollars = getView().findViewById(R.id.dollars_button);
        espanol = getView().findViewById(R.id.espanol_button);
        english = getView().findViewById(R.id.english_button);
        saveButton = getView().findViewById(R.id.button_save);

        colones.setOnClickListener(this::onClickColones);
        dollars.setOnClickListener(this::onClickDollars);
        espanol.setOnClickListener(this::onClickEspanol);
        english.setOnClickListener(this::onClickEnglish);
        saveButton.setOnClickListener(this::onClickSave);


        todo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todoB = isChecked;
            }

        });


        calendar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calendarB = isChecked;
            }

        });


        payment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentB = isChecked;
            }

        });


        appointments.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appointmentB = isChecked;
            }

        });

        mListener.getBaseActivity().getJhiUsers().getLogedUser(user -> getUserData(user));
    }

    public void getUserData(RoomieUser user){
        roomieService.getCurrentRoomie();


    }

    public void onClickColones(View view) {
        activeButton(colones, dollars);
        currency = CurrencyType.COLON;
    }

    public void onClickDollars(View view) {
        activeButton(dollars, colones);
        currency = CurrencyType.DOLLAR;
    }

    public void onClickEspanol(View view) {
        activeButton(espanol, english);
         language = Lang.ESP;
    }

    public void onClickEnglish(View view) {
        activeButton(english, espanol);
        language = Lang.ENG;
    }

    public void activeButton(Button active, Button inactive) {
        active.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
        active.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        inactiveButton(inactive);
    }

    public void inactiveButton(Button inactive) {
        inactive.setBackgroundTintList(null);
        inactive.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomieService = new RoomieService(getContext(), this);
            configService = new ConfigurationService(getContext(),this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_configuration, container, false);
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
    public void onGetConfigurationSuccess(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        todo.setChecked(userPreferences.isTodoListNotifications());
        calendar.setChecked(userPreferences.isCalendarNotifications());
        appointments.setChecked(userPreferences.isAppointmentsNotifications());
        payment.setChecked(userPreferences.isPaymentsNotifications());

        if(userPreferences.getCurrency() == CurrencyType.COLON){
            activeButton(colones,dollars);
        }else{
            activeButton(dollars,colones);
        }


    }

    @Override
    public void onUpdateConfigurationSuccess(UserPreferences userPreferences) {
        ((BaseActivity) getContext()).showUserMessage("Configuration updated successfully!", BaseActivity.SnackMessageType.SUCCESS);
    }

    @Override
    public void onConfigurationError(String error) {
        ((BaseActivity) getContext()).showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        currentRoomie = roomie;
        configService.getConfigurationById(roomie.getConfigurationId());
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        ((BaseActivity) getContext()).showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    public void onClickSave(View view){
        userPreferences.setTodoListNotifications(todoB);
        userPreferences.setAppointmentsNotifications(appointmentB);
        userPreferences.setCalendarNotifications(calendarB);
        userPreferences.setPaymentsNotifications(paymentB);
        userPreferences.setCurrency(currency);

        configService.updateConfiguration(userPreferences);


    }


}
