package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomEvent;
import com.cosmicode.roomie.service.RoomEventService;
import com.cosmicode.roomie.util.RoomieTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomCalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomCalendarFragment extends Fragment implements RoomEventService.OnRoomEventListener {
    private static String TAG = "RoomCalendarFragment";
    private static final String ARG_ROOM = "roomid";
    private Long mRoomId;
    private RoomEventService roomEventService;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_results)
    TextView noResults;
    @BindView(R.id.room_calendar_view)
    CalendarView calendarView;
    @BindView(R.id.day_event_list)
    RecyclerView dayEventsRecycler;

    List<RoomEvent> roomEvents;
    List<EventDay> calendarEventsList;

    public RoomCalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param roomId Parameter room id.
     * @return A new instance of fragment RoomCalendarFragment.
     */
    public static RoomCalendarFragment newInstance(Long roomId) {
        RoomCalendarFragment fragment = new RoomCalendarFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ROOM, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomEventService = new RoomEventService(getContext(), this);
        if (getArguments() != null) {
            mRoomId = getArguments().getLong(ARG_ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_calendar, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        showProgress(true);
        roomEventService.getAllRoomEventsRoom(mRoomId);

        calendarView.setOnDayClickListener(eventDay -> showDayEvents(eventDay.getCalendar()));

    }

    public void onButtonPressed() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           // throw new RuntimeException(context.toString()
             //       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.create_event_fab)
    public void createEvent(){

    }

    private void showProgress(boolean show) {
        if (show) noResults.setVisibility(View.INVISIBLE);

        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        dayEventsRecycler.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));

        dayEventsRecycler.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dayEventsRecycler.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));
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

    @Override
    public void onCreateRoomEventSuccess(RoomEvent roomEvent) {

    }

    @Override
    public void onUpdateRoomEventSuccess(RoomEvent roomEvent) {

    }

    @Override
    public void onGetRoomEventSuccess(RoomEvent roomEvent) {

    }

    @Override
    public void onGetRoomEventListSuccess(List<RoomEvent> roomEventList) {
        roomEvents = roomEventList;
        calendarEventsList = new ArrayList<>();

        for (RoomEvent roomEvent: roomEvents){
            Calendar calendar = RoomieTimeUtil.dateStringToCalendar(roomEvent.getStartTime());
            roomEvent.setStartTimeCalendar(calendar);
            calendarEventsList.add(new EventDay(calendar, (roomEvent.getPrivate() ? R.drawable.icon_event_private_danger : R.drawable.icon_target_success)));
        }

        calendarView.setEvents(calendarEventsList);
        showDayEvents(Calendar.getInstance());
    }

    private void showDayEvents(Calendar selectedCalendar) {
        showProgress(true);

        List<RoomEvent> dateRoomEvents =new ArrayList<>();

        for(RoomEvent roomEvent: roomEvents)
            if(RoomieTimeUtil.isSameDay(selectedCalendar, roomEvent.getStartTimeCalendar()))
                dateRoomEvents.add(roomEvent);


        if (dateRoomEvents.size() > 0) {
            noResults.setVisibility(View.GONE);
            dayEventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            dayEventsRecycler.setAdapter(new RoomEventRecyclerViewAdapter(dateRoomEvents));
        } else {
            dayEventsRecycler.setAdapter(null);
            noResults.setVisibility(View.VISIBLE);
        }

        showProgress(false);
    }

    @Override
    public void onRoomEventError(String error) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class RoomEventRecyclerViewAdapter extends RecyclerView.Adapter<RoomEventRecyclerViewAdapter.ViewHolder> {

        private final List<RoomEvent> mValues;

        public RoomEventRecyclerViewAdapter(List<RoomEvent> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_event_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            holder.roomEventDescription.setText(holder.mItem.getDescription());

            holder.roomEventType.setBackgroundResource((holder.mItem.getPrivate() ? R.drawable.icon_event_private_danger : R.drawable.icon_target_success));

            holder.roomEventDate.setText(String.format("%s %s %s", RoomieTimeUtil.formatInstantStringDateTime(holder.mItem.getStartTime()), getString(R.string.to) , RoomieTimeUtil.formatInstantStringTime(holder.mItem.getEndTime())));

            holder.roomEventCard.setOnClickListener(l -> {

            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            @BindView(R.id.room_event_type)
            ImageView roomEventType;
            @BindView(R.id.room_event_description)
            TextView roomEventDescription;
            @BindView(R.id.room_event_state)
            TextView roomEventState;
            @BindView(R.id.room_event_date)
            TextView roomEventDate;
            @BindView(R.id.room_event_settings)
            ImageView roomEventSettings;
            @BindView(R.id.room_event_card)
            CardView roomEventCard;

            public RoomEvent mItem;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mView = view;
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItem.toString() + "'";
            }
        }
    }

}
