package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class NewTaskFragment extends Fragment implements RoomTaskService.RoomTaskServiceListener, Validator.ValidationListener{

    private OnFragmentInteractionListener mListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ImageButton buttonDeadline, deletebtn;
    private ImageButton buttonTime;
    private Button createButton;
    private TextView txtDeadline, txtTime, title;
    @NotEmpty
    @Length(min = 4, max = 50)
    TextView editTitle;

    @NotEmpty
    @Length(min = 4, max = 50)
    TextView editDesc;
    private String date;
    private ProgressBar progressBar;
    private TimePickerDialog.OnTimeSetListener mTListener;
    private String time;
    private RoomTaskService roomTaskService;
    private RoomTask task;
    private ImageButton backButton;
    private static final String TASK_KEY = "task";
    private Validator validator;
    private boolean isValid = true;
    public NewTaskFragment() {
    }

    public static NewTaskFragment newInstance(RoomTask task) {
        NewTaskFragment fragment = new NewTaskFragment();
        Bundle args = new Bundle();
        args.putParcelable(TASK_KEY, task);
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
        validator = new Validator(this);
        validator.setValidationListener(this);

        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonDeadline = getView().findViewById(R.id.button_deadline);
        txtTime = getView().findViewById(R.id.txt_time);
        title = getView().findViewById(R.id.Title);
        buttonDeadline.setOnClickListener(this::onClickDate);
        buttonTime = getView().findViewById(R.id.timer_button);
        buttonTime.setOnClickListener(this::onClickTime);
        txtDeadline = getView().findViewById(R.id.textDeadline);
        progressBar = getView().findViewById(R.id.progress);
        createButton = getView().findViewById(R.id.button_create);
        backButton = getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(this::onClickBack);
        createButton.setOnClickListener(this::onClickCreateTask);
        task = getArguments().getParcelable(TASK_KEY);
        deletebtn = getView().findViewById(R.id.deletebtn);
        editTitle = getView().findViewById(R.id.edit_title);
        editDesc = getView().findViewById(R.id.edit_Description);

        txtDeadline.setText(getDeadlineLimit());
        txtTime.setText(getTimeLimit());
        taskNotNull();
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month++;

                txtDeadline.setText(dayOfMonth + "/" + month  + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if (month <= 9) {
                    monthS = "0" + month;
                }

                if (dayOfMonth <= 9) {
                    dayS = "0" + dayOfMonth;
                }
                date = year + "-" + monthS + "-" + dayS;
            }
        };
        mTListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String mm = Integer.toString(minute);
                String hh = Integer.toString(hourOfDay);
                if (minute<10){
                    mm = "0"+minute;
                }
                if (hourOfDay<10){
                    hh = "0"+hourOfDay;
                }
                time = hh + ":" + mm;
                txtTime.setText(time);
            }
        };
        JodaTimeAndroid.init(getContext());
        super.onViewCreated(view, savedInstanceState);
    }
    public String getDeadlineLimit(){
        String txtDead, month, days;
        DateTime max = new DateTime();
        if(max.getMonthOfYear()+1<10){
            month = 0+String.valueOf(max.getMonthOfYear());
        }else{
            month = String.valueOf(max.getMonthOfYear());
        }
        if (max.getDayOfMonth()<10) {
            days = 0+ String.valueOf(max.getDayOfMonth());
        }else{
            days = String.valueOf(max.getDayOfMonth());
        }
        txtDead = days + "/" + month + "/" + max.getYear();
        date = max.getYear() + "-" + month + "-" + days;
        return txtDead;
    }
    public String getTimeLimit(){
        time = "23:59";
        return "23:59";
    }

    public void onClickDate(View view) {
        DateTime max = new DateTime();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear()-1, max.getDayOfMonth());
        dialog.getDatePicker().setMinDate(max.getMillis());
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
        showProgress(true);
        if(txtDeadline.toString().equals("")){
            txtDeadline.setError("Please choose a date");
            isValid = false;
        }else{
            isValid = true;
        }


        if(txtTime.toString().equals("")){
            txtTime.setError("Please choose a gender");
            isValid = false;
        }else{
            isValid = true;
        }
        validator.validate();
    }


    public void onClickUpdateTask(View view) {
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
        this.task.setTitle(editTitle.getText().toString());
        this.task.setDescription(editDesc.getText().toString());
        if(date != null && time != null){
            this.task.setDeadline(deadline);
        }

        roomTaskService.updateTask(task);
        showProgress(true);
    }
    public void onClickDeleteTask(View view) {
        roomTaskService.deleteTask(task.getId());
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
        getFragmentManager().popBackStack();
    }

    @Override
    public void OnUpdateSuccess(RoomTask roomTask) {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void OnDeleteSuccess() {
        showProgress(false);
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();

    }

    @Override
    public void OnGetTaskByRoomSuccess(List<RoomTask> roomTasks) {

    }

    @Override
    public void OnGetTaskByRoomError(String error) {
        showProgress(false);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        if (isValid){

            DateTime today = new DateTime();

            int month, day;
            month = today.getMonthOfYear();
            day = today.getDayOfMonth();
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
            String deadline = date +"T"+ time+ ":00Z";
            String created = (today.getYear()+"-"+monthS+"-"+dayS+"T00:00:00Z");

            Long id = new Long(1);
            task = new RoomTask(created, editTitle.getText().toString(), editDesc.getText().toString(), deadline, RoomTaskState.PENDING, id);

            roomTaskService.createTask(task);
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

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }

    public void onClickBack(View view){
        getFragmentManager().popBackStack();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void taskNotNull(){
        if(task != null){
            editTitle.setText(task.getTitle());
            editDesc.setText(task.getDescription());
            String date, time;
            DateTime dt = formatDate(task.getDeadline());
            date = dt.getDayOfMonth() + "/" + dt.getMonthOfYear()+ "/"+ dt.getYear();
            time = dt.getHourOfDay()+":"+dt.getMinuteOfHour();
            txtDeadline.setText(date);
            txtTime.setText(time);
            createButton.setText(R.string.todo_past_save);
            title.setText(R.string.todo_edit_title);
            deletebtn.setVisibility(View.VISIBLE);
            deletebtn.setOnClickListener(this::onClickDeleteTask);
            createButton.setOnClickListener(this::onClickUpdateTask);
        }else{
            title.setText(R.string.todo_new_title);
            deletebtn.setVisibility(View.GONE);
            createButton.setOnClickListener(this::onClickCreateTask);
        }

    }


    public DateTime formatDate(String pdate){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = format.parseDateTime(pdate);
        return dt;
    }
}
