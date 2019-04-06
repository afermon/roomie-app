package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Notification;
import com.cosmicode.roomie.service.NotificationService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainNotificationFragment extends Fragment implements NotificationService.NotificationServiceListener {


    private NotificationService notificationService;
    private TextView notificationTextView;
    private ProgressBar progress;
    private RecyclerView.Adapter mAdapater;
    @BindView(R.id.notifications_recycler)
    RecyclerView notificationsRecycler;
    @BindView(R.id.no_notif_text)
    TextView noNotif;

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
        View view = inflater.inflate(R.layout.fragment_main_notification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progress = getView().findViewById(R.id.progress);

        notificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

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
        if(notifications.isEmpty()){
            noNotif.setVisibility(View.VISIBLE);
        }else{
            noNotif.setVisibility(View.GONE);
        }
        mAdapater = new NotificationAdapter(notifications);
        notificationsRecycler.setAdapter(mAdapater);
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

        notificationsRecycler.setVisibility(((show) ? View.GONE : View.VISIBLE));

        notificationsRecycler.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        notificationsRecycler.setVisibility(((show) ? View.GONE : View.VISIBLE));
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

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.CardViewHolder> {
        private List<Notification> notifications;

        public class CardViewHolder extends RecyclerView.ViewHolder {

            private TextView titleText, descriptionText;
            private ImageView icon;
            CardViewHolder(View view) {
                super(view);
                titleText = view.findViewById(R.id.text_title);
                descriptionText = view.findViewById(R.id.text_description);
                icon = view.findViewById(R.id.icon_notification);
            }

        }

        public NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications;
        }

        public int getItemCount() {
            return notifications.size();
        }

        @NonNull
        @Override
        public NotificationAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_template, viewGroup, false);
            NotificationAdapter.CardViewHolder vh = new NotificationAdapter.CardViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final NotificationAdapter.CardViewHolder holder, int position) {

            Notification notification = this.notifications.get(position);

            holder.titleText.setText(notification.getTitle());
            holder.descriptionText.setText(notification.getBody());

            switch (notification.getType()){
                case APPOINTMENT:
                    holder.icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_todo_timer));
                    break;
                case TODO:
                    holder.icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_notification_to_do_list));
                    break;
                case EXPENSE:
                    holder.icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_notification_expenses));
                    break;
                case EVENT:
                    holder.icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_register_calendar));
                    break;
                default: //APP
                    holder.icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_action_notification));
                    break;
            }
        }
    }

}
