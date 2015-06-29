package com.lr.ghp.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends FragmentActivity {
    private LoanFlowVPIndicator information_indicator;
    private ViewPager information_vp;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> mDatas = Arrays.asList("选择额度", "基本消息", "征信核实", "审批结果");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
    }

    private void initView(){
        information_indicator= (LoanFlowVPIndicator) findViewById(R.id.information_indicator);
        information_vp= (ViewPager) findViewById(R.id.information_vp);
    }
    private void initFragment(){
        fragmentList.add(new FirstFragment());
        fragmentList.add(new FirstFragment());
        fragmentList.add(new FirstFragment());
        fragmentList.add(new FirstFragment());
        FragmentPagerAdapter fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        information_vp.setAdapter(fragmentPagerAdapter);
        information_indicator.setTabNum(4);//省略默认为4
        information_indicator.initView();

        //设置关联的ViewPager
        information_indicator.setViewPager(information_vp, 1);
        information_indicator.setTabItemTitles(mDatas);
        information_indicator.highLightTextView(1);
    }
}
