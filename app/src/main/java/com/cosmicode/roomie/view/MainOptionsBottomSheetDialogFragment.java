package com.cosmicode.roomie.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.CreateListingActivity;
import com.cosmicode.roomie.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainOptionsBottomSheetDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainOptionsBottomSheetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainOptionsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.options_name) TextView name;

    @BindView(R.id.options_mail) TextView email;

    private OnFragmentInteractionListener mListener;

    public MainOptionsBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainOptionsBottomSheetDialogFragment.
     */
    public static MainOptionsBottomSheetDialogFragment newInstance() {
        MainOptionsBottomSheetDialogFragment fragment = new MainOptionsBottomSheetDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_options, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(mListener != null)
        mListener.getBaseActivity().getJhiUsers().getLogedUser(user -> {
            name.setText(user.getFullName());
            email.setText(user.getEmail());
        });
    }

    @OnClick(R.id.option_room)
    public void optionRoom() {
        startActivity(new Intent(getContext(), CreateListingActivity.class));
        this.dismiss();
    }

    @OnClick(R.id.option_appointments)
    public void optionAppointments() {
        AppointmentsListFragment appointmentsListFragment = new AppointmentsListFragment();
        openFragment(appointmentsListFragment);
        this.dismiss();
    }

    @OnClick(R.id.option_subscriptions)
    public void optionSubscriptions() {
        this.dismiss();
    }

    @OnClick(R.id.option_configuration)
    public void optionConfiguration() {
        MainConfigurationFragment mainConfigurationFragment = MainConfigurationFragment.newInstance("","");
        openFragment(mainConfigurationFragment);
        this.dismiss();
    }

    @OnClick(R.id.option_report_problem)
    public void optionReportProblem() {
        NewExpenseFragment todoFragment = NewExpenseFragment.newInstance(null);
        openFragment(todoFragment);
        this.dismiss();
    }

    @OnClick(R.id.option_logout)
    public void optionLogout() {
        if (mListener != null)  mListener.performLogout();
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

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void performLogout();

        BaseActivity getBaseActivity();
    }
}
