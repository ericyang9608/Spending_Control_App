package com.hyy.accountassis.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyy.accountassis.R;
import com.hyy.accountassis.bean.Bill;

import java.util.List;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.BillListViewHolder> {
    private List<Bill> billList;

    public BillListAdapter(List<Bill> bills) {
        this.billList = bills;
    }

    /**
     * refresh data
     *
     * @param bills
     */
    public void refreshData(List<Bill> bills) {
        if (billList.size() > 0) billList.clear();
        billList.addAll(bills);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BillListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_bill_list, parent, false));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull BillListViewHolder holder, int position) {
        Bill bill = billList.get(position);
        String createDate = bill.getCreateDate();
        String month = createDate.split("/")[1] + " M";
        holder.month.setText(month);
        holder.income.setText(String.format("%.2f", bill.getIncomeAmount()));
        holder.expense.setText(String.format("%.2f", bill.getExpenseAmount()));
        holder.balance.setText(String.format("%.2f", bill.getBalance()));
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }


    static class BillListViewHolder extends RecyclerView.ViewHolder {

        TextView month;
        TextView income;
        TextView expense;
        TextView balance;

        public BillListViewHolder(View rootView) {
            super(rootView);
            month = (TextView) rootView.findViewById(R.id.month);
            income = (TextView) rootView.findViewById(R.id.income);
            expense = (TextView) rootView.findViewById(R.id.expense);
            balance = (TextView) rootView.findViewById(R.id.balance);
        }
    }
}
