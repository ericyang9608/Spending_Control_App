package com.hyy.accountassis.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.hyy.accountassis.R;
import com.hyy.accountassis.bean.Budget;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.util.CommonUtils;
import com.hyy.accountassis.util.SPTool;
import com.hyy.accountassis.widget.ArcView;

import java.util.concurrent.Executors;

import static com.hyy.accountassis.constant.Constants.EXPENSE_TYPE;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_ID;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BudgetDetailFragment extends BaseFragment {


    protected TextView date;
    protected TextView addOrEdit;
    protected TextView remainBudget;
    protected TextView monthBudget;
    protected TextView monthExpense;
    protected ArcView remainBudgetValue;

    private Budget budget;
    private String month;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget_detail, container, false);
        date = (TextView) rootView.findViewById(R.id.date);
        addOrEdit = (TextView) rootView.findViewById(R.id.addOrEdit);
        remainBudgetValue = (ArcView) rootView.findViewById(R.id.remainBudgetValue);
        remainBudget = (TextView) rootView.findViewById(R.id.remainBudget);
        monthBudget = (TextView) rootView.findViewById(R.id.monthBudget);
        monthExpense = (TextView) rootView.findViewById(R.id.monthExpense);
        addOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetupBudgetDialog();
            }
        });
        refreshData(EXPENSE_TYPE);
        return rootView;
    }

    @Override
    public void refreshData(final int type) {
        super.refreshData(type);
        refreshDataByDate(type, CommonUtils.getCurrentDateTime("yyyy/MM"));
    }

    @Override
    public void refreshDataByDate(int typeArgs, final String createDate) {
        super.refreshDataByDate(typeArgs, createDate);
        if (e1 == null) {
            e1 = Executors.newSingleThreadExecutor();
        }
        month = createDate;
        e1.execute(new Runnable() {
            @Override
            public void run() {
                final int accountId = (int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0);
                budget = DbHelper.getInstance().queryBudgetByMonth(accountId, createDate);
                final float expenseTotal = DbHelper.getInstance().queryAmountTotalByTypeDate(accountId, EXPENSE_TYPE, createDate);
                activity.runOnUiThread(new Runnable() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void run() {
                        date.setText(createDate + " Budget:");
                        addOrEdit.setText("Setup Budget");
                        float monthAmount = 0f;
                        int remain = 0;
                        if (budget != null) {
                            monthAmount = budget.getAmount();
                            if (monthAmount > 0) {
                                addOrEdit.setText("Edit Budget");
                                remain = (int) ((monthAmount - expenseTotal) / monthAmount * 100);
                            }
                        }
                        remainBudget.setText(String.format("%.2f", monthAmount - expenseTotal));
                        monthBudget.setText(String.format("%.2f", monthAmount));
                        monthExpense.setText(String.format("%.2f", expenseTotal));
                        remain = Math.max(remain, 0);
                        remainBudgetValue.setValues(0, (int) monthAmount, (int) expenseTotal, "The remaining " + remain + "%");
                    }
                });
            }
        });
    }

    private void showSetupBudgetDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_setup_budget_account, null);
        final Dialog setupBudgetDialog = new AlertDialog.Builder(context)
                .setTitle("Budget")
                .setView(dialogView)
                .create();
        final EditText amount = (EditText) dialogView.findViewById(R.id.amount);
        if (budget != null && budget.getAmount() > 0f) {
            amount.setText(String.format("%.2f", budget.getAmount()));
        }
        AppCompatButton ok = (AppCompatButton) dialogView.findViewById(R.id.ok);
        AppCompatButton cancel = (AppCompatButton) dialogView.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String amountStr = amount.getText().toString().trim();
                if (TextUtils.isEmpty(amountStr)) {
                    Toast.makeText(context, "The budget can't be empty！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final float amountF = Float.parseFloat(amountStr);
                if (amountF == 0f) {
                    Toast.makeText(context, "Budgets are not zero！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (e1 == null) {
                    e1 = Executors.newSingleThreadExecutor();
                }
                e1.execute(new Runnable() {
                    @Override
                    public void run() {
                        long isSuccess = -1;
                        if (budget != null && budget.getAmount() > 0f) {
                            budget.setAmount(amountF);
                            isSuccess = DbHelper.getInstance().updateBudgetById(budget);
                        } else {
                            if (budget == null) {
                                budget = new Budget();
                            }
                            budget.setMonth(month);
                            budget.setAmount(amountF);
                            final int accountId = (int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0);
                            budget.setAccount_id(accountId);
                            isSuccess = DbHelper.getInstance().insertBudget(budget);
                        }
                        if (isSuccess > 0) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setupBudgetDialog.dismiss();
                                    refreshDataByDate(EXPENSE_TYPE, budget.getMonth());
                                }
                            });
                        }
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupBudgetDialog.dismiss();
            }
        });
        setupBudgetDialog.setCanceledOnTouchOutside(false);
        if (!setupBudgetDialog.isShowing()) {
            setupBudgetDialog.show();
        }
    }

}