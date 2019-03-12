package com.cosmicode.roomie.util.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.view.MainHomeFragment.OnFragmentInteractionListener;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Room} and makes a call to the
 * specified {@link OnFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
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
        /*holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getContent());
        holder.mDateView.setText(mValues.get(position).getCreated());*/

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSearchFragmentInteraction(holder.mItem);
                }
            }
        });
}

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
       /* public final TextView mTitleView;
        public final TextView mContentView;
        public final TextView mDateView;*/
        public Room mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            /*mTitleView = (TextView) view.findViewById(R.id.item_title);
            mContentView = (TextView) view.findViewById(R.id.item_content);
            mDateView = (TextView) view.findViewById(R.id.item_date);*/
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}
