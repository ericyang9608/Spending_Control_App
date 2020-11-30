package com.hyy.accountassis.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hyy.accountassis.R;
import com.hyy.accountassis.activity.AddDetailActivity;
import com.hyy.accountassis.app.MyApp;
import com.hyy.accountassis.bean.Detail;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.fragment.IncomeFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import static com.hyy.accountassis.constant.Constants.ADD_DETAIL_REQUEST_CODE;
import static com.hyy.accountassis.constant.Constants.DETAIL;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.DetailListViewHolder> {
    private static final String TAG = "DetailListAdapter";

    private List<Detail> details;
    private WeakReference<IncomeFragment> incomeFragmentWeakReference;

    public DetailListAdapter(List<Detail> datas, IncomeFragment incomeFragment) {
        this.details = datas;
        this.incomeFragmentWeakReference = new WeakReference<>(incomeFragment);
    }

    /**
     * refresh data
     *
     * @param datas
     */
    public void refreshData(List<Detail> datas) {
        if (details.size() > 0) details.clear();
        details.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_detail_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailListViewHolder holder, int position) {
        final Detail detail = details.get(position);
        String classifyName = detail.getClassify().getName();
        holder.classifyName.setText(classifyName);
        holder.classifyName.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(Objects.requireNonNull(incomeFragmentWeakReference.get().getContext()), MyApp.getInstance().getDetailTypeResMap().get(classifyName)), null, null, null);
        holder.detailComment.setText(detail.getComment());
        holder.detailAmount.setText((detail.getType() == INCOME_TYPE ? "" : "-") + detail.getAmount());
        holder.detailRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(incomeFragmentWeakReference.get().getContext(), AddDetailActivity.class);
                intent.putExtra(DETAIL, detail);
                incomeFragmentWeakReference.get().startActivityForResult(intent, ADD_DETAIL_REQUEST_CODE);
            }
        });

        holder.detailRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(incomeFragmentWeakReference.get().getContext())
                        .setTitle("Warning")
                        .setMessage("Whether to delete this record？")
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long isSuccess = DbHelper.getInstance().deleteDetailById(detail.getId());
                                if (isSuccess > 0) {
                                    incomeFragmentWeakReference.get().refreshData(detail.getType());
                                    Toast.makeText(incomeFragmentWeakReference.get().getContext(), "Bill deleted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(incomeFragmentWeakReference.get().getContext(), "Bill deleted failed", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return details.size();
    }


    static class DetailListViewHolder extends RecyclerView.ViewHolder {

        TextView classifyName;
        TextView detailComment;
        TextView detailAmount;
        ConstraintLayout detailRoot;

        public DetailListViewHolder(View itemView) {
            super(itemView);
            classifyName = (TextView) itemView.findViewById(R.id.classifyName);
            detailComment = (TextView) itemView.findViewById(R.id.detailComment);
            detailAmount = (TextView) itemView.findViewById(R.id.detailAmount);
            detailRoot = (ConstraintLayout) itemView.findViewById(R.id.detailRoot);
        }
    }
}
