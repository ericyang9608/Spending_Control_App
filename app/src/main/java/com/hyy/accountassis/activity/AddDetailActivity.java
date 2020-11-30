package com.hyy.accountassis.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.SparseArrayCompat;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.hyy.accountassis.R;
import com.hyy.accountassis.app.MyApp;
import com.hyy.accountassis.bean.Classify;
import com.hyy.accountassis.bean.Detail;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.util.CommonUtils;
import com.hyy.accountassis.util.SPTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hyy.accountassis.constant.Constants.DETAIL;
import static com.hyy.accountassis.constant.Constants.DETAIL_TYPE;
import static com.hyy.accountassis.constant.Constants.EXPENSE_TYPE;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_ID;

public class AddDetailActivity extends AppCompatActivity {
    private static final String TAG = "AddDetailActivity";

    protected Toolbar toolbar;
    protected TextView accountTypeDesc;
    protected LinearLayout accountTypeRoot;
    protected RadioButton radioButton01;
    protected RadioButton radioButton02;
    protected RadioButton radioButton03;
    protected RadioButton radioButton04;
    protected RadioButton radioButton05;
    protected RadioButton radioButton06;
    protected RadioGroup detailType;
    protected RadioButton radioButton21;
    protected RadioButton radioButton22;
    protected RadioButton radioButton23;
    protected RadioButton radioButton24;
    protected RadioButton radioButton25;
    protected RadioButton radioButton26;
    protected RadioGroup detailType02;
    protected RadioButton incomeType;
    protected RadioButton expenseType;
    protected RadioGroup accountType;
    protected TextView detailDate;
    protected TextView accountTypeTitle;
    protected EditText detailAmount;
    protected EditText detailRemark;
    private Detail detail;
    private int type;
    private ExecutorService e1;

