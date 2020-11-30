package com.hyy.accountassis.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hyy.accountassis.fragment.BaseFragment;

import java.util.List;


public class BaseFragmentStatePagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;
    private List<String> mTitles;

    public BaseFragmentStatePagerAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mFragments = fragments;
        this.mTitles = titles;
    }

    @NonNull
    @Override
    public BaseFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


    /**
     * ViewPager with TabLayout after binding, is here for the PageTitle Tab of the Text
     *
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles == null || mTitles.size() == 0) {
            return "";
        }
        return mTitles.get(position);
    }
}
