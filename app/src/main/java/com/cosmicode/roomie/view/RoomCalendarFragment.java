package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.CustomErrorReset;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidationCallback;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomEvent;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.RoomEventService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.RoomieTimeUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomCalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomCalendarFragment extends Fragment implements RoomEventService.OnRoomEventListener, RoomieService.OnGetCurrentRoomieListener {
    private static String TAG = "RoomCalendarFragment";
    private static final String ARG_ROOM = "roomid";
    private Long mRoomId;
    private Roomie currentRoomie;
    private RoomEventService roomEventService;
    private RoomieService roomieService;
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
        roomieService = new RoomieService(getContext(), this);
        roomieService.getCurrentRoomie();
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
        Calendar currentCalendar = calendarView.getFirstSelectedDate();
        Calendar startTimeCalendar = (Calendar) calendarView.getFirstSelectedDate().clone();
        Calendar endTimeCalendar = (Calendar) calendarView.getFirstSelectedDate().clone();

        DateTime currentTime = DateTime.now();
        int mHour = currentTime.getHourOfDay();
        int mMinute = currentTime.getMinuteOfHour();

        startTimeCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        startTimeCalendar.set(Calendar.MINUTE, mMinute);
        endTimeCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        endTimeCalendar.set(Calendar.MINUTE, mMinute);

        AlertDialog.Builder newEventDialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View newEventLayout = inflater.inflate(R.layout.new_event_dialog, null);

        TextView eventStartTV = newEventLayout.findViewById(R.id.room_event_start_tv);
        TextView eventEndTV = newEventLayout.findViewById(R.id.room_event_end_tv);
        Switch eventTypeSwitch = newEventLayout.findViewById(R.id.room_event_private);
        EditText roomEvemtTitleET = newEventLayout.findViewById(R.id.room_event_title);
        EditText roomEventDescriptionET = newEventLayout.findViewById(R.id.room_event_description);

        eventStartTV.setText(RoomieTimeUtil.calendarToTimeString(startTimeCalendar));
        eventStartTV.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.RoomieDialogTheme,
                    (view2, hourOfDay, minute) -> {
                        Log.d(TAG, hourOfDay + ":" + minute);
                        startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startTimeCalendar.set(Calendar.MINUTE, minute);
                        eventStartTV.setText(RoomieTimeUtil.calendarToTimeString(startTimeCalendar));
                    }, mHour, mMinute, false);
            timePickerDialog.setTitle(R.string.start_time);
            timePickerDialog.show();
        });

        eventEndTV.setText(RoomieTimeUtil.calendarToTimeString(endTimeCalendar));
        eventEndTV.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.RoomieDialogTheme,
                    (view2, hourOfDay, minute) -> {
                        Log.d(TAG, hourOfDay + ":" + minute);
                        endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endTimeCalendar.set(Calendar.MINUTE, minute);
                        eventEndTV.setText(RoomieTimeUtil.calendarToTimeString(endTimeCalendar));
                    }, mHour, mMinute, false);
            timePickerDialog.setTitle(R.string.end_time);
            timePickerDialog.show();
        });


        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(roomEvemtTitleET, "^.{4,}", getString(R.string.not_empty));
        mAwesomeValidation.addValidation(roomEventDescriptionET, "^.{4,}", getString(R.string.not_empty));

        newEventDialogBuilder.setTitle(RoomieTimeUtil.calendarToDateString(currentCalendar))
                .setIcon(R.drawable.icon_calendar_brand)
                .setView(newEventLayout)
                .setPositiveButton(R.string.Schedule, (dialog, which) -> { })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog newEventDialog = newEventDialogBuilder.create();
        newEventDialog.show();

        newEventDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (endTimeCalendar.get(Calendar.HOUR_OF_DAY) < startTimeCalendar.get(Calendar.HOUR_OF_DAY) ||
                    ((endTimeCalendar.get(Calendar.HOUR_OF_DAY) == startTimeCalendar.get(Calendar.HOUR_OF_DAY)) && (endTimeCalendar.get(Calendar.MINUTE) < startTimeCalendar.get(Calendar.MINUTE)))){
                endTimeCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalendar.get(Calendar.HOUR_OF_DAY));
                endTimeCalendar.set(Calendar.MINUTE, startTimeCalendar.get(Calendar.MINUTE));
                ((BaseActivity) getContext()).showUserMessage(getString(R.string.room_event_time_error), BaseActivity.SnackMessageType.ERROR);
            } else if(mAwesomeValidation.validate()){
                RoomEvent roomEvent = new RoomEvent();
                roomEvent.setRoomId(mRoomId);
                roomEvent.setStartTime(RoomieTimeUtil.calendarToInstantUTCString(startTimeCalendar));
                roomEvent.setEndTime(RoomieTimeUtil.calendarToInstantUTCString(endTimeCalendar));
                roomEvent.setTitle(roomEvemtTitleET.getText().toString());
                roomEvent.setDescription(roomEventDescriptionET.getText().toString());
                roomEvent.setPrivate(eventTypeSwitch.isChecked());
                roomEvent.setOrganizerId(currentRoomie.getId());
                roomEventService.createRoomEvent(roomEvent);
                showProgress(true);
                newEventDialog.dismiss();
            }
        });
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
        ((BaseActivity) getContext()).showUserMessage(String.format(getString(R.string.room_event_created), roomEvent.getTitle()), BaseActivity.SnackMessageType.SUCCESS);
        roomEvent.setOrganizer(currentRoomie);
        roomEvents.add(roomEvent);
        addCalendarEvent(roomEvent);
        showDayEvents(RoomieTimeUtil.instantUTCStringToCalendar(roomEvent.getStartTime()));
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

        for (RoomEvent roomEvent: roomEvents)
            addCalendarEvent(roomEvent);

        calendarView.setEvents(calendarEventsList);
        showDayEvents(Calendar.getInstance());
    }

    private void addCalendarEvent(RoomEvent roomEvent) {
        Calendar calendar = RoomieTimeUtil.instantUTCStringToCalendar(roomEvent.getStartTime());
        roomEvent.setStartTimeCalendar(calendar);
        calendarEventsList.add(new EventDay(calendar, (roomEvent.getPrivate() ? R.drawable.icon_event_private_danger : R.drawable.icon_target_success)));
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
        ((BaseActivity) getContext()).showUserMessage(getString(R.string.error_request), BaseActivity.SnackMessageType.ERROR);
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        this.currentRoomie = roomie;
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        ((BaseActivity) getContext()).showUserMessage(getString(R.string.error_getting_items), BaseActivity.SnackMessageType.ERROR);
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

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

            Glide.with(getContext()).load(holder.mItem.getOrganizer().getPicture()).centerCrop().into(holder.organizerImageView);

            holder.roomEventDate.setText(String.format("%s %s %s", RoomieTimeUtil.instantUTCStringToLocalTimeString(holder.mItem.getStartTime()), getString(R.string.to) , RoomieTimeUtil.instantUTCStringToLocalTimeString(holder.mItem.getEndTime())));

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
            @BindView(R.id.organizer_profile_image)
            CircleImageView organizerImageView;
            @BindView(R.id.room_event_description)
            TextView roomEventDescription;
            @BindView(R.id.room_event_title)
            TextView roomEventTitle;
            @BindView(R.id.room_event_date)
            TextView roomEventDate;
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
