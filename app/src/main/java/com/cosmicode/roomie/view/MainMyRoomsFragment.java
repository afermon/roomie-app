package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.service.RoomService;
import com.cosmicode.roomie.util.listeners.OnGetOwnedRoomsListener;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMyRoomsFragment extends Fragment implements OnGetOwnedRoomsListener {


    private OnFragmentInteractionListener mListener;
    private RoomService roomService;

    @BindView(R.id.back_my_room)
    ImageButton back;
    @BindView(R.id.my_room_cont)
    ConstraintLayout cont;
    @BindView(R.id.my_rooms_recycler)
    RecyclerView roomsRecycler;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.no_rooms_text)
    TextView noRooms;

    public MainMyRoomsFragment() {
        // Required empty public constructor
    }

    public static MainMyRoomsFragment newInstance() {
        MainMyRoomsFragment fragment = new MainMyRoomsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomService = new RoomService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_my_rooms, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        roomService.getOwnedRooms(mListener.getCurrentRoomie().getId(), this);
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
    public void onGetOwnedRoomsSuccess(List<Room> rooms) {
        if(rooms.isEmpty()){
            noRooms.setVisibility(View.VISIBLE);
        }else{
            noRooms.setVisibility(View.GONE);
        }
        roomsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        roomsRecycler.setAdapter(new MyRoomRecyclerViewAdapter(rooms));
        showProgress(false);
    }

    @Override
    public void onGetOwnedRoomsError(String error) {
        ((BaseActivity) getContext()).showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();

        Roomie getCurrentRoomie();
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        cont.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));

        cont.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cont.setVisibility(((show) ? View.INVISIBLE : View.VISIBLE));
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

    public class MyRoomRecyclerViewAdapter extends RecyclerView.Adapter<MyRoomRecyclerViewAdapter.ViewHolder> {

        private final List<Room> mValues;

        public MyRoomRecyclerViewAdapter(List<Room> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_room_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.roomTitle.setText(mValues.get(position).getTitle());

            holder.roomAvailableFrom.setText(mValues.get(position).getAvailableFrom());

            holder.roomCard.setOnClickListener(l -> {
                MainRoomFragment roomView = MainRoomFragment.newInstance(holder.mItem);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, roomView);
                transaction.addToBackStack(null);
                transaction.commit();
            });

            holder.roomCount.setVisibility(View.GONE);
            holder.roomie_icon.setVisibility(View.GONE);

            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setOnClickListener(l ->{
                MainEditRoom editRoom = MainEditRoom.newInstance(holder.mItem);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, editRoom);
                transaction.addToBackStack(null);
                transaction.commit();
            });


            RoomPicture picture = mValues.get(position).getMainPicture();
            if (picture != null) {
                Glide.with(getContext()).load(picture.getUrl()).centerCrop().into(holder.roomPinture);
            }

            if(mValues.get(position).getState() == RoomState.SEARCH){
                holder.roomAddress.setText(String.format("%s","%s", "State:", "Published"));
            }else{
                holder.roomAddress.setText(String.format("%s","%s", "State:", "Inactive"));

            }

            //Price
            RoomExpense price = mValues.get(position).getPrice();
            Double priceUser = price.getAmount(); /// mValues.get(position).getRooms(); // Price per user

            holder.roomPrice.setAmount(priceUser.intValue());
            holder.roomPrice.setSymbol((price.getCurrency() == CurrencyType.DOLLAR) ? "$" : "₡");

            holder.roomDistance.setVisibility(View.GONE);


            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            DateTime published = dateTimeFormatter.parseDateTime(mValues.get(position).getPublished());
            DateTime now = new DateTime();
            Period period = new Period(published, now);

            PeriodFormatterBuilder builder = new PeriodFormatterBuilder();

            if (period.getYears() != 0) {
                builder.appendYears().appendSuffix(getString(R.string.time_year), getString(R.string.time_years));
            } else if (period.getMonths() != 0) {
                builder.appendMonths().appendSuffix(getString(R.string.time_month), getString(R.string.time_months));
            } else if (period.getDays() != 0) {
                builder.appendDays().appendSuffix(getString(R.string.time_day), getString(R.string.time_days));
            } else if (period.getHours() != 0) {
                builder.appendHours().appendSuffix(getString(R.string.time_hour), getString(R.string.time_hours));
            } else if (period.getMinutes() != 0) {
                builder.appendMinutes().appendSuffix(getString(R.string.time_minute), getString(R.string.time_minutes));
            } else if (period.getSeconds() != 0) {
                builder.appendSeconds().appendSuffix(getString(R.string.time_second), getString(R.string.time_seconds));
            }
            PeriodFormatter formatter = builder.printZeroNever().toFormatter();
            String elapsed = formatter.print(period);
            holder.loc.setVisibility(View.GONE);
            holder.roomPublished.setText(elapsed);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            @BindView(R.id.room_picture)
            ImageView roomPinture;
            @BindView(R.id.room_title)
            TextView roomTitle;
            @BindView(R.id.room_address)
            TextView roomAddress;
            @BindView(R.id.room_available)
            TextView roomAvailableFrom;
            @BindView(R.id.room_published)
            TextView roomPublished;
            @BindView(R.id.room_count)
            TextView roomCount;
            @BindView(R.id.room_distance)
            TextView roomDistance;
            @BindView(R.id.room_card)
            CardView roomCard;
            @BindView(R.id.imageView6)
            ImageView loc;
            @BindView(R.id.imageView2)
            ImageView roomie_icon;
            @BindView(R.id.room_price)
            MoneyTextView roomPrice;
            @BindView(R.id.edit_btn)
            ImageView edit;


            public Room mItem;

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

    @OnClick(R.id.back_my_room)
    public void goBack(View view){
        getFragmentManager().popBackStack();
    }
}
