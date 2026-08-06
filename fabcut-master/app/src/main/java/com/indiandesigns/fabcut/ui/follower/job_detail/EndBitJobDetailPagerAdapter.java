package com.indiandesigns.fabcut.ui.follower.job_detail;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class EndBitJobDetailPagerAdapter extends FragmentStatePagerAdapter {

    private final List<EndBitJobDetailFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public EndBitJobDetailPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(EndBitJobDetailFragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public EndBitJobDetailFragment getFragment(int key) {
        return mFragmentList.get(key);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void clear(){
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }
}
