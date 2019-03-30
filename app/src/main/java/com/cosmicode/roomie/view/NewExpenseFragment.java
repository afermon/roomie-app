package com.cosmicode.roomie.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;

import java.util.List;

public class NewExpenseFragment extends Fragment implements  Validator.ValidationListener  {

    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private Room room;
    private Validator validator;
    private boolean isValid = true;
    private ProgressBar progressBar;
    private String dateStart, dateEnd;
    private DatePickerDialog.OnDateSetListener mDateSetListenerStart,mDateSetListenerEnd;
    private int step = 0;


    @NotEmpty
    @Length(min = 4, max = 50)
    @BindView(R.id.expense_name_txt)
    TextView expenseName;

    @NotEmpty
    @BindView(R.id.expense_amount_txt)
    TextView expenseAmount;

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

    @BindView(R.id.spinner_expense)
    Spinner expenseSpinner;

    @BindView(R.id.create_expense_btn)
    Button createExpenseBtn;

    @BindView(R.id.add_person_recycler)
    RecyclerView recyclerView;


    public NewExpenseFragment() {
        // Required empty public constructor
    }

    public static NewExpenseFragment newInstance() {
        NewExpenseFragment fragment = new NewExpenseFragment();
        Bundle args = new Bundle();
//        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            this.room = getArguments().getParcelable(ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create_expense, container, false);
        ButterKnife.bind(this, view);
        validator = new Validator(this);
        validator.setValidationListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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
        super.onViewCreated(view, savedInstanceState);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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

    @OnClick(R.id.expense_date_picker_start)
    public void onClickDateStart(View view) {
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListenerStart, max.getYear(), max.getMonthOfYear()-1, max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
        dialog.show();
    }

    @OnClick(R.id.expense_date_picker_end)
    public void onClickDateEnd(View view) {
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListenerEnd, max.getYear(), max.getMonthOfYear()-1, max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
        dialog.show();
    }

    @OnClick(R.id.create_expense_btn)
    public void onClickCreateTask(View view){
        switch (step){
            case 0:
                validator.validate();
                break;
            case 1:
                break;
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
