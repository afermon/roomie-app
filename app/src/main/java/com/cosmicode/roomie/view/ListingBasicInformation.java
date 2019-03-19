package com.cosmicode.roomie.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ListingChooseLocation;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.service.RoomFeatureService;

import android.graphics.PorterDuff.Mode;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ListingBasicInformation extends Fragment implements RoomFeatureService.OnGetFeaturesListener {

    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private Room room;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mAdapter2;
    private RoomFeatureService roomFeatureService;

    @BindView(R.id.headline_text)
    TextView headline;

    @BindView(R.id.desc_text)
    TextView desc;

    @BindView(R.id.list_amenities)
    RecyclerView amenities;

    @BindView(R.id.list_restrictions)
    RecyclerView restrictions;

    public ListingBasicInformation() {
        // Required empty public constructor
    }

    public static ListingBasicInformation newInstance(Room room) {
        ListingBasicInformation fragment = new ListingBasicInformation();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = getArguments().getParcelable(ROOM);
            room.setFeatures(new ArrayList<>());
            roomFeatureService = new RoomFeatureService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_basic_information, container, false);
        ButterKnife.bind(this, view);
        roomFeatureService.getAll();
        return view;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        amenities.setLayoutManager(new GridLayoutManager(getContext(), 4));
        restrictions.setLayoutManager(new GridLayoutManager(getContext(), 4));

    }

    @Override
    public void onGetFeaturesSuccess(List<RoomFeature> featureList) {
        List<RoomFeature> lAmenities = new ArrayList<>();
        List<RoomFeature> lRestrictions = new ArrayList<>();
        for (RoomFeature feature : featureList) {
            if(feature.getType() == FeatureType.AMENITIES){
                lAmenities.add(feature);
            }else{
                lRestrictions.add(feature);
            }
        }

        mAdapter = new AmenitiesAdapter(lAmenities);
        amenities.setAdapter(mAdapter);
        mAdapter2 = new RestrictionsAdapter(lRestrictions);
        restrictions.setAdapter(mAdapter2);
    }

    @Override
    public void onGetFeaturesError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.IconViewHolder> {
        private List<RoomFeature> features;

        public class IconViewHolder extends RecyclerView.ViewHolder {

            private TextView iconText;
            private ImageButton icon;
            IconViewHolder(View view) {
                super(view);
                iconText = view.findViewById(R.id.icon_text);
                icon = view.findViewById(R.id.icon);
            }

        }

        public AmenitiesAdapter(List<RoomFeature> features) {
            this.features = features;
        }

        public int getItemCount() {
            return features.size();
        }

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.icon_item, viewGroup, false);
            IconViewHolder vh = new IconViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final IconViewHolder holder, int position) {

            RoomFeature feature = this.features.get(position);
            holder.iconText.setText(feature.getName());
            Glide.with(holder.itemView).load(feature.getIcon()).centerCrop().into(holder.icon);
            holder.icon.setOnClickListener( v -> {
                if(holder.iconText.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.primary)){
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                    room.getFeatures().add(feature);
                }else{
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                    Iterator<RoomFeature> itr = room.getFeatures().iterator();
                    while (itr.hasNext()) {
                        if (itr.next() == feature) {
                            itr.remove();
                        }
                    }
                }
            });
        }
    }

    public class RestrictionsAdapter extends RecyclerView.Adapter<RestrictionsAdapter.IconViewHolder> {
        private List<RoomFeature> features;

        public class IconViewHolder extends RecyclerView.ViewHolder {

            private TextView iconText;
            private ImageButton icon;
            IconViewHolder(View view) {
                super(view);
                iconText = view.findViewById(R.id.icon_text);
                icon = view.findViewById(R.id.icon);
            }

        }

        public RestrictionsAdapter(List<RoomFeature> features) {
            this.features = features;
        }

        public int getItemCount() {
            return features.size();
        }

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.icon_item, viewGroup, false);
            IconViewHolder vh = new IconViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final IconViewHolder holder, int position) {

            RoomFeature feature = this.features.get(position);
            holder.iconText.setText(feature.getName());
            Glide.with(holder.itemView).load(feature.getIcon()).centerCrop().into(holder.icon);
            holder.icon.setOnClickListener( v -> {
                if(holder.iconText.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.primary)){
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                    room.getFeatures().add(feature);
                }else{
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                    Iterator<RoomFeature> itr = room.getFeatures().iterator();
                    while (itr.hasNext()) {
                        if (itr.next() == feature) {
                            itr.remove();
                        }
                    }
                }
            });
        }
    }

    @OnClick(R.id.btn_next)
    public void onClickNext(View view){
        room.setTitle(headline.getText().toString());
        room.setDescription(headline.getText().toString());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
        transaction.replace(R.id.listing_container, ListingStepCost.newInstance(room) );
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
