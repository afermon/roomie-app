package com.cosmicode.roomie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnGetRoomieByIdListener;

import java.util.ArrayList;
import java.util.List;

public class ChoosePremiumMembers extends BaseActivity implements OnGetRoomieByIdListener {

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

    private Room premiumRoom;
    private RoomieService roomieService;
    private MembersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_premium_members);
        ButterKnife.bind(this);
        roomieService = new RoomieService(this);
        premiumRoom = new Room();
        premiumRoom.setRoomies(new ArrayList<>());
        members.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new MembersAdapter(premiumRoom.getRoomies());
        members.setAdapter(mAdapter);
        addMember.setOnClickListener(l -> {
            showProgress(true);
            hideKeyboard();
            roomieService.getRoomieByEmail(roomieEmail.getText().toString(), this);
        });
        payment.setOnClickListener(l -> {
            startActivity(new Intent(this, PaymentActivity.class));
        });
    }

    @Override
    public void OnGetRoomieByIdSuccess(Roomie roomie) {
        showProgress(false);
        clearEmail();
        premiumRoom.getRoomies().add(roomie);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetRoomieError(String error) {
        showProgress(false);
        clearEmail();
        Toast.makeText(this,  String.format("%s %s", roomieEmail.getText().toString(), "does not exist"), Toast.LENGTH_SHORT).show();
    }

    private void clearEmail(){
        roomieEmail.setText(null);
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
            Glide.with(holder.itemView).load(roomie.getPicture()).centerCrop().into(holder.pfp);
            holder.remove.setOnClickListener(l -> {
                premiumRoom.getRoomies().remove(roomie);
                mAdapter.notifyDataSetChanged();
            });
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
}
