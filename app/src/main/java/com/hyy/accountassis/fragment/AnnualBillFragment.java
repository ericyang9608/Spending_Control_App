package com.hyy.accountassis.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hyy.accountassis.R;
import com.hyy.accountassis.adapter.BillListAdapter;
import com.hyy.accountassis.bean.Bill;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.util.CommonUtils;
import com.hyy.accountassis.util.SPTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_ID;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AnnualBillFragment extends BaseFragment {


    protected TextView amount;
    protected TextView incomeAmount;
    protected TextView expenseAmount;
    protected TextView noData;
    protected RecyclerView annualBill;

    private BillListAdapter billListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_annual_bill, container, false);
        amount = (TextView) rootView.findViewById(R.id.amount);
        incomeAmount = (TextView) rootView.findViewById(R.id.incomeAmount);
        expenseAmount = (TextView) rootView.findViewById(R.id.expenseAmount);
        noData = (TextView) rootView.findViewById(R.id.noData);
        annualBill = (RecyclerView) rootView.findViewById(R.id.annualBill);
        billListAdapter = new BillListAdapter(new ArrayList<Bill>());
        annualBill.setAdapter(billListAdapter);
        refreshData(INCOME_TYPE);
        return rootView;
    }

    @Override
    public void refreshData(final int type) {
        super.refreshData(type);
        refreshDataByDate(type, CommonUtils.getCurrentDateTime("yyyy"));
    }

    @Override
    public void refreshDataByDate(int typeArgs, final String createDate) {
        super.refreshDataByDate(typeArgs, createDate);
        if (e1 == null) {
            e1 = Executors.newSingleThreadExecutor();
        }
        e1.execute(new Runnable() {
            @Override
            public void run() {
                final int accountId = (int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0);
                final Pair<Pair<Float, Float>, List<Bill>> result = DbHelper.getInstance().queryBillsByTypeYear(accountId, createDate);
                Log.i(TAG, "run: result==" + result.toString());
                activity.runOnUiThread(new Runnable() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void run() {
                        float incomeTotal = Objects.requireNonNull(result.first).first;
                        float expenseTotal = result.first.second;
                        incomeAmount.setText(String.format("%.2f", incomeTotal));
                        expenseAmount.setText(String.format("-%.2f", expenseTotal));
                        amount.setText(String.format("%.2f", (incomeTotal - expenseTotal)));
                        List<Bill> billList = result.second;
                        if (billList != null && billList.size() > 0) {
                            billListAdapter.refreshData(billList);
                            annualBill.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                        } else {
                            annualBill.setVisibility(View.INVISIBLE);
                            noData.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

}