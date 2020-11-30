package com.hyy.accountassis.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.hyy.accountassis.R;
import com.hyy.accountassis.app.MyApp;
import com.hyy.accountassis.bean.Detail;

import java.util.List;
import java.util.Objects;

public class ClassifyRankAdapter extends RecyclerView.Adapter<ClassifyRankAdapter.ClassifyRankViewHolder> {
    private static final String TAG = "ClassifyRankAdapter";

    private List<Pair<Detail, Float>> details;
    private float amountTotal;

    public ClassifyRankAdapter(List<Pair<Detail, Float>> datas, float total) {
        this.details = datas;
        this.amountTotal = total;
    }

    /**
     * refresh data
     *
     * @param datas
     */
    public void refreshData(List<Pair<Detail, Float>> datas, float total) {
        if (details.size() > 0) details.clear();
        details.addAll(datas);
        this.amountTotal = total;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassifyRankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClassifyRankViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_classify_rank, parent, false));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ClassifyRankViewHolder holder, int position) {
        final Pair<Detail, Float> detailFloatPair = details.get(position);
        Detail detail = detailFloatPair.first;
        String classifyName = Objects.requireNonNull(detail).getClassify().getName();
        holder.classifyIcon.setBackgroundResource(MyApp.getInstance().getDetailTypeResMap().get(classifyName));
        holder.classifyName.setText(classifyName);
        holder.classifyPercent.setText(String.format("%.2f", (detailFloatPair.second / amountTotal * 100f)) + "%");
        holder.classifyAmount.setText(String.format("%.2f", detailFloatPair.second));
        holder.progress.setProgress(position == 0 ? 100 : (int) (detailFloatPair.second / details.get(0).second * 100));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }


    static class ClassifyRankViewHolder extends RecyclerView.ViewHolder {

        ImageView classifyIcon;
        TextView classifyName;
        TextView classifyPercent;
        TextView classifyAmount;
        ProgressBar progress;

        public ClassifyRankViewHolder(View rootView) {
            super(rootView);
            classifyIcon = (ImageView) rootView.findViewById(R.id.classifyIcon);
            classifyName = (TextView) rootView.findViewById(R.id.classifyName);
            classifyPercent = (TextView) rootView.findViewById(R.id.classifyPercent);
            classifyAmount = (TextView) rootView.findViewById(R.id.classifyAmount);
            progress = (ProgressBar) rootView.findViewById(R.id.progress);
        }
    }
}
