package com.cosmicode.roomie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.domain.SearchFilter;
import com.cosmicode.roomie.domain.UserReport;
import com.cosmicode.roomie.domain.enumeration.CurrencyType;
import com.cosmicode.roomie.domain.enumeration.ReportType;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.service.UserReportService;
import com.cosmicode.roomie.util.RoomieBottomNavigationView;
import com.cosmicode.roomie.util.RoomieTimeUtil;
import com.cosmicode.roomie.util.listeners.OnGetUserEmailListener;
import com.cosmicode.roomie.view.ListingBasicInformation;
import com.cosmicode.roomie.view.ListingChooseLocation;
import com.cosmicode.roomie.view.ListingCost;
import com.cosmicode.roomie.view.ListingStepChooseType;
import com.cosmicode.roomie.view.MainConfigurationFragment;
import com.cosmicode.roomie.view.MainEditProfileFragment;
import com.cosmicode.roomie.view.MainEditRoom;
import com.cosmicode.roomie.view.MainMyRoomsFragment;
import com.cosmicode.roomie.view.MainNotificationFragment;
import com.cosmicode.roomie.view.MainOptionsBottomSheetDialogFragment;
import com.cosmicode.roomie.view.MainPremiumRooms;
import com.cosmicode.roomie.view.MainProfileFragment;
import com.cosmicode.roomie.view.MainRoomFragment;
import com.cosmicode.roomie.view.MainSearchFragment;
import com.cosmicode.roomie.view.NewTaskFragment;
import com.cosmicode.roomie.view.ToDoLIstFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.DateTime;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


