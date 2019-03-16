package com.cosmicode.roomie.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Select;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class ListingStep2 extends Fragment implements Validator.ValidationListener {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String ROOM = "room";
    private Room room;
    private String date;
    private OnFragmentInteractionListener mListener;
    private Validator validator;

    @NotEmpty
    @Length(min = 4, max = 100)
    @BindView(R.id.edit_headline)
    EditText headline;

    @NotEmpty
    @Length(min = 4, max = 2000)
    @BindView(R.id.edit_desc)
    EditText desc;

    @NotEmpty
    @BindView(R.id.edit_amount)
    EditText amount;

    @Select
    @BindView(R.id.currency_spinner)
    Spinner currency;

    @NotEmpty
    @BindView(R.id.edit_date)
    TextView dateText;

    @BindView(R.id.date_picker)
    ImageButton datePicker;


    public ListingStep2() {
    }


    public static ListingStep2 newInstance(Room room) {
        ListingStep2 fragment = new ListingStep2();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listing_step2, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(getActivity());
        validator = new Validator(this);
        validator.setValidationListener(this);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month++;
                dateText.setText(dayOfMonth + "/" + month  + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if (month <= 9) {
                    monthS = "0" + month;
                }

                if (dayOfMonth <= 9) {
                    dayS = "0" + dayOfMonth;
                }
                date = year + "-" + monthS + "-" + dayS;
            }
        };
    }

    @OnClick(R.id.date_picker)
    public void onClickDate(View view) {
        dateText.setError(null);
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
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

    public void fillSpinner(){
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("CRC");
        spinnerArray.add("USD");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(adapter);
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
        void onFragmentInteraction(Uri uri);
    }
}
