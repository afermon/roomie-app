package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomExpenseSplit;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.service.RoomExpenseService;
import com.cosmicode.roomie.service.RoomExpenseSplitService;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Expense extends Fragment implements RoomExpenseSplitService.RoomExpenseSplitServiceListener {
    private OnFragmentInteractionListener mListener;
    private RoomExpenseSplitService roomExpenseSplitService;
    private static final String ROOM = "room";
    private static final String ROOMEXPENSE = "expense";
    private Room room;
    private String dateStart, dateEnd;
    private DatePickerDialog.OnDateSetListener mDateSetListenerStart,mDateSetListenerEnd;
    private RoomExpense roomExpense;

    @BindView(R.id.progress4)
    ProgressBar progressBar;


    Spinner expenseSpinner;
    @NotEmpty
    @Length(min = 4, max = 50)
    @BindView(R.id.expense_name_txt)
    TextView expenseName;

    @NotEmpty
    @BindView(R.id.expense_amount_txt)
    CurrencyEditText expenseAmount;

    @NotEmpty
    @BindView(R.id.expense_start_date_txt)
    TextView expenseStartDate;

    @NotEmpty
    @BindView(R.id.expense_end_date_txt)
    TextView expenseEndDate;


    @NotEmpty
    @Length(min = 4, max = 300)
    @BindView(R.id.expense_desc_text)
    TextView expenseDescription;

    @BindView(R.id.currency_radio)
    RadioGroup radioGroupCurrency;

    RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;
    private List<RoomExpenseSplit> roomExpenseSplitList;

    public Expense() {
        // Required empty public constructor
    }

    public static Expense newInstance(Room room, RoomExpense roomExpense) {
        Expense fragment = new Expense();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putParcelable(ROOM, room);
        args.putParcelable(ROOMEXPENSE, roomExpense);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomExpenseSplitService = new RoomExpenseSplitService(getContext(),this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.room = getArguments().getParcelable(ROOM);
        this.roomExpense = getArguments().getParcelable(ROOMEXPENSE);

        expenseSpinner = getView().findViewById(R.id.spinner_expense);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.numbersSpinnerExpense, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        expenseAmount.setDecimalDigits(0);
        if (roomExpense.getCurrency() != null) {
            if (roomExpense.getCurrency() == CurrencyType.COLON) {
                radioGroupCurrency.check(R.id.radio_crc);
                expenseAmount.setLocale(new Locale("es", "cr"));
            } else {
                radioGroupCurrency.check(R.id.radio_usd);
                expenseAmount.setLocale(Locale.US);
            }
        }

        radioGroupCurrency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_crc:
                        roomExpense.setCurrency(CurrencyType.COLON);
                        expenseAmount.setLocale(new Locale("es", "cr"));
                        break;
                    case R.id.radio_usd:
                        roomExpense.setCurrency(CurrencyType.DOLLAR);
                        expenseAmount.setLocale(Locale.US);
                        break;
                }
            }
        });

        mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month++;

                expenseStartDate.setText(dayOfMonth + "/" + month  + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if (month <= 9) {
                    monthS = "0" + month;
                }

                if (dayOfMonth <= 9) {
                    dayS = "0" + dayOfMonth;
                }
                dateStart = year + "-" + monthS + "-" + dayS;
            }
        };
//        Date end
        mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month++;

                expenseEndDate.setText(dayOfMonth + "/" + month  + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if (month <= 9) {
                    monthS = "0" + month;
                }

                if (dayOfMonth <= 9) {
                    dayS = "0" + dayOfMonth;
                }
                dateEnd = year + "-" + monthS + "-" + dayS;
            }
        };
        loadExpense();
        super.onViewCreated(view, savedInstanceState);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void loadExpense(){
        expenseName.setText(roomExpense.getName());
        expenseAmount.setValue(Math.round(roomExpense.getAmount()));
        expenseDescription.setText(roomExpense.getDesciption());
        expenseEndDate.setText(getUsableDate(roomExpense.getFinishDate()));
        expenseStartDate.setText(getUsableDate(roomExpense.getStartDate()));
        expenseSpinner.setSelection(getIndex());

        roomExpenseSplitService.getSplitExpensesByExxpense(roomExpense.getId());
        showProgress(true);
    }

    public int getIndex(){
        for (int i=0;i<expenseSpinner.getCount();i++){
            if (expenseSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(String.valueOf(roomExpense.getPeriodicity()))){
                return i;
            }
        }
        return 0;
    }
    public DateTime formatDate(String pdate){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = format.parseDateTime(pdate);
        return dt;
    }

    public String getUsableDate(String pDate){
        DateTime date = formatDate(pDate);

        int month, day;
        month = date.getMonthOfYear();
        day = date.getDayOfMonth();
        String monthS, dayS;
        monthS = Integer.toString(month);
        dayS = Integer.toString(day);
        RoomTask task;
        if(month <= 9){
            monthS = "0"+month;
        }
        if(day <= 9){
            dayS = "0"+day;
        }
        String deadline = dayS+"-"+monthS+"-"+ date.getYear();

        return deadline;
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnCreateRoomExpenseSplitSuccess(List<RoomExpenseSplit> roomExpenseSplitList) {
        this.roomExpenseSplitList = roomExpenseSplitList;
        showProgress(false);
    }

    @Override
    public void OnUpdateSuccess(RoomExpenseSplit roomExpenseSplit) {

    }

    @Override
    public void OnGetRoomExpenseSplitError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        showProgress(false);
    }

    @Override
    public void OnGetSplitExpenseByRoomSuccess(List<RoomExpenseSplit> body) {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        roomExpenseSplitList = body;
    }

    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
