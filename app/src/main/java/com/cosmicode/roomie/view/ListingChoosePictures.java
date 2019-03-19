package com.cosmicode.roomie.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ListingChooseLocation;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomExpense;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ListingChoosePictures extends Fragment implements BSImagePicker.OnMultiImageSelectedListener{



    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";
    private static final String ROOM_EXPENSE = "expense";

    private Room room;
    private RoomExpense expense;
    private BSImagePicker multiSelectionPicker;


    @BindView(R.id.btn_next)
    Button next;

    @BindViews({R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5})
    List<ImageView> pics;



    public ListingChoosePictures() {
        // Required empty public constructor
    }

    public static ListingChoosePictures newInstance(Room room, RoomExpense roomExpense) {
        ListingChoosePictures fragment = new ListingChoosePictures();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        args.putParcelable(ROOM_EXPENSE, roomExpense);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = getArguments().getParcelable(ROOM);
            expense = getArguments().getParcelable(ROOM_EXPENSE);
             multiSelectionPicker = new BSImagePicker.Builder("com.cosmicode.fileprovider")
                    .isMultiSelect() //Set this if you want to use multi selection mode.
                    .setMinimumMultiSelectCount(1) //Default: 1.
                    .setMaximumMultiSelectCount(5) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                    .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                    .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                    .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                    .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                    .disableOverSelectionMessage() //You can also decide not to show this over select message.
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_choose_pictures, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.btn_next)
    public void onClickNext(View view){
        room.setMonthly(expense);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
        transaction.replace(R.id.listing_container, ListingChooseLocation.newInstance(room) );
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.img1)
    public void choosePictures(View view){
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
            room.setPicturesUris(uriList);
            i++;
        }
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
