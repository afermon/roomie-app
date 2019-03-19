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
import com.cosmicode.roomie.ListingChooseLocation;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.enumeration.FeatureType;

import android.graphics.PorterDuff.Mode;

import java.util.ArrayList;
import java.util.List;


public class ListingBasicInformation extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private Room room;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mAdapter2;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_basic_information, container, false);
        ButterKnife.bind(this, view);
        RoomFeature feature1 = new RoomFeature();
        feature1.setName("WiFi");
        feature1.setIcon("http://aux2.iconspalace.com/uploads/wifi-icon-256-60417624.png");
        feature1.setType(FeatureType.AMENITIES);
        RoomFeature feature2 = new RoomFeature();
        feature2.setName("Furnished");
        feature2.setType(FeatureType.AMENITIES);
        feature2.setIcon("http://millshealth.com/wp-content/uploads/2018/08/icon-bed.png");
        List<RoomFeature> features = new ArrayList<>();
        features.add(feature1);
        features.add(feature2);
        features.add(feature2);
        features.add(feature2);
        features.add(feature2);
        features.add(feature2);
        features.add(feature2);
        List<RoomFeature> lAmenities = new ArrayList<>();
        List<RoomFeature> lRestrictions = new ArrayList<>();

        RoomFeature feature3 = new RoomFeature();
        feature3.setName("No smoking");
        feature3.setType(FeatureType.RESTRICTIONS);
        feature3.setIcon("https://cdn4.iconfinder.com/data/icons/dot/256/smoking_not_allowed.png");
        features.add(feature3);
        features.add(feature3);
        features.add(feature3);
        features.add(feature3);

        for (RoomFeature feature : features) {
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
                }else{
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
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
                }else{
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                }
            });
        }
    }

    @OnClick(R.id.btn_next)
    public void onClickNext(View view){
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
        void onFragmentInteraction(Uri uri);
    }
}