    private SparseArrayCompat<List<Classify>> classifySparse;
    private Classify selectedClassify;
    private String selectedDate;
    private SparseArrayCompat<RadioButton> classifySparseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_detail);
        classifySparseButton = new SparseArrayCompat<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        accountTypeDesc = (TextView) findViewById(R.id.accountTypeDesc);
        accountTypeRoot = (LinearLayout) findViewById(R.id.accountTypeRoot);
        radioButton01 = (RadioButton) findViewById(R.id.radioButton01);
        classifySparseButton.put(0, radioButton01);
        radioButton02 = (RadioButton) findViewById(R.id.radioButton02);
        classifySparseButton.put(1, radioButton02);
        radioButton03 = (RadioButton) findViewById(R.id.radioButton03);
        classifySparseButton.put(2, radioButton03);
        radioButton04 = (RadioButton) findViewById(R.id.radioButton04);
        classifySparseButton.put(3, radioButton04);
        radioButton05 = (RadioButton) findViewById(R.id.radioButton05);
        radioButton06 = (RadioButton) findViewById(R.id.radioButton06);
        classifySparseButton.put(4, radioButton05);
        detailType = (RadioGroup) findViewById(R.id.detailType);
        radioButton21 = (RadioButton) findViewById(R.id.radioButton21);
        classifySparseButton.put(5, radioButton21);
        radioButton22 = (RadioButton) findViewById(R.id.radioButton22);
        classifySparseButton.put(6, radioButton22);
        radioButton23 = (RadioButton) findViewById(R.id.radioButton23);
        classifySparseButton.put(7, radioButton23);
        radioButton24 = (RadioButton) findViewById(R.id.radioButton24);
        classifySparseButton.put(8, radioButton24);
        radioButton25 = (RadioButton) findViewById(R.id.radioButton25);
        radioButton26 = (RadioButton) findViewById(R.id.radioButton26);
        classifySparseButton.put(9, radioButton25);
        detailType02 = (RadioGroup) findViewById(R.id.detailType02);

        accountType = (RadioGroup) findViewById(R.id.accountType);
        incomeType = (RadioButton) findViewById(R.id.incomeType);
        expenseType = (RadioButton) findViewById(R.id.expenseType);

        detailDate = (TextView) findViewById(R.id.detailDate);
        accountTypeTitle = (TextView) findViewById(R.id.accountTypeTitle);
        detailAmount = (EditText) findViewById(R.id.detailAmount);
        detailRemark = (EditText) findViewById(R.id.detailRemark);
        classifySparse = new SparseArrayCompat<>();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DETAIL_TYPE)) {
            type = intent.getIntExtra(DETAIL_TYPE, INCOME_TYPE);
        }
        if (intent != null && intent.hasExtra(DETAIL)) {
            detail = intent.getParcelableExtra(DETAIL);
            if (detail != null) {
                type = detail.getType();
            }
        }
        e1 = Executors.newSingleThreadExecutor();
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        if (detail == null) {
            actionBar.setTitle("New Bill");
            accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.incomeType:
                            type = INCOME_TYPE;
                            radioButton05.setVisibility(View.INVISIBLE);
                            detailType02.setVisibility(View.GONE);
                            break;
                        case R.id.expenseType:
                            type = EXPENSE_TYPE;
                            radioButton05.setVisibility(View.VISIBLE);
                            detailType02.setVisibility(View.VISIBLE);
                            break;
                    }
                    HashMap<String, Integer> detailTypeMap = MyApp.getInstance().getDetailTypeResMap();
                    List<Classify> classifyListTmp = classifySparse.get(type);
                    classifyListTmp = classifyListTmp == null ? new ArrayList<Classify>() : classifyListTmp;
                    setRadioButton(detailTypeMap, classifyListTmp);
                }
            });
            accountTypeTitle.setTextColor(ContextCompat.getColor(this, R.color.blueColor));
            accountType.setVisibility(View.VISIBLE);
            accountTypeDesc.setVisibility(View.GONE);
        } else {
            actionBar.setTitle("Edit Bill");
            accountTypeTitle.setTextColor(ContextCompat.getColor(this, R.color.black3));
            accountTypeDesc.setText(type == INCOME_TYPE ? "Income" : "Expense");
            accountType.setVisibility(View.GONE);
            accountTypeDesc.setVisibility(View.VISIBLE);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDetailActivity.this.finish();
            }
        });
        if (detail != null) {
            selectedDate = detail.getCreate_date();
            detailAmount.setText(String.valueOf(detail.getAmount()));
            detailRemark.setText(detail.getComment());
        } else {
            selectedDate = CommonUtils.getCurrentDateTime("yyyy/MM/dd");
        }
        detailDate.setText(selectedDate);
        detailDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerBuilder(AddDetailActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        selectedDate = CommonUtils.getDateStr(date, "yyyy/MM/dd");
                        detailDate.setText(selectedDate);
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})
                        .setCancelText("Cancel")
                        .setSubmitText("OK")
                        .setTitleText("Select Date")
                        .setTitleSize(16)
                        .setSubCalSize(14)
                        .setOutSideCancelable(false)
                        .setTitleColor(Color.WHITE)
                        .setSubmitColor(Color.WHITE)
                        .setCancelColor(Color.WHITE)
                        .setLabel("", "", "", "", "", "")
                        .setTitleBgColor(ContextCompat.getColor(AddDetailActivity.this, R.color.blueColor))
                        .build().show();
            }
        });
        e1.execute(new Runnable() {
            @Override
            public void run() {
                classifySparse.put(INCOME_TYPE, DbHelper.getInstance().queryClassifyListByType(INCOME_TYPE));
                classifySparse.put(EXPENSE_TYPE, DbHelper.getInstance().queryClassifyListByType(EXPENSE_TYPE));
                AddDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, Integer> detailTypeMap = MyApp.getInstance().getDetailTypeResMap();
                        List<Classify> classifyListTmp = classifySparse.get(type);
                        classifyListTmp = classifyListTmp == null ? new ArrayList<Classify>() : classifyListTmp;
                        incomeType.setChecked(type == INCOME_TYPE);
                        expenseType.setChecked(type == EXPENSE_TYPE);
                        detailType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                int checkedId = radioGroup.getCheckedRadioButtonId();
                                if (checkedId == R.id.radioButton06) return;
                                radioButton26.setChecked(true);
                                List<Classify> classifyListTmp = classifySparse.get(type);
                                RadioButton radioButton = radioGroup.findViewById(checkedId);
                                selectedClassify = Objects.requireNonNull(classifyListTmp).get(classifySparseButton.keyAt(classifySparseButton.indexOfValue(radioButton)));
                            }
                        });
                        detailType02.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                int checkedId = radioGroup.getCheckedRadioButtonId();
                                if (checkedId == R.id.radioButton26) return;
                                radioButton06.setChecked(true);
                                List<Classify> classifyListTmp = classifySparse.get(type);
                                RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                                selectedClassify = Objects.requireNonNull(classifyListTmp).get(classifySparseButton.keyAt(classifySparseButton.indexOfValue(radioButton)));
                            }
                        });
                        setRadioButton(detailTypeMap, classifyListTmp);
                    }
                });
            }
        });
    }

    private void setRadioButton(HashMap<String, Integer> detailTypeMap, List<Classify> classifyListTmp) {
        for (int i = 0; i < classifyListTmp.size(); i++) {
            String classifyName = classifyListTmp.get(i).getName();
            RadioButton radioButton = classifySparseButton.get(i);
            radioButton.setText(classifyName);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(AddDetailActivity.this, detailTypeMap.get(classifyName)), null, null);
            if (detail != null) {
                String classifyNameSelected = detail.getClassify().getName();
                if (TextUtils.equals(classifyName, classifyNameSelected)) {
                    radioButton.setChecked(true);
                    selectedClassify = classifyListTmp.get(i);
                }
            } else if (i == 0) {
                radioButton.setChecked(true);
                selectedClassify = classifyListTmp.get(0);
            }
        }
        if (type == EXPENSE_TYPE) {
            radioButton05.setVisibility(View.VISIBLE);
            detailType02.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (selectedClassify == null) {
                    Toast.makeText(AddDetailActivity.this, "The bill type had no choice", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (isInputEmpty(selectedDate, "The date can not be empty...")) break;
                String amount = detailAmount.getText().toString().trim();
                String comment = detailRemark.getText().toString().trim();
                if (isInputEmpty(amount, "The amount can't be empty...")) break;
                if (isInputEmpty(comment, "Comment cannot be empty...")) break;
                if (detail != null) {
                    detail.setClassify_id(selectedClassify.getId());
                    detail.setCreate_date(selectedDate);
                    detail.setAmount(Double.parseDouble(amount));
                    detail.setComment(comment);
                    detail.setUpdate_time(System.currentTimeMillis());
                    e1.execute(new Runnable() {
                        @Override
                        public void run() {
                            final long isSuccess = DbHelper.getInstance().updateDetailById(detail);
                            AddDetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuccess > 0) {
                                        Toast.makeText(AddDetailActivity.this, "Edit the bill successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra(DETAIL_TYPE, detail.getType());
                                        AddDetailActivity.this.setResult(RESULT_OK, intent);
                                        AddDetailActivity.this.finish();
                                    } else {
                                        Toast.makeText(AddDetailActivity.this, "Edit the bill failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    detail = new Detail();
                    detail.setType(type);
                    detail.setAccount_id((int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0));
                    detail.setClassify_id(selectedClassify.getId());
                    detail.setCreate_date(selectedDate);
                    detail.setAmount(Double.parseDouble(amount));
                    detail.setComment(comment);
                    detail.setCreate_time(System.currentTimeMillis());
                    detail.setUpdate_time(System.currentTimeMillis());
                    e1.execute(new Runnable() {
                        @Override
                        public void run() {
                            final long isSuccess = DbHelper.getInstance().insertDetail(detail);
                            AddDetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuccess > 0) {
                                        Toast.makeText(AddDetailActivity.this, "The new bill successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra(DETAIL_TYPE, type);
                                        AddDetailActivity.this.setResult(RESULT_OK, intent);
                                        AddDetailActivity.this.finish();
                                    } else {
                                        Toast.makeText(AddDetailActivity.this, "The new bill failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Determine whether the edittext value is empty
     *
     * @param content
     * @param msg
     * @return
     */
    public boolean isInputEmpty(String content, String msg) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(AddDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}