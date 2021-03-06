package com.cosmicode.roomie;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.enumeration.RoomState;
import com.cosmicode.roomie.service.RoomService;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.listeners.OnPayPremiumListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.List;


public class PaymentActivity extends BaseActivity implements Validator.ValidationListener, RoomService.RoomServiceListener {

    @NotEmpty
    @Length(min = 16, max = 22)
    @BindView(R.id.card_number)
    EditText cardNumber;
    @NotEmpty
    @Length(min = 5, max = 5)
    @BindView(R.id.card_exp)
    EditText expire;
    @NotEmpty
    @Length(min = 3, max = 3)
    @BindView(R.id.card_cvc)
    EditText cvc;
    @NotEmpty
    @BindView(R.id.card_name)
    EditText name;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.payment_container)
    ConstraintLayout container;


    private Validator validator;
    private RoomService roomService;
    private Room premiumRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        roomService = new RoomService(this, this);

        premiumRoom = getIntent().getParcelableExtra("premium");
        premiumRoom.getRoomies().add(getIntent().getParcelableExtra("owner"));
        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);

        expire.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 3) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if ('/' == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                if (s.length() > 0 && (s.length() % 3) == 0) {
                    char c = s.charAt(s.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf("/")).length <= 2) {
                        s.insert(s.length() - 1, String.valueOf("/"));
                    }
                }
            }
        });
        cardNumber.addTextChangedListener(new TextWatcher() {
            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }


            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });
    }

    @OnClick(R.id.btn_complete)
    public void completePayment(View view) {
        validator.validate();
    }

    @OnClick(R.id.cancel_payment)
    public void cancelPayment(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.bk2)
    public void back(View view) {
        finish();
    }


    @Override
    public void onValidationSucceeded() {
        cardNumber.setError(null);
        expire.setError(null);
        cvc.setError(null);
        boolean isValid = true;

        String[] date = expire.getText().toString().split("/");
        Card card = new Card(
                cardNumber.getText().toString(),
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1]) + 2000,
                cvc.getText().toString()
        );

        if (!card.validateNumber()) {
            cardNumber.setError("Invalid number");
            isValid = false;
        }

        if (!card.validateExpiryDate()) {
            expire.setError("Invalid date");
            isValid = false;
        }

        if (!card.validateCVC()) {
            cvc.setError("Invalid CVC");
            isValid = false;
        }

        if (isValid) {
            showProgress(true);
            Stripe stripe = new Stripe(this, "pk_test_tvOqreoDBMCR33zFGuIpqwHM00njthUCtW");
            stripe.createToken(card, mTokenCallback);
        }
    }

    @NonNull private final TokenCallback mTokenCallback = new TokenCallback() {
        @Override
        public void onSuccess(Token token) {
            Log.d("Stripe Token Success: ", token.toString());
            roomService.payPremium(premiumRoom, token.getId());
        }

        @Override
        public void onError(Exception error) {
            showProgress(false);
            if (error != null && error.getMessage().length() > 0) {
                Log.d("Stripe Token Error: ", error.getLocalizedMessage());
                showUserMessage(error.getMessage(), BaseActivity.SnackMessageType.ERROR);
            }
        }
    };



    @Override
    public void onValidationFailed(List<ValidationError> errors) {
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

    @Override
    public void OnCreateSuccess(Room room) {

    }

    @Override
    public void OnGetRoomsSuccess(List<Room> rooms) {

    }

    @Override
    public void OnGetRoomsError(String error) {

    }

    @Override
    public void OnUpdateSuccess(Room room) {

    }

    @Override
    public void onPaySuccess(Room room) {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("room", room);
        startActivity(intent);
        showProgress(false);
    }

    @Override
    public void onPayError(String error) {
        showProgress(false);
        showUserMessage(error, BaseActivity.SnackMessageType.ERROR);
    }


    private void showProgress(boolean show) {
        Long shortAnimTime = (long) getResources().getInteger(android.R.integer.config_shortAnimTime);

        container.setVisibility(((show) ? View.GONE : View.VISIBLE));

        container.animate()
                .setDuration(shortAnimTime)
                .alpha((float) ((show) ? 0 : 1))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        container.setVisibility(((show) ? View.GONE : View.VISIBLE));
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

