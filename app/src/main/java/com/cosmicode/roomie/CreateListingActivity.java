package com.cosmicode.roomie;

import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

import com.cosmicode.roomie.view.ListingChoosePictures;
import com.cosmicode.roomie.view.ListingStepChooseType;
import com.cosmicode.roomie.view.ListingStepCost;

public class CreateListingActivity extends BaseActivity implements ListingChoosePictures.OnFragmentInteractionListener, ListingStepChooseType.OnFragmentInteractionListener, ListingStepCost.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.listing_container, ListingStepChooseType.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
