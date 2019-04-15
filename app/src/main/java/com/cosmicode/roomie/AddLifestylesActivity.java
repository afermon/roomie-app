package com.cosmicode.roomie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.FeatureType;
import com.cosmicode.roomie.service.RoomFeatureService;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddLifestylesActivity extends BaseActivity implements RoomFeatureService.OnGetFeaturesListener {

    private List<RoomFeature> allLifestyles;
    private List<RoomFeature> addedLifestyles;
    private RecyclerView.Adapter mAdapterAll, mAdapterAdded;
    private RoomFeatureService roomFeatureService;

    @BindView(R.id.all_recycler)
    RecyclerView allRecycler;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.lifestyles_constraint)
    ConstraintLayout cont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lifestyles);
        ButterKnife.bind(this);
        roomFeatureService = new RoomFeatureService(this, this);
        FlexboxLayoutManager managerAll = new FlexboxLayoutManager(this);
        managerAll.setFlexDirection(FlexDirection.ROW);
        managerAll.setJustifyContent(JustifyContent.FLEX_START);
        Roomie roomie = getIntent().getParcelableExtra("lifestyles");
        addedLifestyles = roomie.getLifestyles();
        allRecycler.setLayoutManager(managerAll);
        showProgress(true);
        roomFeatureService.getAll();
    }

    public class AllLifestylesAdapter extends RecyclerView.Adapter<AllLifestylesAdapter.TagViewHolder> {
        private List<RoomFeature> allLifestyles;

        public class TagViewHolder extends RecyclerView.ViewHolder {

            private TextView tag;

            TagViewHolder(View view) {
                super(view);
                tag = view.findViewById(R.id.tag);
            }

        }

        public AllLifestylesAdapter(List<RoomFeature> allLifestyles) {
            this.allLifestyles = allLifestyles;
        }

        public int getItemCount() {
            return allLifestyles.size();
        }

        @NonNull
        @Override
        public TagViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lifestyle_item, viewGroup, false);
            TagViewHolder vh = new TagViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final TagViewHolder holder, int position) {
            ColorStateList primary = ContextCompat.getColorStateList(getApplicationContext(), R.color.primary);
            ColorStateList accent = ContextCompat.getColorStateList(getApplicationContext(), R.color.grey);

            RoomFeature lifestyle = this.allLifestyles.get(position);
            holder.tag.setText(lifestyle.getName());
            if(isPresent(lifestyle)){
                holder.tag.setBackgroundTintList(primary);
            }else{
                holder.tag.setBackgroundTintList(accent);
            }

            holder.tag.setOnClickListener(l -> {
                if(holder.tag.getBackgroundTintList() == primary){
                    addedLifestyles.remove(addedLifestyles.stream().filter(f -> f.getId().equals(lifestyle.getId())).findAny().get());
                    holder.tag.setBackgroundTintList(accent);
                }else{
                    addedLifestyles.add(lifestyle);
                    holder.tag.setBackgroundTintList(primary);
                }
            });
        }
    }

    public boolean isPresent(RoomFeature ls){
        return addedLifestyles.stream().anyMatch(f -> f.getId().equals(ls.getId()));
    }


    @Override
    public void onGetFeaturesSuccess(List<RoomFeature> featureList) {
        allLifestyles = featureList.stream().filter(f -> f.getType() == FeatureType.LIFESTYLE).collect(Collectors.toList());

        mAdapterAll = new AllLifestylesAdapter(allLifestyles);
        allRecycler.setAdapter(mAdapterAll);
        showProgress(false);
    }

    @Override
    public void onGetFeaturesError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.finish_activity)
    public void cancelActivity(View view){
        finish();
    }

    @OnClick(R.id.btn_finish)
    public void finishLifeStyles(View view){
        ArrayList<RoomFeature> lsReturn = new ArrayList<>(addedLifestyles);
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("newLifestyles", lsReturn);
        setResult(2, intent);
        finish();
    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        cont.setVisibility(((show) ? View.GONE : View.VISIBLE));

        cont.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cont.setVisibility(((show) ? View.GONE : View.VISIBLE));
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
}
