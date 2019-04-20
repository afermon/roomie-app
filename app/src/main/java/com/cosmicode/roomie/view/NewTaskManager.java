package com.cosmicode.roomie.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomTask;

public class NewTaskManager extends BaseActivity implements NewTaskFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_manager);
        RoomTask roomTask = getIntent().getParcelableExtra("task");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.task_container, NewTaskFragment.newInstance(roomTask, getIntent().getLongExtra("room", 0)));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }
}
