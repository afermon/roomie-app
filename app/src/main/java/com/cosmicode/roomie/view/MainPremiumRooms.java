package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.service.RoomService;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;


public class MainPremiumRooms extends Fragment implements RoomService.RoomServiceListener {

    private OnFragmentInteractionListener mListener;
    private RoomService roomService;
    @BindView(R.id.prem_rooms_cont)
    ConstraintLayout cont;
    @BindView(R.id.prem_recycler)
    RecyclerView premRecycler;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    public MainPremiumRooms() {
        // Required empty public constructor
    }

    public static MainPremiumRooms newInstance() {
        MainPremiumRooms fragment = new MainPremiumRooms();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.back_prem)
    public void goBack(View view){
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomService = new RoomService(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_premium_rooms, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        roomService.getAllRooms();
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

    public class PremRoomsAdapter extends RecyclerView.Adapter<PremRoomsAdapter.ViewHolder> {

        private final List<Room> mValues;

        public PremRoomsAdapter(List<Room> items) {
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
            holder.roomCount.setText(String.format("x%d", mValues.get(position).getRooms()));

            holder.roomAvailableFrom.setText(mValues.get(position).getAvailableFrom());

            holder.roomCard.setOnClickListener(l -> {
//                ToDoLIstFragment toDoLIstFragment = ToDoLIstFragment.newInstance(mValues.get(position).getId());
                ExpenseList newExpenseFragment = ExpenseList.newInstance(mValues.get(position));
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, newExpenseFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });


            RoomPicture picture = mValues.get(position).getMainPicture();
            if (picture != null) {
                Glide.with(getContext()).load(picture.getUrl()).centerCrop().into(holder.roomPinture);
            }

            Address address = mValues.get(position).getAddress();
            holder.roomAddress.setText(String.format("%s, %s", address.getCity(), address.getState()));

            //Price
            RoomExpense price = mValues.get(position).getPrice();
            Double priceUser = price.getAmount(); /// mValues.get(position).getRooms(); // Price per user
            if (price.getCurrency() == CurrencyType.DOLLAR) {
                holder.roomPrice.setText(String.format("%s %s %s", "$", priceUser.intValue(), "USD"));
            } else {
                holder.roomPrice.setText(String.format("%s %s %s", "₡", priceUser.intValue(), "CRC"));
            }

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
            @BindView(R.id.room_price)
            TextView roomPrice;
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

    @Override
    public void OnCreateSuccess(Room room) {

    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {
        List<Room> premium = new ArrayList<>();
        premium.add(rooms.get(0));
        premRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        premRecycler.setAdapter(new PremRoomsAdapter(premium));
        showProgress(false);
    }

    @Override
    public void OnGetRoomsError(String error) {

    }

    @Override
    public void OnUpdateSuccess(Room room) {

    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
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
}
