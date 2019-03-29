package com.cosmicode.roomie;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.view.ListingBasicInformation;
import com.cosmicode.roomie.view.ListingChooseLocation;
import com.cosmicode.roomie.view.ListingChoosePictures;
import com.cosmicode.roomie.view.ListingStepChooseType;
import com.cosmicode.roomie.view.ListingCost;

public class CreateListingActivity extends BaseActivity implements ListingChooseLocation.OnFragmentInteractionListener, ListingBasicInformation.OnFragmentInteractionListener, ListingChoosePictures.OnFragmentInteractionListener, ListingStepChooseType.OnFragmentInteractionListener, ListingCost.OnFragmentInteractionListener {

    private RoomCreate room;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        room = new RoomCreate();
        room.setState(RoomState.SEARCH);
        room.setLookingForRoomie(true);

        setContentView(R.layout.activity_create_listing);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.listing_container, ListingBasicInformation.newInstance(room));
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public BaseActivity getBaseActivity() {
        return this;
    }



}
