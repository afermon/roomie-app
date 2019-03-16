package com.cosmicode.roomie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.view.ListingStep1;
import com.cosmicode.roomie.view.ListingStep2;

public class CreateListingActivity extends BaseActivity implements ListingStep1.OnFragmentInteractionListener, ListingStep2.OnFragmentInteractionListener {

    private Room room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
