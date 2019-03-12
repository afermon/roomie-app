package com.cosmicode.roomie.view;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomieState;
import com.cosmicode.roomie.domain.RoomieUser;
import com.cosmicode.roomie.service.RoomService;
import com.cosmicode.roomie.util.adapters.SearchRoomRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainHomeFragment extends Fragment implements RoomService.RoomServiceListener {

    private static final String ARG_SEARCH_QUERY = "search-query";
    private String searchQuery;

    @BindView(R.id.room_list) RecyclerView roomListRecyclerView;

    private OnFragmentInteractionListener mListener;
    private RoomService roomService;

    public MainHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchQuery Parameter 1.
     * @return A new instance of fragment MainHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainHomeFragment newInstance(String searchQuery) {
        MainHomeFragment fragment = new MainHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomService = new RoomService(getContext(), this);
        if (getArguments() != null) {
            searchQuery = getArguments().getString(ARG_SEARCH_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_home, container, false);
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
        roomService.getAllRooms();
    }

    public void updateUserTestInfo(RoomieUser roomieUser) {
        /*TextView textView = getView().findViewById(R.id.home_test_textview);
        textView.setText(roomieUser.toString());*/
    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {
        Context context = getView().getContext();
        roomListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        roomListRecyclerView.setAdapter(new SearchRoomRecyclerViewAdapter(rooms, mListener));
    }

    @Override
    public void OnGetRoomsError(String error) {

    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
        void onSearchFragmentInteraction(Room item);
    }
}
