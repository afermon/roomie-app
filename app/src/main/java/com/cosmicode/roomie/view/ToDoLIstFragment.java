package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.service.RoomTaskService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ToDoLIstFragment extends Fragment implements RoomTaskService.RoomTaskServiceListener {
    private OnFragmentInteractionListener mListener;
    private RoomTaskService roomTaskService;
    private List<RoomTask> roomTaskList;
    @BindView(R.id.task_list) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private static final String ARG_ROOMID = "room_id";
    private long roomId = 1;
    public ToDoLIstFragment() {
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
    public void OnGetTaskByRoomSuccess(List<RoomTask> roomTasks) {
        this.roomTaskList = roomTasks;
        sectionAdapter = new SectionedRecyclerViewAdapter();

//        for (Folder folder: folders) {
            sectionAdapter.addSection(new TaskSection("all task", roomTasks));
        //}

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        Toast.makeText(getContext(), roomTaskList.get(0).toString(), Toast.LENGTH_SHORT).show();
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
        NewTaskFragment todoFragment = NewTaskFragment.newInstance("", "");
        openFragment(todoFragment);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
            itemHolder.cardDeadline.setText(roomTask.getDeadline());

            itemHolder.buttonConfirm.setOnClickListener(v -> Toast.makeText(getContext(),
                    String.format("Clicked on position #%s of Section %s",
                            sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition()),
                            roomTask),
                    Toast.LENGTH_SHORT).show());
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

        TaskViewHolder(View view) {
            super(view);
            rootView = view;
            cardTitle = view.findViewById(R.id.card_title);
            cardDescription = view.findViewById(R.id.card_description);
            cardDeadline = view.findViewById(R.id.card_time);
            buttonConfirm = view.findViewById(R.id.button_confirm_done);
        }
    }
}
