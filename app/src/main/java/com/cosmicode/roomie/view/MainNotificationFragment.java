package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Notification;
import com.cosmicode.roomie.service.NotificationService;

import java.util.List;

public class MainNotificationFragment extends Fragment implements NotificationService.NotificationServiceListener {


    private NotificationService notificationService;
    private TextView notificationTextView;
    private ProgressBar progress;

    private OnFragmentInteractionListener mListener;

    public MainNotificationFragment() {
        // Required empty public constructor
    }

    public static MainNotificationFragment newInstance( ) {
        MainNotificationFragment fragment = new MainNotificationFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        notificationService = new NotificationService(getContext(), this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        notificationTextView = getView().findViewById(R.id.textview_notifications);
        progress = getView().findViewById(R.id.progress);
        showProgress(true);
        notificationService.getAllNotifications();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnGetNotificationsSuccess(List<Notification> notifications) {
        notificationTextView.setText(notifications.toString());
        showProgress(false);
    }

    @Override
    public void OnGetNotificationsError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        if(mListener != null)
            mListener.returnToHomeFragment();
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        notificationTextView.setVisibility(((show) ? View.GONE : View.VISIBLE));

        notificationTextView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        notificationTextView.setVisibility(((show) ? View.GONE : View.VISIBLE));
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void returnToHomeFragment();
    }
}
