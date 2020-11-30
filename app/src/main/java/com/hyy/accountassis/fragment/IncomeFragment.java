package com.hyy.accountassis.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyy.accountassis.R;
import com.hyy.accountassis.activity.AddDetailActivity;
import com.hyy.accountassis.adapter.DetailListAdapter;
import com.hyy.accountassis.bean.Detail;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.util.CommonUtils;
import com.hyy.accountassis.util.SPTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.hyy.accountassis.constant.Constants.ADD_DETAIL_REQUEST_CODE;
import static com.hyy.accountassis.constant.Constants.DETAIL_TYPE;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_ID;

/**
 *
 */
public class IncomeFragment extends BaseFragment {

    protected RecyclerView recyclerView;
    protected TextView noData;
    protected TextView date;
    protected TextView total;
    private FloatingActionButton addDetail;
    private DetailListAdapter detailListAdapter;
    private int type;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        noData = (TextView) rootView.findViewById(R.id.noData);
        date = (TextView) rootView.findViewById(R.id.date);
        total = (TextView) rootView.findViewById(R.id.total);
        addDetail = (FloatingActionButton) rootView.findViewById(R.id.addDetail);
        detailListAdapter = new DetailListAdapter(new ArrayList<Detail>(), IncomeFragment.this);
        recyclerView.setAdapter(detailListAdapter);
        recyclerView.setVisibility(View.INVISIBLE);
        noData.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DETAIL_TYPE)) {
            type = bundle.getInt(DETAIL_TYPE, INCOME_TYPE);
        }
        date.setText(CommonUtils.getCurrentDateTime("yyyy/MM"));
        addDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddDetailActivity.class);
                intent.putExtra(DETAIL_TYPE, type);
                IncomeFragment.this.startActivityForResult(intent, ADD_DETAIL_REQUEST_CODE);
            }
        });
        refreshData(type);
        return rootView;
    }

    @Override
    public void refreshData(final int type) {
        super.refreshData(type);
        refreshDataByDate(type, CommonUtils.getCurrentDateTime("yyyy/MM"));
    }

    @Override
    public void refreshDataByDate(int typeArgs, final String createDate) {
        super.refreshDataByDate(type, createDate);
        if (e1 == null) {
            e1 = Executors.newSingleThreadExecutor();
        }
        e1.execute(new Runnable() {
            @Override
            public void run() {
                final int accountId = (int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0);
                final Pair<Double, List<Detail>> result = DbHelper.getInstance().queryDetailListByTypeDate(accountId, type, createDate);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        date.setText(CommonUtils.getDateStr(CommonUtils.strToDate(createDate, "yyyy/MM"), "yyyy/MM"));
                        total.setText((type == INCOME_TYPE ? "Total revenue: " : "Total spending : -") + result.first);
                        List<Detail> detailList = result.second;
                        if (detailList != null && detailList.size() > 0) {
                            detailListAdapter.refreshData(detailList);
                            recyclerView.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.INVISIBLE);
                            noData.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_DETAIL_REQUEST_CODE) {
                if (data != null && data.hasExtra(DETAIL_TYPE)) {
                    int typeTmp = data.getIntExtra(DETAIL_TYPE, INCOME_TYPE);
                    if (onFragmentCallback != null) {
                        onFragmentCallback.onCallback(typeTmp);
                    }
                    refreshData(typeTmp);
                }
            }
        }
    }
}