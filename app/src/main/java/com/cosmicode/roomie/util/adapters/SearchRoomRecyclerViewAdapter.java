package com.cosmicode.roomie.util.adapters;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.MainActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.view.MainRoomFragment;
import com.cosmicode.roomie.view.MainSearchFragment.OnFragmentInteractionListener;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.DecimalFormat;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Room} and makes a call to the
 * specified {@link OnFragmentInteractionListener}.
 */
public class SearchRoomRecyclerViewAdapter extends RecyclerView.Adapter<SearchRoomRecyclerViewAdapter.ViewHolder> {

    private final List<Room> mValues;
    private final OnFragmentInteractionListener mListener;
    private Location mCurrentUserLocation;
    private Context mContext;
    private static final String TAG = "SearchRoomRecyclerViewAdapter";

    public SearchRoomRecyclerViewAdapter(List<Room> items, Location currentUserLocation, OnFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mCurrentUserLocation = currentUserLocation;
        mContext = context;
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
        MainActivity activity = (MainActivity) mContext;

        holder.roomCard.setOnClickListener(l -> {
            MainRoomFragment roomView = MainRoomFragment.newInstance(holder.mItem);
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, roomView);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        try {

            //Picture
            RoomPicture picture = mValues.get(position).getMainPicture();
            if (picture != null)
                Glide.with(mContext).load(picture.getUrl()).centerCrop().into(holder.roomPinture);
            else
                Log.e(TAG, "no picture found > " + mValues.get(position).getPictures().size());

            Address address = mValues.get(position).getAddress();
            holder.roomAddress.setText(String.format("%s, %s", address.getCity(), address.getState()));

            //Price
            RoomExpense price = mValues.get(position).getPrice();
            Double priceUser = price.getAmount(); /// mValues.get(position).getRooms(); // Price per user
            holder.roomPrice.setAmount(priceUser.intValue());
            holder.roomPrice.setSymbol((price.getCurrency() == CurrencyType.DOLLAR) ? "$" : "₡");

            float distance = mCurrentUserLocation.distanceTo(address.getLocation());
            DecimalFormat distanceFormat = new DecimalFormat("#0.00");
            if (distance > 1000) {
                distance = distance / 1000; //To Km
                holder.roomDistance.setText(String.format("%s Km", distanceFormat.format(distance)));
            } else
                holder.roomDistance.setText(String.format("%s m", distanceFormat.format(distance)));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        DateTime published = dateTimeFormatter.parseDateTime(mValues.get(position).getPublished());
        DateTime now = new DateTime();
        Period period = new Period(published, now);

        PeriodFormatterBuilder builder = new PeriodFormatterBuilder();

        if(period.getYears() != 0) {
            builder.appendYears().appendSuffix(mContext.getString(R.string.time_year), mContext.getString(R.string.time_years));
        } else if(period.getMonths() != 0) {
            builder.appendMonths().appendSuffix(mContext.getString(R.string.time_month), mContext.getString(R.string.time_months));
        } else if(period.getDays() != 0) {
            builder.appendDays().appendSuffix(mContext.getString(R.string.time_day), mContext.getString(R.string.time_days));
        } else if(period.getHours() != 0) {
            builder.appendHours().appendSuffix(mContext.getString(R.string.time_hour), mContext.getString(R.string.time_hours));
        } else if(period.getMinutes() != 0) {
            builder.appendMinutes().appendSuffix(mContext.getString(R.string.time_minute), mContext.getString(R.string.time_minutes));
        } else if(period.getSeconds() != 0) {
            builder.appendSeconds().appendSuffix(mContext.getString(R.string.time_second), mContext.getString(R.string.time_seconds));
        }
        PeriodFormatter formatter = builder.printZeroNever().toFormatter();
        String elapsed = formatter.print(period);

        holder.roomPublished.setText(elapsed);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onSearchFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.room_picture) ImageView roomPinture;
        @BindView(R.id.room_price) MoneyTextView roomPrice;
        @BindView(R.id.room_title) TextView roomTitle;
        @BindView(R.id.room_address) TextView roomAddress;
        @BindView(R.id.room_available) TextView roomAvailableFrom;
        @BindView(R.id.room_published) TextView roomPublished;
        @BindView(R.id.room_count) TextView roomCount;
        @BindView(R.id.room_distance) TextView roomDistance;
        @BindView(R.id.room_card) CardView roomCard;

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
