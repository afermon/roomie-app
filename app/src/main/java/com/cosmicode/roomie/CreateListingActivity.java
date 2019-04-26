package com.cosmicode.roomie;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.view.ListingBasicInformation;
import com.cosmicode.roomie.view.ListingChooseLocation;
import com.cosmicode.roomie.view.ListingChoosePictures;
import com.cosmicode.roomie.view.ListingCost;
import com.cosmicode.roomie.view.ListingStepChooseType;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CreateListingActivity extends BaseActivity implements ListingChooseLocation.OnFragmentInteractionListener, ListingBasicInformation.OnFragmentInteractionListener, ListingChoosePictures.OnFragmentInteractionListener, ListingStepChooseType.OnFragmentInteractionListener, ListingCost.OnFragmentInteractionListener {

    private RoomCreate room;
    private ProgressBar stepBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        room = new RoomCreate();
        room.setState(RoomState.SEARCH);
        room.setLookingForRoomie(true);
        room.setFeatures(null);
        room.setMonthly(null);
        room.setAddress(null);
        room.setPicturesUris(null);
        stepBar = findViewById(R.id.step_bar);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.listing_container, ListingBasicInformation.newInstance(room, false));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openFragment(Fragment fragment, String start) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (start) {
            case "left":
                transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, 0, 0);
                break;
            case "right":
                transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
                break;
            case "up":
        }
        transaction.replace(R.id.listing_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void changePercentage(int progress) {
        stepBar.setProgress(progress);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public Double getAmount() {
        return null;
    }

}
