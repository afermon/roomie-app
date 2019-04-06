package com.cosmicode.roomie.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.blackcat.currencyedittext.CurrencyEditText;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ListingCost extends Fragment implements Validator.ValidationListener {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String ROOM = "room";
    private RoomCreate room;
    private String date, date2;
    private OnFragmentInteractionListener mListener;
    private static int selectedDate;
    private RoomExpense roomExpense;
    private static final String COST = "cost";


    @BindView(R.id.error_text)
    TextView error;
    @NotEmpty
    @BindView(R.id.edit_amount)
    CurrencyEditText amount;
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


    public ListingCost() {
    }


    public static ListingCost newInstance(RoomCreate room) {
        ListingCost fragment = new ListingCost();
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
            if (room.getMonthly() == null) {
                roomExpense = new RoomExpense();
                roomExpense.setCurrency(CurrencyType.COLON);
                room.setMonthly(roomExpense);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_cost, container, false);
        mListener.changePercentage(50);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        amount.setDecimalDigits(0);
        if (room.getMonthly().getCurrency() != null) {
            if (room.getMonthly().getCurrency() == CurrencyType.COLON) {
                currency.check(R.id.radio_crc);
                amount.setLocale(new Locale("es", "cr"));
            } else {
                currency.check(R.id.radio_usd);
                amount.setLocale(Locale.US);
            }
        }

        if (room.getMonthly().getAmount() != null) {
            amount.setValue(room.getMonthly().getAmount().longValue());
        }

        setDate();
        currency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mListener.hideKeyboard();
                    switch (checkedId) {
                    case R.id.radio_crc:
                        room.getMonthly().setCurrency(CurrencyType.COLON);
                        amount.setLocale(new Locale("es", "cr"));
                        break;
                    case R.id.radio_usd:
                        room.getMonthly().setCurrency(CurrencyType.DOLLAR);
                        amount.setLocale(Locale.US);
                        break;
                }
            }
        });
    }

    @OnClick(R.id.cancel_cost)
    public void finish(View view) {
        getActivity().finish();
    }

    @OnClick(R.id.back_cost)
    public void back(View view) {
        saveState();
        mListener.openFragment(ListingBasicInformation.newInstance(room), "left");
    }

    @OnClick(R.id.btn_next)
    public void onClickNext(View view) {
        saveState();
        mListener.openFragment(ListingChoosePictures.newInstance(room), "right");
    }

    private void saveState() {
        room.getMonthly().setName("Monthly rent");
        room.getMonthly().setAmount((double) amount.getRawValue());
        room.getMonthly().setPeriodicity(30);
        room.getMonthly().setMonthDay(1);
        room.getMonthly().setStartDate(date);
        room.setAvailableFrom(date);
        if (!date2.equals("")) {
            room.getMonthly().setFinishDate(date2);
        } else {
            room.getMonthly().setFinishDate(null);
        }
    }

    @OnClick(R.id.date_picker)
    public void onClickDate(View view) {
        mListener.hideKeyboard();
        error.setVisibility(View.GONE);
        dateText.setError(null);
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());

        selectedDate = 0;
        dialog.show();
    }

    @OnClick(R.id.date_picker2)
    public void onClickDate2(View view) {
        mListener.hideKeyboard();
        error.setVisibility(View.GONE);
        dateText2.setError(null);
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateText2.setText(getString(R.string.no_move_out));
                date2 = "";
            }
        });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(COST, amount.getText().toString());
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();

        void openFragment(Fragment fragment, String start);

        void changePercentage(int progress);

        void hideKeyboard();
    }

    public void setDate() {
        DateTime x;
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime start, finish;

        if (room.getMonthly().getStartDate() != null) {
            start = dateTimeFormatter.parseDateTime(room.getMonthly().getStartDate());
            x = start;
        } else {
            x = new DateTime();
        }

        if (room.getMonthly().getFinishDate() != null) {
            finish = dateTimeFormatter.parseDateTime(room.getMonthly().getFinishDate());
            date2 = getDateString(finish);
            dateText2.setText(String.format("%s/%s/%s", finish.getDayOfMonth(), finish.getMonthOfYear(), finish.getYear()));
        } else {
            date2 = "";
            dateText2.setText(getString(R.string.no_move_out));
        }

        dateText.setText(String.format("%s/%s/%s", x.getDayOfMonth(), x.getMonthOfYear(), x.getYear()));
        date = getDateString(x);


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

                if (selectedDate == 0) {
                    dateText.setText(String.format("%s/%s/%s", dayOfMonth, month, year));
                    date = year + "-" + monthS + "-" + dayS;
                } else {
                    date2 = year + "-" + monthS + "-" + dayS;

                    DateTime startCompare, finishCompare;
                    startCompare = dateTimeFormatter.parseDateTime(date);
                    finishCompare = dateTimeFormatter.parseDateTime(date2);

                    if (finishCompare.isBefore(startCompare)) {
                        error.setVisibility(View.VISIBLE);
                        dateText2.setText(getString(R.string.no_move_out));
                        date2 = "";
                    } else {
                        dateText2.setText(String.format("%s/%s/%s", dayOfMonth, month, year));
                    }

                }
            }
        };
    }

    private String getDateString(DateTime date) {
        String monthS, dayS;
        monthS = Integer.toString(date.getMonthOfYear());
        dayS = Integer.toString(date.getDayOfMonth());
        if (date.getMonthOfYear() <= 9) {
            monthS = "0" + date.getMonthOfYear();
        }

        if (date.getDayOfMonth() <= 9) {
            dayS = "0" + date.getDayOfMonth();
        }

        return date.getYear() + "-" + monthS + "-" + dayS;
    }
}
