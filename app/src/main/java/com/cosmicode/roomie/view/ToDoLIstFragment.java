package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.domain.enumeration.RoomTaskState;
import com.cosmicode.roomie.service.RoomTaskService;
import com.cosmicode.roomie.util.RoomieTimeUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class ToDoLIstFragment extends Fragment implements RoomTaskService.RoomTaskServiceListener {
    private OnFragmentInteractionListener mListener;
    private RoomTaskService roomTaskService;
    private List<RoomTask> roomTaskList;
    @BindView(R.id.task_list) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.no_tasks_txt) TextView noTasks;
    private FloatingActionButton conconfirmButton;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private static final String ARG_ROOMID = "room_id";

    private long roomId = 1;
    private DateTime dateToday;

    private DateTimeComparator comparator;    public ToDoLIstFragment() {
        // Required empty public constructor
    }

    public static ToDoLIstFragment newInstance(long roomId) {
        ToDoLIstFragment fragment = new ToDoLIstFragment();
        Bundle args = new Bundle();//
        args.putLong(ARG_ROOMID, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.roomId = getArguments().getLong(ARG_ROOMID);
            roomTaskService = new RoomTaskService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        ButterKnife.bind(this, view);
        dateToday = new DateTime();

        comparator = DateTimeComparator.getDateOnlyInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        showProgress(true);
        roomTaskService.getAllTaskByRoom(roomId);
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

//    @OnClick(R.id.button_confirm_done )
//    public void onClickConfirm(View view){
//        confirmButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
//        confirmButton.setImageResource(R.drawable.ic_todo_check);
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnCreateTask(RoomTask roomTask) {

    }

    @Override
    public void OnUpdateSuccess(RoomTask roomTask) {
        roomTaskService.getAllTaskByRoom(roomId);
//        reorderList();
    }

    @Override
    public void OnDeleteSuccess() {

    }

//    private void reorderList() {
//        doneTasks.clear();
//        todayTasks.clear();
//        laterTasks.clear();
//        pastTasks.clear();
//        for (RoomTask task: roomTaskList) {
//
//            int difday = comparator.compare( formatDate(task.getDeadline()), dateToday);
//
//            if(task.getState() == RoomTaskState.COMPLETED){
//                doneTasks.add(task);
//            }else {
//                if(difday == 0){
//                    todayTasks.add(task);
//                }else if (difday > 0){
//                    laterTasks.add(task);
//                }else if(difday<0){
//                    pastTasks.add(task);
//                }
//            }
//        }
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(sectionAdapter);
//
//        showProgress(false);
//    }
    @Override
    public void OnGetTaskByRoomSuccess(List<RoomTask> roomTasks) {
        this.roomTaskList = roomTasks;
        if(roomTaskList.isEmpty()){
            noTasks.setVisibility(View.VISIBLE);
        }else{
            noTasks.setVisibility(View.GONE);
        }
        sectionAdapter = new SectionedRecyclerViewAdapter();

         List<RoomTask> todayTasks = new ArrayList<>();
         List<RoomTask> laterTasks = new ArrayList<>();
         List<RoomTask> doneTasks = new ArrayList<>();
         List<RoomTask> pastTasks = new ArrayList<>();
        for (RoomTask task: roomTasks) {

            int difday = comparator.compare(task.getDeadline(), dateToday);

            if(task.getState() == RoomTaskState.COMPLETED){
                doneTasks.add(task);
            }else {
                if(difday == 0){
                    todayTasks.add(task);
                }else if (difday > 0){
                    laterTasks.add(task);
                }else if(difday<0){
                    pastTasks.add(task);
                }
            }
        }

        if(todayTasks.size() > 0) sectionAdapter.addSection(new TaskSection(getString(R.string.todo_today), todayTasks));
        if(laterTasks.size() > 0) sectionAdapter.addSection(new TaskSection(getString(R.string.todo_upcoming), laterTasks));
        if(pastTasks.size() > 0) sectionAdapter.addSection(new TaskSection(getString(R.string.todo_past_task), pastTasks));
        if(doneTasks.size() > 0) sectionAdapter.addSection(new TaskSection(getString(R.string.todo_completed), doneTasks));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        showProgress(false);
    }
    @Override
    public void OnGetTaskByRoomError(String error) {
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
    @OnClick(R.id.button_new_task )
    public void openTasks(View view){
        NewTaskFragment todoFragment = NewTaskFragment.newInstance(null);
        openFragment(todoFragment);
    }
    @OnClick(R.id.back_button)
    public void goBack(View view){
        getFragmentManager().popBackStack();
    }
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public DateTime formatDate(String pdate){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withLocale(Locale.ROOT)
                .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = format.parseDateTime(pdate);
        return dt;
    }


    //---------------------------------------------------------------------

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        recyclerView.setVisibility(((show) ? View.GONE : View.VISIBLE));

        recyclerView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerView.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

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

    private class TaskSection extends StatelessSection {

        final String title;
        final List<RoomTask> taskList;

        TaskSection( String title, List<RoomTask> taskList) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.task_item)
                    .headerResourceId(R.layout.task_group)
                    .build());

            this.title = title;
            this.taskList = taskList;
        }

        @Override
        public int getContentItemsTotal() {
            return this.taskList.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final TaskViewHolder itemHolder = (TaskViewHolder) holder;

            RoomTask roomTask = this.taskList.get(position);
            itemHolder.cardDescription.setText(roomTask.getDescription());
            itemHolder.cardTitle.setText(roomTask.getTitle());
            itemHolder.cardDeadline.setText(RoomieTimeUtil.instantUTCStringToLocalDateTimeString(roomTask.getDeadline()));

            if(roomTask.getState() == RoomTaskState.COMPLETED){
                itemHolder.buttonConfirm.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.toast_success));
                itemHolder.buttonConfirm.setImageResource(R.drawable.ic_todo_check);
            }
            itemHolder.container.setOnClickListener( v -> {
                NewTaskFragment fragment = NewTaskFragment.newInstance(roomTask);

                openFragment(fragment);

            });
            itemHolder.buttonConfirm.setOnClickListener(v -> {

                if(roomTask.getState() == RoomTaskState.PENDING) {
                    itemHolder.buttonConfirm.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.toast_success));
                    itemHolder.buttonConfirm.setImageResource(R.drawable.ic_todo_check);
                    roomTask.setState(RoomTaskState.COMPLETED);
                    roomTaskService.updateTask(roomTask);
                    showProgress(true);
                }else{
                    itemHolder.buttonConfirm.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.secondary));
                    itemHolder.buttonConfirm.setImageResource(R.drawable.ic_todo_timer);
                    roomTask.setState(RoomTaskState.PENDING);
                    roomTaskService.updateTask(roomTask);
                    showProgress(true);
                }
            }

            );
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new TaskGroupViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            TaskGroupViewHolder headerHolder = (TaskGroupViewHolder) holder;

            headerHolder.groupTitle.setText(this.title);
        }
    }

    private class TaskGroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupTitle;

        TaskGroupViewHolder(View view) {
            super(view);
            groupTitle = view.findViewById(R.id.group_title);
        }
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView cardTitle;
        private final TextView cardDescription;
        private final TextView cardDeadline;
        private final FloatingActionButton buttonConfirm;
        private final CardView container;
        TaskViewHolder(View view) {
            super(view);
            rootView = view;
            cardTitle = view.findViewById(R.id.card_title);
            cardDescription = view.findViewById(R.id.card_description);
            cardDeadline = view.findViewById(R.id.card_time);
            buttonConfirm = view.findViewById(R.id.button_confirm_done);
            container = view.findViewById(R.id.card_item);
        }
    }
}
