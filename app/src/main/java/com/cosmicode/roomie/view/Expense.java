package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomExpenseSplit;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.service.RoomExpenseService;
import com.cosmicode.roomie.service.RoomExpenseSplitService;
import com.cosmicode.roomie.service.RoomieService;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Expense extends Fragment implements RoomExpenseSplitService.RoomExpenseSplitServiceListener, Validator.ValidationListener, RoomExpenseService.RoomExpenseServiceListener{
    private OnFragmentInteractionListener mListener;
    private RoomExpenseSplitService roomExpenseSplitService;
    private RoomExpenseService roomExpenseService;
    private static final String ROOM = "room";
    private static final String ROOMEXPENSE = "expense";
    private static final String ROOMIE = "roomie";
    private Room room;
    private String dateStart, dateEnd;
    private DatePickerDialog.OnDateSetListener mDateSetListenerStart,mDateSetListenerEnd;
    private RoomExpense roomExpense;
    private Roomie currentRoomie;
    private List<Roomie> selectedRoomies, listCreateNewExpense;
    private List<RoomExpenseSplit> roomExpenseSplitList, newRoomExpenseSplitList;
    private Validator validator;
    private GridLayoutManager layoutManager;
    private boolean isValid = true;
    private RecyclerView.Adapter mAdapter;
    private boolean editEnable = false;


    @BindView(R.id.progress4)
    ProgressBar progressBar;

    @BindView(R.id.edit_btn)
    ImageButton editBtn;

    @BindView(R.id.edit_expense)
    Button editExpense;

    @BindView(R.id.delete_expense_btn)
    ImageButton deleteBtn;

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

    @BindView(R.id.radio_crc)
    RadioButton radioButtonCrc;

    @BindView(R.id.radio_usd)
    RadioButton radioButtonUsd;

    RecyclerView recyclerView;

    @BindView(R.id.empty_list)
    TextView roomieTxt;

    public Expense() {
        // Required empty public constructor
    }

    public static Expense newInstance(Room room, RoomExpense roomExpense, Roomie roomie) {
        Expense fragment = new Expense();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putParcelable(ROOMIE, roomie);
        args.putParcelable(ROOM, room);
        args.putParcelable(ROOMEXPENSE, roomExpense);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomExpenseSplitService = new RoomExpenseSplitService(getContext(),this);
            roomExpenseService = new RoomExpenseService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        ButterKnife.bind(this, view);
        validator = new Validator(this);
        validator.setValidationListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.room = getArguments().getParcelable(ROOM);
        this.roomExpense = getArguments().getParcelable(ROOMEXPENSE);
        this.currentRoomie = getArguments().getParcelable(ROOMIE);
        this.selectedRoomies = new ArrayList<>();
        listCreateNewExpense = new ArrayList<>();
        newRoomExpenseSplitList = new ArrayList<>();

        expenseSpinner = getView().findViewById(R.id.spinner_expense);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.numbersSpinnerExpense, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseSpinner.setAdapter(spinnerAdapter);

        loadExpense();

        recyclerView = getView().findViewById(R.id.expense_roomies);
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

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
        super.onViewCreated(view, savedInstanceState);
    }

    private void  getSelectedRoomies(List<RoomExpenseSplit> list) {
        if(list != null){
            for (int k=0; k<list.size() ;k++){
                for (int i=0; i<room.getRoomies().size(); i++){
                    if (room.getRoomies().get(i).getId() == list.get(k).getRoomieId()){
                        selectedRoomies.add(room.getRoomies().get(i));
                    }
                }
            }
            mAdapter = new Expense.MyAdapter(selectedRoomies);
            recyclerView.setAdapter(mAdapter);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void loadExpense(){
        if (room.getOwnerId() == currentRoomie.getId()){
            editBtn.setVisibility(View.VISIBLE);
        }
        expenseName.setText(roomExpense.getName());
        expenseAmount.setValue(Math.round(roomExpense.getAmount()));
        expenseDescription.setText(roomExpense.getDesciption());
        expenseEndDate.setText(getUsableDate(roomExpense.getFinishDate()));
        expenseStartDate.setText(getUsableDate(roomExpense.getStartDate()));
        expenseSpinner.setSelection(getIndex());
        roomExpenseSplitService.getSplitExpensesByExxpense(roomExpense.getId());
        disableTextView();
        showProgress(true);
    }

    public int getIndex(){
        for (int i=0;i<expenseSpinner.getCount();i++){
            if (expenseSpinner.getItemAtPosition(i).toString().equals(String.valueOf(roomExpense.getPeriodicity()))){
                return i;
            }
        }
        return 0;
    }

    public void disableTextView(){
        if (editEnable ==false){
            expenseName.setEnabled(false);
            expenseAmount.setEnabled(false);
            expenseDescription.setEnabled(false);
            expenseEndDate.setEnabled(false);
            expenseStartDate.setEnabled(false);
            expenseSpinner.setEnabled(false);
            radioButtonCrc.setClickable(false);
            radioButtonUsd.setClickable(false);
        }else{
            expenseName.setEnabled(true);
            expenseAmount.setEnabled(true);
            expenseDescription.setEnabled(true);
            expenseEndDate.setEnabled(true);
            expenseStartDate.setEnabled(true);
            expenseSpinner.setEnabled(true);
            radioButtonCrc.setClickable(true);
            radioButtonUsd.setClickable(true);
        }
    }

    @OnClick(R.id.edit_btn)
    public void onClickEditBtn(){
        editBtn.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.VISIBLE);
        editExpense.setVisibility(View.VISIBLE);
        editExpense.setText(R.string.edit);
        editEnable = true;

        RecyclerView.Adapter mAdapter2 = new Expense.MyAdapter(room.getRoomies());
        recyclerView.setAdapter(mAdapter2);
        roomieTxt.setVisibility(View.GONE);

        for (int i=0; i<selectedRoomies.size();i++){
            listCreateNewExpense.add(selectedRoomies.get(i));
        }
        disableTextView();
    }

    @OnClick(R.id.expense_date_picker_start)
    public void onClickDateStart(View view) {
        if (editEnable) {
            DateTime max = new DateTime();
            DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListenerStart, max.getYear(), max.getMonthOfYear() - 1, max.getDayOfMonth());
            dialog.getDatePicker().setMinDate(max.getMillis());
            dialog.show();
        }
    }

    @OnClick(R.id.expense_date_picker_end)
    public void onClickDateEnd(View view) {
        if (editEnable){
            DateTime max = new DateTime();
            DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListenerEnd, max.getYear(), max.getMonthOfYear()-1, max.getDayOfMonth());
            dialog.getDatePicker().setMinDate(max.getMillis());
            dialog.show();
        }
    }


    @OnClick(R.id.delete_expense_btn)
    public void onClickDelete(){
        roomExpenseService.deleteExpense(roomExpense.getId());
        showProgress(true);
    }

    @OnClick(R.id.back_edit_expense_btn)
    public void goBack(){
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.edit_expense)
    public void OnClickEditExpense(){
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

        isValid = validateDates();

        validator.validate();

        showProgress(true);
    }

    public boolean validateDates(){
        int dayDif = Days.daysBetween(formatDatefromTxt(expenseStartDate.getText().toString()), formatDatefromTxt(expenseEndDate.getText().toString())).getDays();
        int totalDays = Integer.parseInt(expenseSpinner.getSelectedItem().toString())*7;
        double remainder = dayDif% totalDays;
        if (dayDif!=0 && totalDays!=0){
            if(remainder == 0){

                return true;

            }else{
                Toast.makeText(getContext(), R.string.no_valid_date , Toast.LENGTH_SHORT).show();
                expenseEndDate.setError("Incorrect date");
                showProgress(false);
                return false;
            }
        }else {
            Toast.makeText(getContext(), R.string.no_valid_date , Toast.LENGTH_SHORT).show();
            expenseEndDate.setError("Incorrect date");
            showProgress(false);
            return false;
        }

    }
    public DateTime formatDatefromTxt(String pdate){
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = format.parseDateTime(pdate);
        return dt;
    }
    public Double splitAmount(){
        Double result =  roomExpense.getAmount()/listCreateNewExpense.size();
        return  result;
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
        Toast.makeText(getContext(), "Update success", Toast.LENGTH_SHORT).show();
        showProgress(false);
    }

    @Override
    public void OnGetRoomExpenseSplitError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        showProgress(false);
    }

    @Override
    public void OnDeleteExpenseSplitSuccess() {
        List<RoomExpenseSplit> roomExpenseSplitLIst = new ArrayList<RoomExpenseSplit>();

        for (Roomie r: listCreateNewExpense){
            RoomExpenseSplit expenseSplit = new RoomExpenseSplit();
            expenseSplit.setAmount(splitAmount());
            expenseSplit.setRoomieId(r.getId());
            expenseSplit.setExpenseId(roomExpense.getId());
            roomExpenseSplitLIst.add(expenseSplit);
        }

        roomExpenseSplitService.createExpenseSplit(roomExpenseSplitLIst);
    }

    @Override
    public void OnGetSplitExpenseByRoomSuccess(List<RoomExpenseSplit> body) {

        roomExpenseSplitList = body;
        if (body.size() > 0) {
            getSelectedRoomies(roomExpenseSplitList);
        }else {
            roomieTxt.setVisibility(View.VISIBLE);
        }
        showProgress(false);
    }

    @Override
    public void OnGetExpenseByRoomSuccess(List<RoomExpense> roomTasks) {

    }

    @Override
    public void OnCreateExpenseSuccess(RoomExpense roomExpense) {

    }

    @Override
    public void OnUpdateSuccess(RoomExpense roomExpense) {
        if (roomExpenseSplitList.size()>0){
            roomExpenseSplitService.deleteExpenseSplit(roomExpenseSplitList);
        }else {
            for (Roomie r: listCreateNewExpense){
                RoomExpenseSplit expenseSplit = new RoomExpenseSplit();
                expenseSplit.setAmount(splitAmount());
                expenseSplit.setRoomieId(r.getId());
                expenseSplit.setExpenseId(roomExpense.getId());
                newRoomExpenseSplitList.add(expenseSplit);
            }

            roomExpenseSplitService.createExpenseSplit(newRoomExpenseSplitList);
        }

    }

    @Override
    public void OnDeleteSuccess() {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void OnGetExpenseRoomError(String error) {

    }

    @Override
    public void onValidationSucceeded() {
        if(isValid){
            roomExpense.setAmount((double) expenseAmount.getRawValue());
            roomExpense.setDesciption(expenseDescription.getText().toString());
            roomExpense.setFinishDate(getUsableDateForServer(expenseEndDate.getText().toString())+"T00:00:00Z");
            roomExpense.setStartDate(getUsableDateForServer(expenseStartDate.getText().toString())+"T00:00:00Z");
            roomExpense.setName(expenseName.getText().toString());
            roomExpense.setRoomId(Long.parseLong("1"));
            roomExpense.setMonthDay(1);
            roomExpense.setPeriodicity(Integer.valueOf(expenseSpinner.getSelectedItem().toString()));
            roomExpenseService.updateExpense(roomExpense);
        }else{
            showProgress(false);
        }
//
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
        String deadline = dayS+"/"+monthS+"/"+ date.getYear();

        return deadline;
    }


    public DateTime formatDate2(String pdate){
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = format.parseDateTime(pdate);
        return dt;
    }
    public String getUsableDateForServer(String pDate){
        DateTime date = formatDate2(pDate);

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
        String deadline = date.getYear()+"-"+monthS+"-"+dayS;

        return deadline;
    }

    //    -----------------------------------------Recycler-------------------------------------------
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.RoomieViewHolder>{

        private List<Roomie> roomieList;
        public class RoomieViewHolder extends  RecyclerView.ViewHolder {
            private CardView cardView;
            private CircleImageView pfp;
            RoomieViewHolder(View view){
                super(view);
                cardView = view.findViewById(R.id.card_view_add_person);
                pfp = view.findViewById(R.id.profile_image2);
            }

        }
        public MyAdapter(List<Roomie> proomieList) {
            this.roomieList = proomieList;
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
            Glide.with(getContext()).load(roomie.getPicture()).centerCrop().into(holder.pfp);

            if (editEnable == true){

                if (selectedRoomies.contains(roomie)==true) holder.cardView.setCardBackgroundColor(Color.LTGRAY);

                holder.cardView.setOnClickListener( v -> {
                    if (listCreateNewExpense.contains(roomie)==false){
                        holder.cardView.setCardBackgroundColor(Color.LTGRAY);
                        listCreateNewExpense.add(roomie);
                    }else{
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        listCreateNewExpense.remove(roomie);
                    }
                });
            }

        }
    }
}
