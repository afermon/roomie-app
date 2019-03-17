package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.service.RoomService;
import com.cosmicode.roomie.util.adapters.SearchRoomRecyclerViewAdapter;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSearchFragment extends Fragment implements RoomService.RoomServiceListener, SearchView.OnCloseListener {

    private static final String TAG = "SearchFragment";
    private static final String ARG_SEARCH_QUERY = "search-query";
    private String searchQuery;

    @BindView(R.id.room_list) RecyclerView roomListRecyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.search_layout) ConstraintLayout searchLayout;
    @BindView(R.id.no_results) TextView noResults;

    private OnFragmentInteractionListener mListener;
    private RoomService roomService;

    public MainSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchQuery Parameter 1.
     * @return A new instance of fragment MainSearchFragment.
     */
    public static MainSearchFragment newInstance(String searchQuery) {
        MainSearchFragment fragment = new MainSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getContext());
        roomService = new RoomService(getContext(), this);
        if (getArguments() != null) {
            searchQuery = getArguments().getString(ARG_SEARCH_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_search, container, false);
        ButterKnife.bind(this, view);
        return view;
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
        searchView.setIconified(false);
        searchView.setOnCloseListener(this);
        searchView.setQueryHint("Search....");
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(Color.WHITE);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = searchPlate.findViewById(searchTextId);
            if (searchText!=null) {
                searchText.setTextColor(getActivity().getResources().getColor(R.color.light));
                searchText.setHintTextColor(getActivity().getResources().getColor(R.color.light));
            }
        }

        showProgress(true);

        roomListRecyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                //searchLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                searchLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                //searchLayout.animate().translationY(searchLayout.getHeight() - 8).setInterpolator(new AccelerateInterpolator(2)).start();
                searchLayout.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: Submit search
                showProgress(true);
                roomService.serachRooms(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO: Typeahead
                return false;
            }
        });

        roomService.getAllRooms();
    }

    private void showProgress(boolean show) {
        if(show) noResults.setVisibility(View.INVISIBLE);

        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        roomListRecyclerView.setVisibility(((show) ? View.GONE : View.VISIBLE));

        roomListRecyclerView.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        roomListRecyclerView.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

        progressBar.setVisibility(((show) ? View.VISIBLE : View.GONE));
        progressBar.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 1 : 0))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setVisibility(((show) ? View.VISIBLE : View.GONE));
                    }
                });
    }


    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {
        if (rooms.size() > 0){
            noResults.setVisibility(View.INVISIBLE);
            roomListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            roomListRecyclerView.setAdapter(new SearchRoomRecyclerViewAdapter(rooms, mListener));
        } else
            noResults.setVisibility(View.VISIBLE);

        showProgress(false);
    }

    @Override
    public void OnGetRoomsError(String error) {

    }

    @Override //Search view close
    public boolean onClose() {
        return true;
    }

    public interface OnFragmentInteractionListener {
        BaseActivity getBaseActivity();
        void onSearchFragmentInteraction(Room item);
    }


    public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {
        static final float MINIMUM = 100;
        int scrollDist = 0;
        boolean isVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (isVisible && scrollDist > MINIMUM) {
                hide();
                scrollDist = 0;
                isVisible = false;
            }
            else if (!isVisible && scrollDist < -MINIMUM) {
                show();
                scrollDist = 0;
                isVisible = true;
            }

            if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                scrollDist += dy;
            }
        }

        public abstract void show();
        public abstract void hide();
    }
}
