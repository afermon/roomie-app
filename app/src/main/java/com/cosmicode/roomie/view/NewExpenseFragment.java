package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomExpenseSplit;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.service.RoomExpenseService;
import com.cosmicode.roomie.service.RoomExpenseSplitService;
import com.google.android.gms.maps.model.Circle;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewExpenseFragment extends Fragment implements  Validator.ValidationListener, RoomExpenseService.RoomExpenseServiceListener, RoomExpenseSplitService.RoomExpenseSplitServiceListener {
    private RoomExpenseService roomExpenseService;
    private RoomExpenseSplitService roomExpenseSplitService;
    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private Room room;
    private Validator validator;
    private boolean isValid = true;
    private String dateStart, dateEnd;
    private RoomExpense roomExpense, roomExpenseCreated;
    private DatePickerDialog.OnDateSetListener mDateSetListenerStart,mDateSetListenerEnd;
    private int step = 0;
    private DateTimeComparator comparator;
    private RecyclerView.LayoutManager layoutManager;
    private List<Roomie> selectedRoomies;
    @BindView(R.id.main_info_expense)
    ScrollView mainInfoView;

    @BindView(R.id.add_person_expense)
    ScrollView addPersonView;

    @BindView(R.id.amount_splitwise_txt)
    TextView splitwiseTxt;

    @BindView(R.id.AmoutSplit)
    RelativeLayout amountSplit;

    @BindView(R.id.progress2)
    ProgressBar progressBar;;

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

    @BindView(R.id.create_expense_btn)
    Button createExpenseBtn;

    RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;

    Spinner expenseSpinner;

    List<Roomie> roomies;

    public NewExpenseFragment() {
        // Required empty public constructor
    }

    public static NewExpenseFragment newInstance(Room room) {
        NewExpenseFragment fragment = new NewExpenseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomExpense = new RoomExpense();
            roomExpenseService = new RoomExpenseService(getContext(),this);
            roomExpenseSplitService = new RoomExpenseSplitService(getContext(),this);
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

        expenseSpinner = getView().findViewById(R.id.spinner_expense);
        this.room = getArguments().getParcelable(ROOM);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.numbersSpinnerExpense, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseSpinner.setAdapter(spinnerAdapter);
        selectedRoomies = new ArrayList<>();
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

        recyclerView = getView().findViewById(R.id.add_person_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(room.getRoomies());

        recyclerView.setAdapter(mAdapter);

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

        radioGroupCurrency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValidationSucceeded() {
        if(isValid){
            roomExpense.setAmount((double) expenseAmount.getRawValue());
            roomExpense.setDesciption(expenseDescription.getText().toString());
            roomExpense.setFinishDate(dateEnd+"T00:00:00Z");
            roomExpense.setStartDate(dateStart+"T00:00:00Z");
            roomExpense.setName(expenseName.getText().toString());
            roomExpense.setRoomId(Long.parseLong("1"));
            roomExpense.setMonthDay(1);
            roomExpense.setPeriodicity(Integer.valueOf(expenseSpinner.getSelectedItem().toString()));
            roomExpenseService.createExpense(roomExpense);
        }else{
            showProgress(false);
        }

    }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        showProgress(false);
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
                showProgress(true);
                if(expenseStartDate.getText().toString().equals("")){
                    expenseStartDate.setError("Please choose a date");
                    isValid = false;
                }else{
                    isValid = true;
                }


                if(expenseEndDate.getText().toString().equals("")){
                    expenseEndDate.setError("Please choose a date");
                    isValid = false;
                }else{
                    isValid = true;
                }

                if (expenseAmount.getText().toString().equals("0")){
                    expenseAmount.setError("Can not be 0.00 or less");
                    isValid = false;
                }else{
                    isValid = true;
                }

//                if(dateEnd==null && dateStart==null){
//                    DateTime dEnd,dStart;
//                    dEnd = formatDate(dateEnd+"T00:00:00Z");
//                    dStart = formatDate(dateStart+"T00:00:00Z");
//                    int difday = comparator.compare(dEnd, dStart);
//                    if(difday<0){
//                        isValid = false;
//                        Toast.makeText(getContext(), "End date can't be older than the start date of the expense", Toast.LENGTH_SHORT).show();
//                    }else{
//                        isValid = true;
//                    }
//                }

                validator.validate();
                break;
            case 1:
                Toast.makeText(getContext(), Integer.toString(selectedRoomies.size()), Toast.LENGTH_LONG).show();
                List<RoomExpenseSplit> roomExpenseSplitLIst = new ArrayList<RoomExpenseSplit>();

                for (Roomie r: selectedRoomies){
//                    Long id, Double amount, Long expenseId, Long roomieId
                    RoomExpenseSplit expenseSplit = new RoomExpenseSplit();
                    expenseSplit.setAmount(Double.parseDouble(splitwiseTxt.getText().toString()));
                    expenseSplit.setRoomieId(r.getId());
                    expenseSplit.setExpenseId(roomExpenseCreated.getId());
                    roomExpenseSplitLIst.add(expenseSplit);
                }

                roomExpenseSplitService.createExpense(roomExpenseSplitLIst);
                showProgress(true);
                break;
        }
    }

    public DateTime formatDate(String pdate){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = format.parseDateTime(pdate);
        return dt;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void OnGetExpenseByRoomSuccess(List<RoomExpense> roomTasks) {

    }

    @Override
    public void OnCreateExpenseSuccess(RoomExpense roomExpense) {
        showProgress(false);
        roomExpenseCreated = roomExpense;
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        step = 1;
        mainInfoView.setVisibility(View.GONE);
        addPersonView.setVisibility(View.VISIBLE);
        amountSplit.setVisibility(View.VISIBLE);
        createExpenseBtn.setText(R.string.add_person_split);
    }

    @Override
    public void OnUpdateSuccess(RoomExpense roomExpense) {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnGetExpenseRoomError(String error) {

    }

    public void updateAmountSplit(){
//        double total= roomExpenseCreated.getAmount()/selectedRoomies.size();
        double total=0;
        if(selectedRoomies.size()>0){
           total = 10000/selectedRoomies.size();
        }
        splitwiseTxt.setText(String.valueOf(total));
    }

    @Override
    public void OnCreateRoomExpenseSplitSuccess(List<RoomExpenseSplit> roomExpenseSplit) {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void OnUpdateSuccess(RoomExpenseSplit roomExpenseSplit) {

    }

    @Override
    public void OnGetRoomExpenseSplitError(String error) {

    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
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

    //    -----------------------------------------Recycler-------------------------------------------
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.RoomieViewHolder>{

        private List<Roomie> roomieList;
        public class RoomieViewHolder extends  RecyclerView.ViewHolder {
            private CardView cardView;
            private TextView txtPersonName;
            private CircleImageView pfp;
            private boolean selected;
            RoomieViewHolder(View view){
                super(view);
                cardView = view.findViewById(R.id.card_view_add_person);
                txtPersonName = view.findViewById(R.id.add_person_name_txt);
                pfp = view.findViewById(R.id.profile_image2);
                selected = false;
            }

        }
        public MyAdapter(List<Roomie> proomieList) {
            this.roomieList = proomieList;
//            this.mContext = mContext;
        }

        public int getItemCount() {
            return roomieList.size();
        }

        @Override
        public RoomieViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_person_item, viewGroup, false);

            RoomieViewHolder vh = new RoomieViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(final RoomieViewHolder holder, int position) {
            Roomie roomie = this.roomieList.get(position);
            holder.txtPersonName.setText(roomie.getPhone());
            Glide.with(getContext()).load(roomie.getPicture()).centerCrop().into(holder.pfp);

            holder.cardView.setOnClickListener( v -> {
                if(selectedRoomies.contains(roomie)==false) {
                    holder.cardView.setCardBackgroundColor(Color.LTGRAY);
                    selectedRoomies.add(roomie);
                    updateAmountSplit();
                }else{
                    holder.cardView.setCardBackgroundColor(Color.WHITE);
                    selectedRoomies.remove(roomie);
                    updateAmountSplit();
                }
            });

        }
    }
}



