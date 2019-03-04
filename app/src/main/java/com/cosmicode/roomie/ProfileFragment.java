package com.cosmicode.roomie;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.RoomieUser;
import com.cosmicode.roomie.domain.enumeration.Gender;
import com.cosmicode.roomie.service.RoomieService;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment implements RoomieService.OnGetCurrentRoomieListener {

    private OnFragmentInteractionListener mListener;

    private Roomie currentRoomie;
    private RoomieService roomieService;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        roomieService = new RoomieService(getContext(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mListener != null)
            roomieService.getCurrentRoomie();
        mListener.getBaseActivity().getJhiUsers().getLogedUser(user -> fillProfileInfo(user));
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this::openEdit);
    }

    public void openEdit(View view) {
        EditProfile editProfile = EditProfile.newInstance("", "");
        openFragment(editProfile);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void fillProfileInfo(RoomieUser roomieUser) {
        TextView name = getView().findViewById(R.id.nameTxt);
        name.setText(roomieUser.getFullName());

        TextView email = getView().findViewById(R.id.mailTxt);
        email.setText(roomieUser.getEmail());

    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        this.currentRoomie = roomie;
        ImageView pfp = getView().findViewById(R.id.profileImg);
        Glide.with(getActivity().getApplicationContext()).load(currentRoomie.getPicture()).centerCrop().into(pfp);

        TextView phone = getView().findViewById(R.id.phoneTxt);
        phone.setText(getString(R.string.profile_phone, roomie.getPhone()));

        TextView age = getView().findViewById(R.id.ageTxt);
        age.setText(getString(R.string.profile_age, calculateAge(roomie.getBirthDate(), new Date())));

        TextView gender = getView().findViewById(R.id.genderTxt);
        gender.setText(getString(R.string.profile_gender, getEnumString(roomie.getGender())));

        TextView bio = getView().findViewById(R.id.bioTxt);
        bio.setText(roomie.getBiography());
    }

    public String calculateAge(Date birthDate, Date currentDate) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(formatter.format(birthDate));
        int d2 = Integer.parseInt(formatter.format(currentDate));
        int age = (d2 - d1) / 10000;
        return Integer.toString(age);
    }

    public String getEnumString(Gender gender) {
        String stringId = "";
        switch (gender) {
            case MALE:
                stringId = getResources().getString(R.string.enum_male);
            break;
            case FEMALE:
                stringId = getResources().getString(R.string.enum_female);
            break;
            case OTHER:
                stringId =getResources().getString(R.string.enum_other);
            break;
        }
        return stringId;
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        Log.e("Profile", error);
    }


    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
    }
}
