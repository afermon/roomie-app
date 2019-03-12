package com.cosmicode.roomie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cosmicode.roomie.domain.Room;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;

import com.cosmicode.roomie.view.MainEditProfileFragment;
import com.cosmicode.roomie.view.MainHomeFragment;
import com.cosmicode.roomie.view.MainNotificationFragment;
import com.cosmicode.roomie.view.MainOptionsFragment;
import com.cosmicode.roomie.view.MainProfileFragment;
import com.cosmicode.roomie.view.NewTaskFragment;
import com.cosmicode.roomie.view.ToDoLIstFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ToDoLIstFragment.OnFragmentInteractionListener, NewTaskFragment.OnFragmentInteractionListener, MainHomeFragment.OnFragmentInteractionListener, MainOptionsFragment.OnFragmentInteractionListener, MainProfileFragment.OnFragmentInteractionListener, MainEditProfileFragment.OnFragmentInteractionListener, MainNotificationFragment.OnFragmentInteractionListener {

    private BottomNavigationView navigationView;

    public static final Intent clearTopIntent(Context from) {
        Intent intent = new Intent(from, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setOnNavigationItemSelectedListener(this);
        openFragment(MainOptionsFragment.newInstance("", ""), "up");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_view_home:
                MainHomeFragment homeFragment = MainHomeFragment.newInstance("");
                openFragment(homeFragment, "right");
                return true;
            case R.id.navigation_view_account:
                MainProfileFragment mainProfileFragment = MainProfileFragment.newInstance();
                openFragment(mainProfileFragment, "right");
                return true;
            case R.id.navigation_view_notifications:
                MainNotificationFragment notificationFragment = MainNotificationFragment.newInstance();
                openFragment(notificationFragment, "left");
                return true;
            case R.id.navigation_view_options:
                MainOptionsFragment optionsFragment = MainOptionsFragment.newInstance("", "");
                openFragment(optionsFragment,"left");
                return true;
            default:
                MainHomeFragment defaultFragment = MainHomeFragment.newInstance("");
                openFragment(defaultFragment, "right");
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void openFragment(Fragment fragment, String start) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (start){
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
            //Ignore TODO: LOG
        }

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            //Ignore TODO: LOG
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
        MainHomeFragment mainHomeFragment = MainHomeFragment.newInstance("");
        openFragment(mainHomeFragment, "up");
    }
}
