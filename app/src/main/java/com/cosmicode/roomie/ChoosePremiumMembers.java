package com.cosmicode.roomie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.domain.enumeration.RoomType;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnGetRoomieByIdListener;

import java.util.ArrayList;
import java.util.List;

public class ChoosePremiumMembers extends BaseActivity implements OnGetRoomieByIdListener, RoomieService.OnGetCurrentRoomieListener {

    @BindView(R.id.room_name)
    EditText roomName;
    @BindView(R.id.roomie_email)
    EditText roomieEmail;
    @BindView(R.id.invited_recycler)
    RecyclerView members;
    @BindView(R.id.add_roomie)
    ImageView addMember;
    @BindView(R.id.cancel_premium)
    ImageButton cancel;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btn_payment)
    Button payment;


    private List<Roomie> addedMembers;
    private Roomie owner;
    private Room premiumRoom;
    private RoomieService roomieService;
    private MembersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_premium_members);
        ButterKnife.bind(this);
        roomieService = new RoomieService(this, this);
        premiumRoom = new Room();
        addedMembers = new ArrayList<>();
        members.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new MembersAdapter(addedMembers);
        members.setAdapter(mAdapter);
        roomieService.getCurrentRoomie();
        addMember.setOnClickListener(l -> {
            roomieEmail.setError(null);
            if (roomieEmail.getText().toString().equals("")) {
                roomieEmail.setError("This field is required");
            }else{
                showProgress(true);
                hideKeyboard();
                roomieService.getRoomieByEmail(roomieEmail.getText().toString(), this);
            }

        });
        payment.setOnClickListener(l -> {
            boolean isValid = true;
            hideKeyboard();
            roomieEmail.setError(null);
            roomName.setError(null);
            if (roomName.getText().toString().equals("")) {
                roomName.setError("This field is required");
                isValid = false;
            }
            if (addedMembers.isEmpty()) {
                roomieEmail.setError("You need at least 1 member");
                isValid = false;
            }

            if (isValid) {
                premiumRoom.setTitle(roomName.getText().toString());
                premiumRoom.setOwnerId(owner.getId());
                premiumRoom.setRoomies(new ArrayList<>(addedMembers));
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("premium", premiumRoom);
                intent.putExtra("owner", owner);
                startActivity(intent);
            }
        });
    }

    @Override
    public void OnGetRoomieByIdSuccess(Roomie roomie) {
        showProgress(false);
        clearEmail();
        if(addedMembers.stream().anyMatch(r -> r.getId().equals(roomie.getId())) || roomie.getId().equals(owner.getId())) {
            showUserMessage(roomie.getUser().getEmail() + " is already part of the room", SnackMessageType.WARNING);
        }else{
            addedMembers.add(roomie);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetRoomieError(String error) {
        showProgress(false);
        showUserMessage(String.format("%s %s", roomieEmail.getText().toString(), "does not exist"), SnackMessageType.WARNING);
        clearEmail();

    }

    private void clearEmail() {
        roomieEmail.setText(null);
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        owner = roomie;
    }

    @Override
    public void onGetCurrentRoomieError(String error) {

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
            if(!roomie.getId().equals(owner.getId())){
                holder.name.setText(roomie.getUser().getFirstName());
                Glide.with(holder.itemView).load(roomie.getPicture()).centerCrop().into(holder.pfp);
                holder.remove.setOnClickListener(l -> {
                    addedMembers.remove(roomie);
                    mAdapter.notifyDataSetChanged();
                });
            }
        }
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

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R.id.cancel_premium)
    public void cancelPremium (View view){
        finish();
    }
}
