package com.cosmicode.roomie;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

import com.cosmicode.roomie.view.ListingBasicInformation;
import com.cosmicode.roomie.view.ListingChoosePictures;
import com.cosmicode.roomie.view.ListingStepChooseType;
import com.cosmicode.roomie.view.ListingStepCost;

public class CreateListingActivity extends BaseActivity implements ListingChooseLocation.OnFragmentInteractionListener, ListingBasicInformation.OnFragmentInteractionListener, ListingChoosePictures.OnFragmentInteractionListener, ListingStepChooseType.OnFragmentInteractionListener, ListingStepCost.OnFragmentInteractionListener {

    private Fragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.listing_container, ListingStepChooseType.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public BaseActivity getBaseActivity() {
        return this;
    }



}
