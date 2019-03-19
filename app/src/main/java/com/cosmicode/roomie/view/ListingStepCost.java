package com.cosmicode.roomie.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;

import java.util.List;


public class ListingStepCost extends Fragment implements Validator.ValidationListener {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String ROOM = "room";
    private RoomCreate room;
    private String date, date2;
    private OnFragmentInteractionListener mListener;
    private Validator validator;
    private static int selectedDate;
    private RoomExpense roomExpense;

    @NotEmpty
    @BindView(R.id.edit_amount)
    EditText amount;

    @NotEmpty
    @BindView(R.id.movein_date)
    TextView dateText;

    @NotEmpty
    @BindView(R.id.moveout_date)
    TextView dateText2;

    @BindView(R.id.date_picker)
    ImageButton datePicker;


    @BindView(R.id.date_picker2)
    ImageButton datePicker2;

    @BindView(R.id.currency_radio)
    RadioGroup currency;

    @BindView(R.id.btn_next)
    Button next;


    public ListingStepCost() {
    }


    public static ListingStepCost newInstance(RoomCreate room) {
        ListingStepCost fragment = new ListingStepCost();
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
            roomExpense = new RoomExpense();
            roomExpense.setCurrency(CurrencyType.DOLLAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_step2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date2 = "";
        validator = new Validator(this);
        validator.setValidationListener(this);
        DateTime x = new DateTime();
        dateText.setText(x.getDayOfMonth() + "/" + x.getMonthOfYear() + "/" +x.getYear());
        String monthS, dayS;
        monthS = Integer.toString(x.getMonthOfYear());
        dayS = Integer.toString(x.getDayOfMonth());
        if (x.getMonthOfYear() <= 9) {
            monthS = "0" + x.getMonthOfYear();
        }

        if (x.getDayOfMonth() <= 9) {
            dayS = "0" + x.getDayOfMonth();
        }
        date = x.getYear() + "-" + monthS + "-" + dayS;
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month++;
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if (month <= 9) {
                    monthS = "0" + month;
                }

                if (dayOfMonth <= 9) {
                    dayS = "0" + dayOfMonth;
                }

                if(selectedDate == 0){
                    dateText.setText(dayOfMonth + "/" + month  + "/" + year);
                    date = year + "-" + monthS + "-" + dayS;
                }else{
                    dateText2.setText(dayOfMonth + "/" + month  + "/" + year);
                    date2 = year + "-" + monthS + "-" + dayS;
                }
            }
        };

        currency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_crc:
                        roomExpense.setCurrency(CurrencyType.COLON);
                        break;
                    case R.id.radio_usd:
                        roomExpense.setCurrency(CurrencyType.DOLLAR);
                        break;
                }
            }
        });
    }

    @OnClick(R.id.btn_next)
    public void onClickNext(View view){
        roomExpense.setName("Monthly rent");
        String newStr = amount.getText().toString().replaceAll("[,]", "");
        roomExpense.setAmount(Double.parseDouble(newStr));
        roomExpense.setPeriodicity(30);
        roomExpense.setMonthDay(1);
        roomExpense.setStartDate(date);
        room.setAvailableFrom(date);
        if(!date2.equals("")){
            roomExpense.setFinishDate(date2);
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
        transaction.replace(R.id.listing_container, ListingChoosePictures.newInstance(room, roomExpense) );
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.date_picker)
    public void onClickDate(View view) {
        dateText.setError(null);
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
        selectedDate = 0;
        dialog.show();
    }

    @OnClick(R.id.date_picker2)
    public void onClickDate2(View view) {
        dateText2.setError(null);
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
        selectedDate = 1;
        dialog.show();
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
    public void onValidationSucceeded() {

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
    }
}
