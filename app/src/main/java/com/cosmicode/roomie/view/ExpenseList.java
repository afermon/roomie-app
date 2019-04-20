package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.service.RoomExpenseService;
import com.cosmicode.roomie.service.RoomieService;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

public class ExpenseList extends Fragment implements RoomExpenseService.RoomExpenseServiceListener, RoomieService.OnGetCurrentRoomieListener {

    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private Roomie currentRoomie;
    private Room room;
    private List<RoomExpense> roomExpenseList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private RoomExpenseService roomExpenseService;
    private RoomieService roomieService;

    ImageView addExpenseBtn;

    ProgressBar progressBar;

    public ExpenseList() {
        // Required empty public constructor
    }
    public static ExpenseList newInstance(Room room) {
        ExpenseList fragment = new ExpenseList();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomieService = new RoomieService(getContext(), this);
            roomExpenseService = new RoomExpenseService(getContext(),this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.room = getArguments().getParcelable(ROOM);

        addExpenseBtn = getView().findViewById(R.id.button_new_expense);
        addExpenseBtn.setOnClickListener(this::onClickAdd);
        progressBar = getView().findViewById(R.id.progress3);

        recyclerView = getView().findViewById(R.id.expense_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        getRoomie();
        super.onViewCreated(view, savedInstanceState);
    }

    public void getRoomie(){
        showProgress(true);
        roomieService.getCurrentRoomie();
    }
    public void getRooms(){
        roomExpenseService.getAllExpensesByRoom(room.getId());

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClickAdd(View view){

        NewExpenseFragment todoFragment = NewExpenseFragment.newInstance(room);
        openFragment(todoFragment);
    }

    @Override
    public void OnGetExpenseByRoomSuccess(List<RoomExpense> roomTasks) {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(roomTasks);
        recyclerView.setAdapter(mAdapter);
        showProgress(false);
    }

    @Override
    public void OnCreateExpenseSuccess(RoomExpense roomExpense) {

    }

    @Override
    public void OnUpdateSuccess(RoomExpense roomExpense) {

    }

    @Override
    public void OnDeleteSuccess() {

    }

    @Override
    public void OnGetExpenseRoomError(String error) {
        showProgress(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        currentRoomie = roomie;

        if (room.getOwnerId() == roomie.getId()){
            addExpenseBtn.setVisibility(View.VISIBLE);
        }
        getRooms();
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        showProgress(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
    public class MyAdapter extends RecyclerView.Adapter<ExpenseList.MyAdapter.ExpenseViewHolder>{

        private List<RoomExpense> expenseList;

        public class ExpenseViewHolder extends  RecyclerView.ViewHolder {
            private TextView expenseName;
            private TextView endDate;
            private TextView description;
            private CardView cardView;
            private CurrencyEditText typeAmount;
            ExpenseViewHolder(View view){
                super(view);
                typeAmount = view.findViewById(R.id.txt_expense_list_amount_type);
                expenseName = view.findViewById(R.id.expense_name);
                endDate = view.findViewById(R.id.expense_date_list);
                description = view.findViewById(R.id.txt_expenselist_desc);
                cardView = view.findViewById(R.id.expense_item);
            }

        }
        public MyAdapter(List<RoomExpense> proomExpenseList) {
            this.expenseList = proomExpenseList;
        }

        public int getItemCount() {
            return expenseList.size();
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expense_item, viewGroup, false);

            ExpenseViewHolder vh = new ExpenseViewHolder(v);

            return vh;
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

        @Override
        public void onBindViewHolder(final ExpenseViewHolder holder, int position) {
            RoomExpense e = this.expenseList.get(position);

            if(e.getName() !=null){
                holder.expenseName.setText(e.getName());
            }

            if (e.getDesciption() != null){
                holder.description.setText(e.getDesciption());
            }
            holder.endDate.setText(getUsableDate(e.getFinishDate()));
            holder.typeAmount.setText(String.valueOf(e.getAmount()));
            holder.typeAmount.setEnabled(false);
            if (e.getCurrency() == CurrencyType.COLON) {
                holder.typeAmount.setLocale(new Locale("es", "cr"));
            } else {
                holder.typeAmount.setLocale(Locale.US);
            }

            holder.cardView.setOnClickListener( v -> {
                Expense todoFragment = Expense.newInstance(room, e, currentRoomie);
                openFragment(todoFragment);
            });

        }
    }
}
