package com.cosmicode.roomie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.enumeration.Gender;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnCreateRoomieListener;
import com.cosmicode.roomie.util.listeners.OnGetUserEmailListener;
import com.cosmicode.roomie.util.listeners.OnRegisterListener;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class RegisterActivity extends BaseActivity implements OnRegisterListener, OnGetUserEmailListener, OnCreateRoomieListener {

    private Gender gender;
    private Button maleButton, femaleButton;
    private ImageButton datePicker;
    private TextView dateText;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText password, confirmPassword, email, name, lastName;
    private Button register;
    private RoomieService roomieService;
    private String date;
    private ConstraintLayout container;
    private ProgressBar progress;
    private JhiAccount user;
    public static final String USER_NAME = "name";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        maleButton = findViewById(R.id.male_button);
        femaleButton = findViewById(R.id.female_button);
        datePicker = findViewById(R.id.date_picker);
        password = findViewById(R.id.edit_pw);
        confirmPassword = findViewById(R.id.edit_confirm);
        password.setTransformationMethod(new PasswordTransformationMethod());
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        datePicker.setOnClickListener(this::onClickDate);
        dateText = findViewById(R.id.date_text);
        email = findViewById(R.id.edit_email);
        name = findViewById(R.id.edit_first);
        lastName = findViewById(R.id.edit_last);
        register = findViewById(R.id.register_button);
        register.setOnClickListener(this::onClickRegister);
        roomieService = new RoomieService(this);
        container = findViewById(R.id.register_container);
        progress = findViewById(R.id.progress);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateText.setText(dayOfMonth + "/" + month + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if(month <= 9){
                    monthS = "0"+month;
                }

                if(dayOfMonth <= 9){
                    dayS = "0"+dayOfMonth;
                }
                date = year+"-"+monthS+"-"+dayS;
            }
        };
        JodaTimeAndroid.init(this);
    }

    public void onClickRegister(View view) {
        showProgress(true);
        getJhiUsers().register(email.getText().toString(), name.getText().toString(), lastName.getText().toString(), password.getText().toString(), this);
    }

    public void onClickDate(View view) {
        DateTime max = new DateTime().minusYears(18);
        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.show();
    }

    public void onClickMale(View view) {
        activeGender(maleButton, femaleButton);
        gender = Gender.MALE;
    }

    public void onClickFemale(View view) {
        activeGender(femaleButton, maleButton);
        gender = Gender.FEMALE;
    }

    public void activeGender(Button active, Button inactive) {
        active.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
        active.setTextColor(ContextCompat.getColor(this, R.color.white));
        inactiveGender(inactive);
    }

    public void inactiveGender(Button inactive) {
        inactive.setBackgroundTintList(null);
        inactive.setTextColor(ContextCompat.getColor(this, R.color.black));
    }


    @Override
    public void onRegisterSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        getJhiUsers().findByEmail(email.getText().toString(), this);
    }

    @Override
    public void onRegisterError(String error) {
        showProgress(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onGetUserSuccess(JhiAccount user) {
        this.user = user;
        Roomie roomie = new Roomie(date, "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png", gender, null, null, "", user.getId(), null, null,null, null );
        roomieService.createRoomie(roomie, this);
    }

    @Override
    public void onGetUserError(String error) {
        showProgress(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateRoomieSuccess(Roomie roomie) {
        showProgress(false);
        Intent intent = new Intent(this, RegisterSuccess.class);
        String name = user.getFirstName();
        intent.putExtra(USER_NAME, name);
        startActivity(intent);
    }

    @Override
    public void onCreateRoomieError(String error) {
        showProgress(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

    }

    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

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
