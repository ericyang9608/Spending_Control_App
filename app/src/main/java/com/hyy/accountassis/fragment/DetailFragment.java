package com.hyy.accountassis.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hyy.accountassis.R;
import com.hyy.accountassis.adapter.BaseFragmentStatePagerAdapter;
import com.hyy.accountassis.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hyy.accountassis.constant.Constants.DETAIL_TYPE;
import static com.hyy.accountassis.constant.Constants.EXPENSE_TYPE;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DetailFragment extends BaseFragment {

    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    private List<BaseFragment> fragmentList;

    private int type;
    private int position;
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_detail, container, false);
        tabLayout = (TabLayout) contentView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);
        type = INCOME_TYPE;
        selectedDate = CommonUtils.getCurrentDateTime("yyyy/MM");
        fragmentList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add("Income");
        titleList.add("Expense");
        fragmentList.add(getIncomeFragment(INCOME_TYPE));
        fragmentList.add(getIncomeFragment(EXPENSE_TYPE));
        BaseFragmentStatePagerAdapter detailAdapter = new BaseFragmentStatePagerAdapter(getChildFragmentManager(), fragmentList, titleList);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(detailAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                type = i == 0 ? INCOME_TYPE : EXPENSE_TYPE;
                refreshDataByDate(type, selectedDate);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        return contentView;
    }

    private IncomeFragment getIncomeFragment(int type) {
        IncomeFragment incomeFragment = new IncomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DETAIL_TYPE, type);
        incomeFragment.setArguments(bundle);
        incomeFragment.setOnFragmentCallback(new OnFragmentCallback() {
            @Override
            public void onCallback(int type) {
                viewPager.setCurrentItem(type - 1);
                selectedDate = CommonUtils.getCurrentDateTime("yyyy/MM");
            }
        });
        return incomeFragment;
    }

    @Override
    public void refreshData(int typeArgs) {
        super.refreshData(type);
        fragmentList.get(position).refreshData(type);
    }

    @Override
    public void refreshDataByDate(int typeArgs, String createDate) {
        super.refreshDataByDate(type, createDate);
        selectedDate = createDate;
        fragmentList.get(position).refreshDataByDate(type, createDate);
    }
}