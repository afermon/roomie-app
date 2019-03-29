package com.cosmicode.roomie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.service.RoomieService;
import com.cosmicode.roomie.util.RoomieBottomNavigationView;
import com.cosmicode.roomie.util.listeners.OnGetUserEmailListener;
import com.cosmicode.roomie.view.MainConfigurationFragment;
import com.cosmicode.roomie.view.MainEditProfileFragment;
import com.cosmicode.roomie.view.MainNotificationFragment;
import com.cosmicode.roomie.view.MainOptionsFragment;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements RoomieService.OnGetCurrentRoomieListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        ToDoLIstFragment.OnFragmentInteractionListener,
        NewTaskFragment.OnFragmentInteractionListener,
        MainOptionsFragment.OnFragmentInteractionListener,
        MainProfileFragment.OnFragmentInteractionListener,
        MainEditProfileFragment.OnFragmentInteractionListener,
        MainNotificationFragment.OnFragmentInteractionListener,
        MainConfigurationFragment.OnFragmentInteractionListener,
        MainSearchFragment.OnFragmentInteractionListener,
        MainRoomFragment.OnFragmentInteractionListener,
        OnGetUserEmailListener {

    private BottomNavigationView navigationView;
    private RoomieService roomieService;
    private Roomie currentRoomie;
    private static final String TAG = "MainActivity";
    public static final String JHIUSER_EMAIL = "jhiEmail";
    public static final String JHIUSER_ID = "jhiID";
    public static final String JHIUSER_NAME = "jhiName";
    public static final String JHIUSER_LAST = "jhiLast";
    private MenuItem currentMenuItem;

    @BindView(R.id.navigation_view) RoomieBottomNavigationView bottomNavigationView;

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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if(currentMenuItem != null && menuItem.getItemId() == currentMenuItem.getItemId()) return true;
        
        currentMenuItem = menuItem;

        switch (menuItem.getItemId()) {
            case R.id.navigation_view_home:
                MainSearchFragment homeFragment = MainSearchFragment.newInstance("");
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
                MainOptionsFragment optionsFragment = MainOptionsFragment.newInstance("", "");
                openFragment(optionsFragment, "left");
                return true;
            default:
                MainSearchFragment defaultFragment = MainSearchFragment.newInstance("");
                openFragment(defaultFragment, "right");
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void openFragment(Fragment fragment, String start) {
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

    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void onSearchFragmentInteraction(Room item) {

    }

    @Override
    public void returnToHomeFragment() {
        MainSearchFragment mainSearchFragment = MainSearchFragment.newInstance("");
        openFragment(mainSearchFragment, "up");
    }

    @Override
    public void onGetCurrentRoomieSuccess(Roomie roomie) {
        this.currentRoomie = roomie;
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setOnNavigationItemSelectedListener(this);
        if(getJhiUsers().getMobileDeviceID().equals("") || currentRoomie.getMobileDeviceID().equals("") || !currentRoomie.getMobileDeviceID().equals(getJhiUsers().getMobileDeviceID())) registerDeviceFirebaseCloudMessaging();
        openFragment(MainSearchFragment.newInstance(""), "up");
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

    private void registerDeviceFirebaseCloudMessaging(){
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

    @OnClick(R.id.navigation_view_add_fab)
    public void newListing(){
        startActivity(new Intent(this, CreateListingActivity.class));
    }
}
