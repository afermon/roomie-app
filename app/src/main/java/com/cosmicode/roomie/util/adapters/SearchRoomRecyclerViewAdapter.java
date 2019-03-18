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
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.view.MainSearchFragment.OnFragmentInteractionListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.DecimalFormat;
import java.util.List;

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
            Double priceUser = price.getAmount() / mValues.get(position).getRooms(); // Price per user
            if (price.getCurrency() == CurrencyType.DOLLAR) {
                holder.roomPrice.setText(String.format("%s %s %s", "$", priceUser.intValue(), "USD"));
            } else {
                holder.roomPrice.setText(String.format("%s %s %s", "₡", priceUser.intValue(), "CRC"));
            }

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
        //TODO: Replace for strings
        if(period.getYears() != 0) {
            builder.appendYears().appendSuffix(" year ago", " years ago");
        } else if(period.getMonths() != 0) {
            builder.appendMonths().appendSuffix(" month ago", " months ago");
        } else if(period.getDays() != 0) {
            builder.appendDays().appendSuffix(" day ago", " days ago");
        } else if(period.getHours() != 0) {
            builder.appendHours().appendSuffix(" hour ago", " hours ago");
        } else if(period.getMinutes() != 0) {
            builder.appendMinutes().appendSuffix(" minute ago", " minutes ago");
        } else if(period.getSeconds() != 0) {
            builder.appendSeconds().appendSuffix(" second ago"," seconds ago");
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
        @BindView(R.id.room_price) TextView roomPrice;
        @BindView(R.id.room_title) TextView roomTitle;
        @BindView(R.id.room_address) TextView roomAddress;
        @BindView(R.id.room_available) TextView roomAvailableFrom;
        @BindView(R.id.room_published) TextView roomPublished;
        @BindView(R.id.room_count) TextView roomCount;
        @BindView(R.id.room_distance) TextView roomDistance;

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
