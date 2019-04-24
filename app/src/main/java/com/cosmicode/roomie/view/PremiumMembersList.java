package com.cosmicode.roomie.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.ChoosePremiumMembers;
import com.cosmicode.roomie.R;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnGetRoomieByIdListener;

import java.util.List;

public class PremiumMembersList extends Fragment implements OnGetRoomieByIdListener, RoomieService.OnGetCurrentRoomieListener {

    private OnFragmentInteractionListener mListener;
    private static final String ROOM = "room";

    @BindView(R.id.invite_email)
    EditText email;
    @BindView(R.id.members)
    RecyclerView members;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.add_roomie2)
    ImageView addMember;
    @BindView(R.id.invi_title)
    TextView title;

    private MembersAdapter mAdapter;
    private Room room;
    private Roomie current;
    private RoomieService roomieService;

    public PremiumMembersList() {
        // Required empty public constructor
    }


    public static PremiumMembersList newInstance(Room room) {
        PremiumMembersList fragment = new PremiumMembersList();
        Bundle args = new Bundle();
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomieService = new RoomieService(getContext(), this);
        addMember.setOnClickListener(l -> {
            email.setError(null);
            if (email.getText().toString().equals("")) {
                email.setError("This field is required");
            }else{
                showProgress(true);
                mListener.hideKeyboard();
                roomieService.getRoomieByEmail(email.getText().toString(), this);
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(true);
        roomieService.getCurrentRoomie();
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        members.setVisibility(((show) ? View.GONE : View.VISIBLE));

        members.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        members.setVisibility(((show) ? View.GONE : View.VISIBLE));
                    }
                });

        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
        progress.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 1 : 0))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(((show) ? View.VISIBLE : View.GONE));
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_premium_members_list, container, false);
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
    public void OnGetRoomieByIdSuccess(Roomie roomie) {
        showProgress(false);
        clearEmail();
        if(room.getRoomies().stream().anyMatch(r -> r.getId().equals(roomie.getId()))) {
            ((BaseActivity) getContext()).showUserMessage(roomie.getUser().getEmail() + " is already part of the room", BaseActivity.SnackMessageType.WARNING);
        }else{
            room.getRoomies().add(roomie);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void clearEmail() {
        email.setText(null);
    }


    @Override
    public void onGetRoomieError(String error) {
        showProgress(false);
        ((BaseActivity) getContext()).showUserMessage(String.format("%s %s", email.getText().toString(), "does not exist"), BaseActivity.SnackMessageType.WARNING);
        clearEmail();
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        current = roomie;
        if (!current.getId().equals(room.getOwnerId())){
            title.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            addMember.setVisibility(View.GONE);
        }
        room = getArguments().getParcelable(ROOM);
        members.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mAdapter = new MembersAdapter(room.getRoomies());
        members.setAdapter(mAdapter);
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        showProgress(false);
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }

    public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.IconViewHolder> {
        private List<Roomie> roomies;

        public class IconViewHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private ImageView pfp;
            private ImageButton remove;

            IconViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                pfp = view.findViewById(R.id.pfp);
                remove = view.findViewById(R.id.remove);
            }

        }

        public MembersAdapter(List<Roomie> roomies) {
            this.roomies = roomies;
        }

        public int getItemCount() {
            return roomies.size();
        }

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.roomie_item, viewGroup, false);
            IconViewHolder vh = new IconViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final IconViewHolder holder, int position) {
            Roomie roomie = roomies.get(position);
            holder.name.setText(roomie.getUser().getFirstName());
            if(current.getId().equals(roomie.getId())){
                holder.remove.setVisibility(View.GONE);
            }
            if(current.getId().equals(room.getOwnerId())){
                holder.remove.setVisibility(View.GONE);
            }
            Glide.with(holder.itemView).load(roomie.getPicture()).centerCrop().into(holder.pfp);
            holder.remove.setOnClickListener(l -> {
                room.getRoomies().remove(roomie);
                mAdapter.notifyDataSetChanged();
            });

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void hideKeyboard();
    }
}
