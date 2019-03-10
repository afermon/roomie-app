package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.domain.enumeration.RoomTaskState;
import com.cosmicode.roomie.service.RoomTaskService;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.List;


public class NewTaskFragment extends Fragment implements RoomTaskService.RoomTaskServiceListener {

    private OnFragmentInteractionListener mListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ImageButton buttonDeadline;
    private ImageButton buttonTime;
    private Button createButton;
    private TextView txtDeadline;
    private TextView txtTime;
    private EditText editTitle, editDesc;
    private String date;
    private ProgressBar progressBar;
    private TimePickerDialog.OnTimeSetListener mTListener;
    private String time;
    private RoomTaskService roomTaskService;
    public NewTaskFragment() {
    }

    public static NewTaskFragment newInstance(String param1, String param2) {
        NewTaskFragment fragment = new NewTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomTaskService = new RoomTaskService(getContext(),this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonDeadline = getView().findViewById(R.id.button_deadline);
        txtTime = getView().findViewById(R.id.txt_time);
        buttonDeadline.setOnClickListener(this::onClickDate);
        buttonTime = getView().findViewById(R.id.timer_button);
        buttonTime.setOnClickListener(this::onClickTime);
        txtDeadline = getView().findViewById(R.id.textDeadline);
        progressBar = getView().findViewById(R.id.progress);
        editTitle = getView().findViewById(R.id.edit_title);
        editDesc = getView().findViewById(R.id.edit_Description);
        createButton = getView().findViewById(R.id.button_create);
        createButton.setOnClickListener(this::onClickCreateTask);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtDeadline.setText(dayOfMonth + "/" + month + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if(month <= 9){
                    monthS = "0"+month;
                }

                if(dayOfMonth <= 9){
                    dayS = "0"+dayOfMonth;
                }
                date = year+"-"+monthS+"-"+dayS;
            }
        };
        mTListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String mm = Integer.toString(minute);
                if (minute<10){
                    mm = "0"+minute;
                }
                time = hourOfDay + ":" + mm;
                txtTime.setText(time);
            }
        };
        JodaTimeAndroid.init(getContext());
        super.onViewCreated(view, savedInstanceState);
    }

    public void onClickDate(View view) {
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.show();
    }

    public void onClickTime(View view) {
        DateTime max = new DateTime();
        TimePickerDialog dialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mTListener, max.getHourOfDay(), max.getMinuteOfDay(), true);
        dialog.show();
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

    public void onClickCreateTask(View view){
        DateTime today = new DateTime();

        int month, day;
        month = today.getMonthOfYear();
        day = today.getDayOfMonth();
        String monthS, dayS;
        monthS = Integer.toString(month);
        dayS = Integer.toString(day);

        if(month <= 9){
            monthS = "0"+month;
        }
        if(day <= 9){
            dayS = "0"+day;
        }
        String deadline = date +"T"+ time+ ":00Z";
        String created = (today.getYear()+"-"+monthS+"-"+dayS+"T00:00:00Z");
        Long id = new Long(1);
        RoomTask task = new RoomTask(created, editTitle.getText().toString(), editDesc.getText().toString(), deadline, RoomTaskState.PENDING, id);
//        Toast.makeText(getContext(), task.toString(), Toast.LENGTH_SHORT).show();
        roomTaskService.createTask(task);
        showProgress(true);
     }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void OnCreateTask(RoomTask roomTask) {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

        ToDoLIstFragment todoFragment = ToDoLIstFragment.newInstance(1);
        openFragment(todoFragment);
    }

    @Override
    public void OnGetTaskByRoomSuccess(List<RoomTask> roomTasks) {

    }

    @Override
    public void OnGetTaskByRoomError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
