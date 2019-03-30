package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.asksira.bsimagepicker.BSImagePicker;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.RoomExpense;

import java.util.ArrayList;
import java.util.List;

public class ListingChoosePictures extends Fragment implements BSImagePicker.OnMultiImageSelectedListener {


    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";

    private RoomCreate room;
    private BSImagePicker multiSelectionPicker;

    @BindView(R.id.btn_next)
    Button next;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.pic_container)
    ConstraintLayout cont;

    @BindViews({R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5})
    List<ImageView> pics;


    public ListingChoosePictures() {
        // Required empty public constructor
    }

    public static ListingChoosePictures newInstance(RoomCreate room) {
        ListingChoosePictures fragment = new ListingChoosePictures();
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
            multiSelectionPicker = new BSImagePicker.Builder("com.cosmicode.fileprovider")
                    .isMultiSelect()
                    .setMinimumMultiSelectCount(1)
                    .setMaximumMultiSelectCount(5)
                    .setMultiSelectBarBgColor(R.color.secondary)
                    .setMultiSelectTextColor(R.color.primary)
                    .setMultiSelectDoneTextColor(R.color.primary)
                    .setOverSelectTextColor(R.color.danger)
                    .disableOverSelectionMessage()
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_choose_pictures, container, false);
        mListener.changePercentage(75);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        if(room.getPicturesUris() == null){
            room.setPicturesUris(new ArrayList<>());
        }else{
            int i = 0;
            for (Uri uri : room.getPicturesUris()) {
                pics.get(i).setImageURI(uri);
                i++;
            }
        }
        showProgress(false);
    }

    @OnClick(R.id.btn_next)
    public void onClickNext(View view) {
        mListener.openFragment(ListingChooseLocation.newInstance(room), "right");
    }

    @OnClick(R.id.cancel_pic)
    public void finish(View view) {
        getActivity().finish();
    }

    @OnClick(R.id.back_button3)
    public void back(View view) {
        mListener.openFragment(ListingCost.newInstance(room), "left");
    }

    @OnClick(R.id.img1)
    public void choosePictures(View view) {
        multiSelectionPicker.show(getChildFragmentManager(), "picker");
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
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        int i = 0;
        for (ImageView pic : pics) {
            pic.setImageResource(R.drawable.ph);
        }
        for (Uri uri : uriList) {
            pics.get(i).setImageURI(uri);
            i++;
        }
        room.setPicturesUris(uriList);
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
        void openFragment(Fragment fragment, String start);
        void changePercentage(int progress);
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        cont.setVisibility(((show) ? View.GONE : View.VISIBLE));

        cont.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cont.setVisibility(((show) ? View.GONE : View.VISIBLE));
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
}
