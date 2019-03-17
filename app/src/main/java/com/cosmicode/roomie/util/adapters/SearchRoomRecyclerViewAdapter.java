package com.cosmicode.roomie.util.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.view.MainSearchFragment.OnFragmentInteractionListener;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

    public SearchRoomRecyclerViewAdapter(List<Room> items, OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        holder.roomCount.setText("x" + mValues.get(position).getRooms());

        holder.roomAvailableFrom.setText(mValues.get(position).getAvailableFrom());

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
