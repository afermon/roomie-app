package com.cosmicode.roomie.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.Roomie;

public class ExpenseManager extends BaseActivity implements NewExpenseFragment.OnFragmentInteractionListener, Expense.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_manager);
        Room room = getIntent().getParcelableExtra("room");
        RoomExpense expense = getIntent().getParcelableExtra("expense");
        Roomie roomie = getIntent().getParcelableExtra("roomie");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(expense == null){
            transaction.replace(R.id.expense_container, NewExpenseFragment.newInstance(room));
        }else{
            transaction.replace(R.id.expense_container, Expense.newInstance(room, expense, roomie));
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
