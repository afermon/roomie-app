package com.cosmicode.roomie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public class RegisterActivity extends BaseActivity implements OnRegisterListener, OnGetUserEmailListener, OnCreateRoomieListener, Validator.ValidationListener {
    private Gender gender;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Validator validator;
    private RoomieService roomieService;
    private String date;
    private JhiAccount user;
    public static final String USER_NAME = "name";
    private boolean isValid = true;

    @BindViews({R.id.account_info, R.id.password_info})
    public List<TextView> infoTexts;
    @BindViews({R.id.view, R.id.view2, R.id.view3, R.id.view4})
    public List<View> lines;

    @BindView(R.id.edit_date) TextView editDate;
    @BindView(R.id.edit_gender) TextView editGender;
    @BindView(R.id.personal_info)
    TextView infoPersonal;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.male_button)
    Button maleButton;
    @BindView(R.id.female_button)
    Button femaleButton;
    @BindView(R.id.date_picker)
    ImageButton datePicker;
    @BindView(R.id.date_text)
    TextView dateText;
    @Password(min = 8, scheme = Password.Scheme.ANY)
    @BindView(R.id.edit_pw)
    EditText password;
    @ConfirmPassword
    @BindView(R.id.edit_confirm)
    EditText confirmPassword;
    @NotEmpty
    @Email
    @BindView(R.id.edit_email)
    EditText email;
    @NotEmpty
    @Length(min = 4, max = 50)
    @BindView(R.id.edit_first)
    EditText name;
    @NotEmpty
    @Length(min = 4, max = 50)
    @BindView(R.id.edit_last)
    EditText lastName;
    @BindView(R.id.register_button)
    Button register;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.back_button)
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        password.setTransformationMethod(new PasswordTransformationMethod());
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        datePicker.setOnClickListener(this::onClickDate);
        register.setOnClickListener(this::onClickRegister);
        roomieService = new RoomieService(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if(month <= 9){
                    month++;
                }

                dateText.setText(dayOfMonth + "/" + month  + "/" + year);
                String monthS, dayS;
                monthS = Integer.toString(month);
                dayS = Integer.toString(dayOfMonth);
                if (month <= 9) {
                    monthS = "0" + month;
                }

                if (dayOfMonth <= 9) {
                    dayS = "0" + dayOfMonth;
                }
                date = year + "-" + monthS + "-" + dayS;
            }
        };
        Intent intent = getIntent();
        if (intent.getStringExtra(MainActivity.JHIUSER_EMAIL) != null) {
            password.setText("placeholder");
            confirmPassword.setText("placeholder");
            email.setText("placeholder@gmail.com");
            lines.forEach(view -> view.setVisibility(View.GONE));
            infoTexts.forEach(text -> text.setVisibility(View.GONE));
            email.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            register.setText(R.string.complete_user_btn);
            title.setText(R.string.complete_title);
            infoPersonal.setText(R.string.complete_title);
            infoPersonal.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setText(intent.getStringExtra(MainActivity.JHIUSER_NAME));
            name.setEnabled(false);
            lastName.setEnabled(false);
            lastName.setText(intent.getStringExtra(MainActivity.JHIUSER_LAST));
        }

        JodaTimeAndroid.init(this);
    }

    public void onClickRegister(View view) {
        showProgress(true);

        if(dateText.getText().toString().equals("")){
            editDate.setError("Please choose a date");
            isValid = false;
        }else{
            isValid = true;
        }

        if(gender == null){
            editGender.setError("Please choose a gender");
            isValid = false;
        }else{
            isValid = true;
        }

        validator.validate();
    }

    public void onClickDate(View view) {
        editDate.setError(null);
        DateTime max = new DateTime().minusYears(18);
        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, max.getYear(), max.getMonthOfYear(), max.getDayOfMonth());
        dialog.getDatePicker().setMaxDate(max.getMillis());
        dialog.show();
    }

    public void onClickMale(View view) {
        editGender.setError(null);
        activeGender(maleButton, femaleButton);
        gender = Gender.MALE;
    }

    public void onClickFemale(View view) {
        editGender.setError(null);
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
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
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
        Roomie roomie = new Roomie(date, "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png", gender, null, null, "", user.getId(), null, null, null, null);
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
        Intent intent;
        if (getIntent().getStringExtra(MainActivity.JHIUSER_EMAIL) != null) {
            Toast.makeText(this, getString(R.string.success_info), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));

        } else {
            intent = new Intent(this, RegisterSuccess.class);
            String name = user.getFirstName();
            intent.putExtra(USER_NAME, name);
            startActivity(intent);
        }
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

    @Override
    public void onValidationSucceeded() {
        if(isValid){
            if (getIntent().getStringExtra(MainActivity.JHIUSER_EMAIL) != null) {
                Roomie roomie = new Roomie(date, "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png", gender, null, null, "", Long.parseLong(getIntent().getStringExtra(MainActivity.JHIUSER_ID)), null, null, null, null);
                roomieService.createRoomie(roomie, this);
            } else {
                getJhiUsers().register(email.getText().toString(), name.getText().toString(), lastName.getText().toString(), password.getText().toString(), this);
            }
        }else{
            showProgress(false);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        showProgress(false);
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
