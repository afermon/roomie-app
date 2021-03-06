package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.service.RoomFeatureService;
import com.cosmicode.roomie.service.RoomService;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ListingBasicInformation extends Fragment implements RoomFeatureService.OnGetFeaturesListener, Validator.ValidationListener, RoomService.RoomServiceListener {

    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private static final String IS_EDIT = "edit";
    private RoomCreate room;
    private Boolean isEdit;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mAdapter2;
    private RoomFeatureService roomFeatureService;
    private Validator validator;
    private RoomService roomService;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.basic_cont)
    ConstraintLayout cont;

    @BindView(R.id.basic_scroll)
    ScrollView scrollView;

    @BindView(R.id.number_room)
    TextView amount;

    @NotEmpty
    @Length(min = 4, max = 100)
    @BindView(R.id.headline_text)
    TextView headline;

    @NotEmpty
    @Length(min = 4, max = 2000)
    @BindView(R.id.desc_text)
    TextView desc;

    @BindView(R.id.list_amenities)
    RecyclerView amenities;

    @BindView(R.id.list_restrictions)
    RecyclerView restrictions;

    @BindView(R.id.btn_next)
    Button next;

    @BindView(R.id.back_button)
    ImageButton back;

    @BindView(R.id.back_basic)
    ImageButton cancel;

    public ListingBasicInformation() {
        // Required empty public constructor
    }

    public static ListingBasicInformation newInstance(RoomCreate room, Boolean editFlag) {
        ListingBasicInformation fragment = new ListingBasicInformation();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        args.putBoolean(IS_EDIT, editFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = getArguments().getParcelable(ROOM);
            isEdit = getArguments().getBoolean(IS_EDIT);
            if (room.getFeatures() == null) {
                room.setFeatures(new ArrayList<>());
            }
            validator = new Validator(this);
            validator.setValidationListener(this);
            roomFeatureService = new RoomFeatureService(getContext(), this);
            roomService = new RoomService(getContext(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_basic_information, container, false);
        mListener.changePercentage(25);
        ButterKnife.bind(this, view);
        validator = new Validator(this);
        validator.setValidationListener(this);
        showProgress(true);
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
        if (room.getTitle() != null) {
            headline.setText(room.getTitle());
        }

        if (room.getDescription() != null) {
            desc.setText(room.getDescription());
        }

        if (room.getRooms() != null) {
            amount.setText(String.format("%s", room.getRooms()));
        }

        if (isEdit) {
            back.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            next.setText(getString(R.string.button_save));
            scrollView.setPadding(0, 0, 0, 135);
            scrollView.setClipToPadding(false);
        } else {
            back.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            next.setText(getString(R.string.cont_btn));
        }
        amenities.setLayoutManager(new GridLayoutManager(getContext(), 4));
        restrictions.setLayoutManager(new GridLayoutManager(getContext(), 4));

    }

    @Override
    public void onGetFeaturesSuccess(List<RoomFeature> featureList) {
        List<RoomFeature> lAmenities = new ArrayList<>();
        List<RoomFeature> lRestrictions = new ArrayList<>();
        for (RoomFeature feature : featureList) {
            if (feature.getType() == FeatureType.AMENITIES) {
                lAmenities.add(feature);
            } else {
                if (feature.getType() == FeatureType.RESTRICTIONS) {
                    lRestrictions.add(feature);
                }
            }
        }

        mAdapter = new AmenitiesAdapter(lAmenities);
        amenities.setAdapter(mAdapter);
        mAdapter2 = new RestrictionsAdapter(lRestrictions);
        restrictions.setAdapter(mAdapter2);
        showProgress(false);
    }

    @Override
    public void onGetFeaturesError(String error) {
        ((BaseActivity) getContext()).showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
    }

    @Override
    public void onValidationSucceeded() {
        room.setTitle(headline.getText().toString());
        room.setDescription(desc.getText().toString());
        room.setRooms(Integer.parseInt(amount.getText().toString()));
        if (isEdit) {
            showProgress(true);
            roomService.updateRoom(room);
        } else {
            showProgress(true);
            mListener.openFragment(ListingCost.newInstance(room, false), "right");
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                ((BaseActivity) getContext()).showUserMessage(message, BaseActivity.SnackMessageType.ERROR);
            }
        }
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    @Override
    public void OnCreateSuccess(Room room) {

    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {

    }

    @Override
    public void OnGetRoomsError(String error) {
        ((BaseActivity) getContext()).showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
        showProgress(false);
    }

    @Override
    public void OnUpdateSuccess(Room room) {
        ((BaseActivity) getContext()).showUserMessage("Room updated successfully!", BaseActivity.SnackMessageType.SUCCESS);
        showProgress(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().popBackStack();
            }
        }, 1000);
    }

    @OnClick(R.id.back_button)
    public void goBack(View view) {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onPaySuccess(Room room) {

    }

    @Override
    public void onPayError(String error) {

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
            if (isPresent(feature)) {
                holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
            } else {
                holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
            }
            holder.icon.setOnClickListener(v -> {
                mListener.hideKeyboard();
                if (holder.iconText.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.primary)) {
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                    room.getFeatures().remove(room.getFeatures().stream().filter(f -> f.getId().equals(feature.getId())).findAny().get());
                } else {
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                    room.getFeatures().add(feature);
                }
            });
        }
    }

    @OnClick(R.id.add_number)
    public void increase(View view) {
        mListener.hideKeyboard();
        int number = Integer.parseInt(amount.getText().toString());
        amount.setText(String.format("%s", number + 1));
    }

    @OnClick(R.id.remove_number)
    public void decrease(View view) {
        mListener.hideKeyboard();
        int number = Integer.parseInt(amount.getText().toString());
        if (number > 1) {
            amount.setText(String.format("%s", number - 1));
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

            if (isPresent(feature)) {
                holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
            } else {
                holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
            }

            holder.icon.setOnClickListener(v -> {
                mListener.hideKeyboard();
                if (holder.iconText.getCurrentTextColor() == ContextCompat.getColor(getContext(), R.color.primary)) {
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                    room.getFeatures().remove(room.getFeatures().stream().filter(f -> f.getId().equals(feature.getId())).findAny().get());
                } else {
                    holder.iconText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
                    room.getFeatures().add(feature);
                }
            });
        }
    }


    public boolean isPresent(RoomFeature ls) {
        return room.getFeatures().stream().anyMatch(f -> f.getId().equals(ls.getId()));
    }

    @OnClick(R.id.btn_next)
    public void onClickNext(View view) {
        mListener.hideKeyboard();
        validator.validate();
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

    @OnClick(R.id.back_basic)
    public void back(View view) {
        getActivity().finish();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        scrollView.setVisibility(((show) ? View.GONE : View.VISIBLE));

        scrollView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scrollView.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
        progress.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 1 : 0))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
                    }
                });
    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();

        void openFragment(Fragment fragment, String start);

        void changePercentage(int progress);

        void hideKeyboard();
    }
}
