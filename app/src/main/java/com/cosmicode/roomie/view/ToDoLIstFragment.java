package com.cosmicode.roomie.view;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.service.RoomTaskService;

import java.util.List;

public class ToDoLIstFragment extends Fragment implements RoomTaskService.RoomTaskServiceListener {
    private OnFragmentInteractionListener mListener;
    private RoomTaskService roomTaskService;
    private List<RoomTask> roomTaskList;
    private FloatingActionButton confirmButton;
    private FloatingActionButton newTaskButton;
    public ToDoLIstFragment() {
        // Required empty public constructor
    }

    public static ToDoLIstFragment newInstance(String param1, String param2) {
        ToDoLIstFragment fragment = new ToDoLIstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomTaskService = new RoomTaskService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        roomTaskService.getAllTaskByRoom(Long.parseLong("1"));
        confirmButton = getView().findViewById(R.id.button_confirm_done);
        confirmButton.setOnClickListener(this::onClickConfirm);
        newTaskButton = getView().findViewById(R.id.button_new_task);
        newTaskButton.setOnClickListener(this::openTasks);
        super.onViewCreated(view, savedInstanceState);
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

    public void onClickConfirm(View view){
        confirmButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        confirmButton.setImageResource(R.drawable.ic_todo_check);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnCreateTask(RoomTask roomTask) {

    }

    @Override
    public void OnGetTaskByRoomSuccess(List<RoomTask> roomTasks) {
        this.roomTaskList = roomTasks;
        Toast.makeText(getContext(), roomTaskList.get(0).toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnGetTaskByRoomError(String error) {

    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    public void openTasks(View view){
        NewTaskFragment todoFragment = NewTaskFragment.newInstance("", "");
        openFragment(todoFragment);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
