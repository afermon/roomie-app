package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.service.RoomService;

import java.sql.RowId;
import java.util.List;

public class RoomStateFragment extends Fragment implements RoomService.RoomServiceListener {

    private static final String ROOM = "room";

    private RoomCreate room;
    private RoomService roomService;

    @BindView(R.id.published)
    Button published;
    @BindView(R.id.inactive)
    Button inactive;
    @BindView(R.id.cont)
    ConstraintLayout cont;
    @BindView(R.id.progress)
    ProgressBar progress;

    private OnFragmentInteractionListener mListener;

    public RoomStateFragment() {
        // Required empty public constructor
    }


    public static RoomStateFragment newInstance(RoomCreate room) {
        RoomStateFragment fragment = new RoomStateFragment();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = getArguments().getParcelable(ROOM);
            roomService = new RoomService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_state, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(room.getState() == RoomState.SEARCH){
            clickPublished(view);
        }else {
            clickInactive(view);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.back)
    public void goBack(View view){
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.btn_state)
    public void saveState (View view){
        showProgress(true);
        roomService.updateRoom(room);
    }

    @OnClick(R.id.published)
    public void clickPublished (View view) {
        activeButton(published, inactive);
        room.setState(RoomState.SEARCH);
    }

    @OnClick(R.id.inactive)
    public void clickInactive(View view) {
        activeButton(inactive, published);
        room.setState(RoomState.INACTIVE);
    }

    public void activeButton(Button active, Button inactive) {
        active.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
        active.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        inactiveGender(inactive);
    }

    public void inactiveGender(Button inactive) {
        inactive.setBackgroundTintList(null);
        inactive.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    @Override
    public void OnCreateSuccess(Room room) {

    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {

    }

    @Override
    public void OnGetRoomsError(String error) {
        ((BaseActivity) getContext()).showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
        showProgress(false);
    }

    @Override
    public void OnUpdateSuccess(Room room) {
        ((BaseActivity) getContext()).showUserMessage("Room updated successfully!", BaseActivity.SnackMessageType.SUCCESS);
        showProgress(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().popBackStack();
            }
        }, 1000);
    }

    @Override
    public void onPaySuccess(Room room) {

    }

    @Override
    public void onPayError(String error) {

    }


    public interface OnFragmentInteractionListener {
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        cont.setVisibility(((show) ? View.GONE : View.VISIBLE));

        cont.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cont.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
        progress.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 1 : 0))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
                    }
                });
    }
}
