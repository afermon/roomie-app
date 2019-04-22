package com.cosmicode.roomie;

import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.view.ExpenseList;
import com.cosmicode.roomie.view.NewTaskFragment;
import com.cosmicode.roomie.view.RoomCalendarFragment;
import com.cosmicode.roomie.view.ToDoLIstFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class PremiumToolsAcitivity extends BaseActivity implements ToDoLIstFragment.OnFragmentInteractionListener, NewTaskFragment.OnFragmentInteractionListener, ExpenseList.OnFragmentInteractionListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        room = getIntent().getParcelableExtra("room");

        setContentView(R.layout.activity_premium_tools_acitivity);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment = ToDoLIstFragment.newInstance(room.getId());
            switch (position){
                case 0:
                    fragment = ToDoLIstFragment.newInstance(room.getId());
                    break;
                case 1:
                    fragment = ExpenseList.newInstance(room);
                    break;
                case 2:
                    fragment = RoomCalendarFragment.newInstance(room.getId());
                    break;
                case 3:
                    finish();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
