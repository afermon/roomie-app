package com.cosmicode.roomie.view;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

public class Expense extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private static final String ROOMEXPENSE = "expense";
    private Room room;
    private RoomExpense roomExpense;

    @BindView(R.id.progress2)
    ProgressBar progressBar;;

    @BindView(R.id.amount_splitwise_txt)
    TextView splitwiseTxt;

    @BindView(R.id.AmoutSplit)
    RelativeLayout amountSplit;

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
    private List<RoomExpense> roomExpenseList;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listing_cost, container, false);

        ButterKnife.bind(this, view);

        return inflater.inflate(R.layout.fragment_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.room = getArguments().getParcelable(ROOM);
        this.roomExpense = getArguments().getParcelable(ROOMEXPENSE);

        super.onViewCreated(view, savedInstanceState);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
