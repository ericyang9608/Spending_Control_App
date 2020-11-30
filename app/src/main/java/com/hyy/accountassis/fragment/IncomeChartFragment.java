package com.hyy.accountassis.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hyy.accountassis.R;
import com.hyy.accountassis.adapter.ClassifyRankAdapter;
import com.hyy.accountassis.bean.Detail;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.util.CommonUtils;
import com.hyy.accountassis.util.SPTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.hyy.accountassis.constant.Constants.DETAIL_TYPE;
import static com.hyy.accountassis.constant.Constants.EXPENSE_TYPE;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_ID;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class IncomeChartFragment extends BaseFragment {


    protected PieChart pieChart;
    protected TextView date;
    protected TextView amount;
    protected ClassifyRankAdapter classifyRankAdapter;
    protected RecyclerView rank;
    private int type;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DETAIL_TYPE)) {
            type = bundle.getInt(DETAIL_TYPE, INCOME_TYPE);
        }
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        date = (TextView) rootView.findViewById(R.id.date);
        pieChart = (PieChart) rootView.findViewById(R.id.pie_chart);
        amount = (TextView) rootView.findViewById(R.id.amount);
        rank = (RecyclerView) rootView.findViewById(R.id.rank);
        classifyRankAdapter = new ClassifyRankAdapter(new ArrayList<Pair<Detail, Float>>(), 1);
        rank.setAdapter(classifyRankAdapter);
        refreshData(1);
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
                final List<Pair<Detail, Float>> result = DbHelper.getInstance().queryClassifyDetailByTypeDate(accountId, type, createDate);
                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        float amountTotal = 0;
                        if (result != null && result.size() > 0) {
                            ArrayList<PieEntry> incomePieEntryList = new ArrayList<>();
                            for (Pair<Detail, Float> detailPair : result) {
                                PieEntry pieEntry = new PieEntry(detailPair.second, detailPair.first.getClassify().getName());
                                incomePieEntryList.add(pieEntry);
                                amountTotal += detailPair.second;
                            }
                            showPieChart(pieChart, incomePieEntryList);
                        } else {
                            amountTotal = 0;
                            pieChart.setNoDataText("No chart is available for this period!");
                            pieChart.clear();
                            pieChart.notifyDataSetChanged();
                        }
                        switch (type) {
                            case INCOME_TYPE:
                                date.setText(createDate + " Income Chart");
                                amount.setText("Total income：" + amountTotal);
                                break;
                            case EXPENSE_TYPE:
                                date.setText(createDate + " Expense Chart");
                                amount.setText("Total expense： -" + amountTotal);
                                break;
                        }
                        classifyRankAdapter.refreshData(result, amountTotal);
                    }
                });
            }
        });
    }

    private void showPieChart(PieChart pieChart, List<PieEntry> pieList) {
        PieDataSet dataSet = new PieDataSet(pieList, "Level：");
        // Set the color list, let different block according to different color, the color of the below is I think a good collection, more bright
        ArrayList<Integer> colors = new ArrayList<Integer>();
        int[] MATERIAL_COLORS = {
                Color.rgb(200, 172, 255)
        };
        for (int c : MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);

        Description description = new Description();
        description.setEnabled(false);
        pieChart.setDescription(description);
        //Set translucent circle radius, 0 for transparency
        pieChart.setTransparentCircleRadius(0f);

        //Set the initial rotation Angle
        pieChart.setRotationAngle(-15);

        //Data cables from graphics of internal boundary distance, as a percentage
        dataSet.setValueLinePart1OffsetPercentage(80f);

        //Set the color of the cable
        dataSet.setValueLineColor(Color.LTGRAY);
        // Cable outside the pie chart
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // The gap between the set of pizza
        dataSet.setSliceSpace(1f);
        dataSet.setHighlightEnabled(true);
        // Don't show legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        // And around a distance apart, display the data
        pieChart.setExtraOffsets(26, 5, 26, 5);
        // Sets whether pieChart chart can be manually rotated
        pieChart.setRotationEnabled(false);
        // Settings piecahrt chart click Item highlight is available
        pieChart.setHighlightPerTapEnabled(true);
        // Set the pieChart charts show animation effects, animation run of 1.4 seconds
        pieChart.animateY(1400);
        //Sets whether pieChart show only the percentage does not display the text on the pie chart
        pieChart.setDrawEntryLabels(true);
        //Whether to draw text PieChart internal center
        pieChart.setDrawCenterText(false);
        // Draw the content value, set the font color size
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.DKGRAY);
        pieChart.setData(pieData);
        //Update the piechart view
        pieChart.postInvalidate();
    }

}