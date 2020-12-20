package com.example.shanpin.ui;

import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.shanpin.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class EnterActivity extends AppCompatActivity {


    private ViewPager myViewPager;//切换区
    private List<Fragment> fragmentList;
    private MyfragmentPagerAdapter fragmentPagerAdapter;
    private BottomNavigationView navView;
    private Toolbar toolbar;

//private myFragmentStatePagerAdapter FragmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        toolbar = findViewById(R.id.activity_enter_toolbar);
        setSupportActionBar(toolbar);


        myViewPager=(ViewPager)findViewById(R.id.activity_enter_myViewPager);
        navView = findViewById(R.id.activity_enter_nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initTab();
    }

    //初始化Fragmet及第一个标签
    private void initTab(){
        Fragment_home fragment1=new Fragment_home();
        Fragment_message fragment2=new Fragment_message();
        Fragment_me fragment3=new Fragment_me();

        fragmentList=new ArrayList<Fragment>();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);

        //新建适配器
        fragmentPagerAdapter=new MyfragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
//        FragmentAdapter= new myFragmentStatePagerAdapter(getSupportFragmentManager(),fragmentList);
        //设置适配器
        myViewPager.setAdapter(fragmentPagerAdapter);
        //滑动监听
        myViewPager.addOnPageChangeListener(MyPageChangeListenner);
        //显示第一个页面

    }

    //下导航栏点击事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    myViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    myViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    myViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    private ViewPager.OnPageChangeListener MyPageChangeListenner
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            navView.setSelectedItemId(navView.getMenu().getItem(position).getItemId());
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public class MyfragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public MyfragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyfragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }
        //显示页面
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}