public class MainActivity extends BaseActivity implements RoomieService.OnGetCurrentRoomieListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        ToDoLIstFragment.OnFragmentInteractionListener,
        NewTaskFragment.OnFragmentInteractionListener,
        MainOptionsBottomSheetDialogFragment.OnFragmentInteractionListener,
        MainProfileFragment.OnFragmentInteractionListener,
        MainEditProfileFragment.OnFragmentInteractionListener,
        MainNotificationFragment.OnFragmentInteractionListener,
        MainConfigurationFragment.OnFragmentInteractionListener,
        MainSearchFragment.OnFragmentInteractionListener,
        MainRoomFragment.OnFragmentInteractionListener,
        MainMyRoomsFragment.OnFragmentInteractionListener,
        MainPremiumRooms.OnFragmentInteractionListener,
        ListingStepChooseType.OnFragmentInteractionListener,
        OnGetUserEmailListener,
        MainEditRoom.OnFragmentInteractionListener,
        ListingBasicInformation.OnFragmentInteractionListener,
        ListingCost.OnFragmentInteractionListener,
        ListingChooseLocation.OnFragmentInteractionListener{

    public static final String JHIUSER_EMAIL = "jhiEmail";
    public static final String JHIUSER_ID = "jhiID";
    public static final String JHIUSER_NAME = "jhiName";
    public static final String JHIUSER_LAST = "jhiLast";
    private static final String TAG = "MainActivity";
    @BindView(R.id.navigation_view)
    RoomieBottomNavigationView bottomNavigationView;
    private BottomNavigationView navigationView;
    private RoomieService roomieService;
    private Roomie currentRoomie;
    private MenuItem currentMenuItem;
    private SearchFilter searchFilter;

    public static final Intent clearTopIntent(Context from) {
        Intent intent = new Intent(from, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomNavigationView.showBadge(3);
        roomieService = new RoomieService(this, this);
        roomieService.getCurrentRoomie();
        searchFilter = new SearchFilter("", 20, CurrencyType.DOLLAR, 100, 500, new ArrayList<>());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.navigation_view_options) currentMenuItem = menuItem;

        switch (menuItem.getItemId()) {
            case R.id.navigation_view_home:
                MainSearchFragment homeFragment = MainSearchFragment.newInstance(searchFilter);
                openFragment(homeFragment, "right");
                return true;
            case R.id.navigation_view_account:
                MainProfileFragment mainProfileFragment = MainProfileFragment.newInstance(currentRoomie);
                openFragment(mainProfileFragment, "right");
                return true;
            case R.id.navigation_view_notifications:
                MainNotificationFragment notificationFragment = MainNotificationFragment.newInstance();
                openFragment(notificationFragment, "left");
                return true;
            case R.id.navigation_view_options:
                MainOptionsBottomSheetDialogFragment mainOptionsBottomSheetDialogFragment = new MainOptionsBottomSheetDialogFragment();
                mainOptionsBottomSheetDialogFragment.show(getSupportFragmentManager(), mainOptionsBottomSheetDialogFragment.getTag());
                return false;
            default:
                MainSearchFragment defaultFragment = MainSearchFragment.newInstance(searchFilter);
                openFragment(defaultFragment, "right");
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void openFragment(Fragment fragment, String start) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (start) {
            case "left":
                transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, 0, 0);
                break;
            case "right":
                transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, 0, 0);
                break;
            case "up":
        }
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void changePercentage(int progress) {

    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public final void performLogout() {
        try {
            GoogleSignInOptions gso = (new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)).requestServerAuthCode(getString(R.string.default_web_client_id2)).requestEmail().build();
            GoogleSignIn.getClient(this, gso).signOut();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        getJhiUsers().logout();
        startActivity(LoginActivity.clearTopIntent(this));
    }

    @Override
    public void reportProblemApp() {
        UserReportService userReportService = new UserReportService(this, new UserReportService.UserReportListener() {
            @Override
            public void onGetUserReportSuccess(UserReport userReport) {

            }

            @Override
            public void onCreateUserReportSuccess(UserReport userReport) {
                showUserMessage(getString(R.string.report_success), SnackMessageType.SUCCESS);
            }

            @Override
            public void onUpdateUserReportSuccess(UserReport userReport) {

            }

            @Override
            public void onUserReportError(String error) {
                showUserMessage(getString(R.string.report_error_message), BaseActivity.SnackMessageType.ERROR);
            }
        });

        AlertDialog.Builder newaReportDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View newReportLayout = inflater.inflate(R.layout.report_problem_app_dialog, null);

        EditText reportDescriptionET = newReportLayout.findViewById(R.id.report_description);


        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(reportDescriptionET, "^.{4,}", getString(R.string.not_empty));

        newaReportDialogBuilder.setTitle(R.string.report_a_problem)
                .setIcon(R.drawable.icon_report_brand)
                .setView(newReportLayout)
                .setPositiveButton(R.string.send, (dialog, which) -> {
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog newReportDialog = newaReportDialogBuilder.create();
        newReportDialog.show();

        newReportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (mAwesomeValidation.validate()) {

                UserReport userReport = new UserReport();
                userReport.setDate(RoomieTimeUtil.dateTimeToInstantUTCString(DateTime.now()));
                userReport.setType(ReportType.APP);
                userReport.setDesciption(reportDescriptionET.getText().toString());
                userReportService.createUserReport(userReport);
                newReportDialog.dismiss();
            }
        });
    }

    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void sendReport(Long roomieId, String type) {
        UserReportService userReportService = new UserReportService(this, new UserReportService.UserReportListener() {
            @Override
            public void onGetUserReportSuccess(UserReport userReport) {

            }

            @Override
            public void onCreateUserReportSuccess(UserReport userReport) {
                showUserMessage(getString(R.string.report_success), SnackMessageType.SUCCESS);
            }

            @Override
            public void onUpdateUserReportSuccess(UserReport userReport) {

            }

            @Override
            public void onUserReportError(String error) {
                showUserMessage(getString(R.string.report_error_message), BaseActivity.SnackMessageType.ERROR);
            }
        });

        AlertDialog.Builder newaReportDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View newReportLayout = inflater.inflate(R.layout.report_room_app_dialog, null);

        Spinner reportUser = newReportLayout.findViewById(R.id.report_spinner);
        EditText reportDescription = newReportLayout.findViewById(R.id.report_description);


        AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(reportDescription, "^.{4,}", getString(R.string.not_empty));

        newaReportDialogBuilder.setTitle(R.string.report_a_problem)
                .setIcon(R.drawable.icon_report_brand)
                .setView(newReportLayout)
                .setPositiveButton(R.string.send, (dialog, which) -> {
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog newReportDialog = newaReportDialogBuilder.create();
        newReportDialog.show();

        newReportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (mAwesomeValidation.validate()) {

                UserReport userReport = new UserReport();
                userReport.setDate(RoomieTimeUtil.dateTimeToInstantUTCString(DateTime.now()));
                if(type.equals("user")){
                    userReport.setType(ReportType.USER);
                    userReport.setRoomieId(roomieId);
                }else{
                    userReport.setType(ReportType.ROOM);
                    userReport.setRoomId(roomieId);
                }

                userReport.setDesciption(reportUser.getSelectedItem().toString()+" "+reportDescription.getText().toString());
                userReportService.createUserReport(userReport);
                newReportDialog.dismiss();
            }
        });
    }

    @Override
    public void onSearchFragmentInteraction(Room item) {

    }

    @Override
    public void onSearchFilterUpdated(SearchFilter searchFilter) {
        this.searchFilter = searchFilter;
    }

    @Override
    public void returnToHomeFragment() {
        MainSearchFragment mainSearchFragment = MainSearchFragment.newInstance(searchFilter);
        openFragment(mainSearchFragment, "up");
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        this.currentRoomie = roomie;
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setOnNavigationItemSelectedListener(this);
        if (getJhiUsers().getMobileDeviceID().equals("") || currentRoomie.getMobileDeviceID().equals("") || !currentRoomie.getMobileDeviceID().equals(getJhiUsers().getMobileDeviceID()))
            registerDeviceFirebaseCloudMessaging();
        openFragment(MainSearchFragment.newInstance(searchFilter), "up");
    }

    @Override
    public void onGetCurrentRoomieError(String error) {
        getJhiUsers().getLogedUser(user -> getJhiUsers().findByEmail(user.getEmail(), this));
    }

    @Override
    public void OnUpdateSuccess(Roomie roomie) {

    }

    @Override
    public void onGetUserSuccess(JhiAccount user) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(JHIUSER_EMAIL, user.getEmail());
        intent.putExtra(JHIUSER_ID, Long.toString(user.getId()));
        intent.putExtra(JHIUSER_NAME, user.getFirstName());
        intent.putExtra(JHIUSER_LAST, user.getLastName());
        startActivity(intent);
    }

    @Override
    public void onGetUserError(String error) {
        showUserMessage(error, SnackMessageType.ERROR);
    }

    private void registerDeviceFirebaseCloudMessaging() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String mobileDeviceID = task.getResult().getToken();
                    Log.d(TAG, String.format("MobileDeviceID: %s", mobileDeviceID));
                    getJhiUsers().setMobileDeviceID(mobileDeviceID);
                    currentRoomie.setMobileDeviceID(mobileDeviceID);
                    roomieService.updateRoomie(currentRoomie);
                });
    }

    public Roomie getCurrentRoomie() {
        return this.currentRoomie;
    }


    @OnClick(R.id.navigation_view_add_fab)
    public void newListing() {
        ListingStepChooseType typeFragment = ListingStepChooseType.newInstance();
        openFragment(typeFragment, "up");
    }
}
