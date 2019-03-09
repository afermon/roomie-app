package com.cosmicode.roomie.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
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
        roomTaskService.getAllTaskByRoom(Long.parseLong("1"));
        confirmButton = getView().findViewById(R.id.button_confirm_done);
        confirmButton.setOnClickListener(this::onClickConfirm);
        return inflater.inflate(R.layout.fragment_to_do_list, container, false);
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
}
