package com.education.edushare.edushare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 20-11-2017.
 */

public class HomeActivity extends AppCompatActivity {
    public static ViewPager viewPager;
    public static MyPagerAdapter mAdapter;
    private SharedPreferences Details;
    private SharedPreferences.Editor prefEditor;
    BottomBar bottomBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager= (ViewPager) findViewById(R.id.viewPager);
        ButterKnife.bind(this);
        stopService(new Intent(HomeActivity.this, Notifications.class));
        Log.d("NOTIFICATION SERVICE", "Service Stopped first: Homeactivity ");
        startService(new Intent(HomeActivity.this, Notifications.class));
        Log.d("NOTIFICATION SERVICE", "Service Started first: Homeactivity ");
        init();
        generatekeys();
        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new DashBoardFragment());
        transaction.commit();*/
    }

    public static void ChangeFragment(){
        mAdapter.changeFragment();
    }

    private void generatekeys() {
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);  //stores user name and path to profilepic
        prefEditor = Details.edit();

    }

    private void init() {
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(5);
        bottomBar= (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Fragment selectedFragment = null;
                if (tabId == R.id.tab_dashboard) {
                    viewPager.setCurrentItem(0);
                }
                if (tabId == R.id.tab_chatroom) {
                    viewPager.setCurrentItem(1);
                }
                if (tabId == R.id.tab_star) {
                    viewPager.setCurrentItem(2);
                }
                if (tabId == R.id.tab_profile) {
                    viewPager.setCurrentItem(4);
                }
                if (tabId == R.id.tab_community) {
                    viewPager.setCurrentItem(3);
                }

              /*  FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, selectedFragment);
                transaction.commit();*/
            }
        });




        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0: {bottomBar.selectTabAtPosition(0,true);
                                break;}
                    case 1: {bottomBar.selectTabAtPosition(1,true);
                                break;}
                    case 2:  {bottomBar.selectTabAtPosition(2,true);
                                 break;}
                    case 3:  {bottomBar.selectTabAtPosition(3,true);
                                 break;}
                    case 4:  {bottomBar.selectTabAtPosition(4,true);
                                break;}
                    default:  {bottomBar.selectTabAtPosition(0,true);
                                 break;}
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        FragmentManager fmanager;
        GroupMessageFragment groupMessageFragment;
        ContactsFragment contactsFragment;
        public Fragment mFragmentAtPos1;

        @Override
        public int getItemPosition(Object object)
        {
            if (object instanceof ContactsFragment &&
                    mFragmentAtPos1 instanceof GroupMessageFragment) {
                return POSITION_NONE;
            }
            if (object instanceof GroupMessageFragment &&
                    mFragmentAtPos1 instanceof ContactsFragment) {
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }

        public void changeFragment(){
            fmanager.beginTransaction().remove(mFragmentAtPos1).commit();
            if (mFragmentAtPos1 instanceof ContactsFragment){
                mFragmentAtPos1 = groupMessageFragment;
            }else{ // Instance of NextFragment
                mFragmentAtPos1 = contactsFragment;
            }
            notifyDataSetChanged();
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fmanager=fm;
            this.groupMessageFragment=new GroupMessageFragment();
            this.contactsFragment=new ContactsFragment();
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0:return new DashBoardFragment();
                case 1: if (mFragmentAtPos1 == null)
                            mFragmentAtPos1 = contactsFragment;
                        return mFragmentAtPos1;
                case 2: return new FavouritesFragment();
                case 3: return new RequestFragment();
                case 4: return new ProfileFragment();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
