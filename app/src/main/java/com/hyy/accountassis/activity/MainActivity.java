package com.hyy.accountassis.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.google.android.material.navigation.NavigationView;
import com.hyy.accountassis.R;
import com.hyy.accountassis.app.MyApp;
import com.hyy.accountassis.bean.Account;
import com.hyy.accountassis.bean.Classify;
import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.fragment.AboutAppFragment;
import com.hyy.accountassis.fragment.AnnualBillFragment;
import com.hyy.accountassis.fragment.BudgetDetailFragment;
import com.hyy.accountassis.fragment.ChartFragment;
import com.hyy.accountassis.fragment.DetailFragment;
import com.hyy.accountassis.util.CommonUtils;
import com.hyy.accountassis.util.SPTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hyy.accountassis.constant.Constants.EXPENSE_TYPE;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_ID;
import static com.hyy.accountassis.constant.Constants.LOCAL_ACCOUNT_NAME;


@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    protected Toolbar toolbar;
    private TextView title;
    protected NavigationView nav;
    protected DrawerLayout drawerLayout;
    private DetailFragment detailFragment;
    private BudgetDetailFragment budgetDetailFragment;
    private ChartFragment chartFragment;
    private AnnualBillFragment annualBillFragment;
    private ExecutorService e1;
    private ActionBarDrawerToggle mDrawerToggle;

    private Menu dateMenu;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        nav = findViewById(R.id.nav);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e1.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<Account> accountList = DbHelper.getInstance().queryAccountList();
                        if (accountList != null && accountList.size() > 0) {
                            final List<String> accountNameList = new ArrayList<>();
                            final HashMap<String, Account> accountHashMap = new HashMap<>();
                            for (Account account : accountList) {
                                accountNameList.add(account.getName());
                                accountHashMap.put(account.getName(), account);
                            }
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final String[] items = new String[accountList.size()];
                                    accountNameList.toArray(items);
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                                    alertBuilder.setTitle("The book list");
                                    String accountNameStrTmp = (String) SPTool.getInstance().getParam(LOCAL_ACCOUNT_NAME, "");
                                    int index = accountNameList.indexOf(accountNameStrTmp);
                                    index = Math.max(index, 0);
                                    final int[] checkedIndex = {index};
                                    alertBuilder.setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int index) {
                                            checkedIndex[0] = index;
                                        }
                                    });
                                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            String accountName = items[checkedIndex[0]];
                                            title.setText(accountName + "-detail");
                                            Account account = accountHashMap.get(accountName);
                                            SPTool.getInstance().setParam(LOCAL_ACCOUNT_ID, account.getId());
                                            SPTool.getInstance().setParam(LOCAL_ACCOUNT_NAME, account.getName());
                                            arg0.dismiss();
                                            refreshFragment();
                                        }
                                    });
                                    alertBuilder.setNegativeButton("New books", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            arg0.dismiss();
                                            showAddDialog();
                                        }
                                    });
                                    alertBuilder.create().show();
                                }
                            });
                        }
                    }
                });
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mDrawerToggle);

        nav.setNavigationItemSelectedListener(this);
        e1 = Executors.newSingleThreadExecutor();
        getAccount();
    }

    private void getAccount() {
        if (e1 == null) {
            e1 = Executors.newSingleThreadExecutor();
        }
        e1.execute(new Runnable() {
            @Override
            public void run() {
                Resources resource = MainActivity.this.getResources();
                String[] incomeNameArr = resource.getStringArray(R.array.income_name);
                TypedArray taIncome = resource.obtainTypedArray(R.array.income_res);
                for (int i = 0; i < incomeNameArr.length; i++) {
                    MyApp.getInstance().getDetailTypeResMap().put(incomeNameArr[i], taIncome.getResourceId(i, 0));
                }
                taIncome.recycle();
                String[] expenseNameArr = resource.getStringArray(R.array.expense_name);
                TypedArray taExpense = resource.obtainTypedArray(R.array.expense_res);
                for (int i = 0; i < expenseNameArr.length; i++) {
                    MyApp.getInstance().getDetailTypeResMap().put(expenseNameArr[i], taExpense.getResourceId(i, 0));
                }
                taExpense.recycle();
            }
        });
        final int accountId = (int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0);
        if (accountId == 0) {
            //Local have no books, pop up the dialog of a new book
            showAddDialog();
        } else if (accountId > 0) {
            e1.execute(new Runnable() {
                @Override
                public void run() {
                    account = DbHelper.getInstance().queryAccountById(accountId);
                    if (account != null) {
                        title.setText(account.getName() + "-detail");
                        switch2Detail();
                    }
                }
            });
        } else if (accountId == -1001) {
            nav.setCheckedItem(R.id.nav_detail);
            title.performClick();
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null);
        final Dialog addAccountDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("New books")
                .setView(dialogView)
                .setCancelable(false)
                .create();
        final EditText accountName = (EditText) dialogView.findViewById(R.id.accountName);
        AppCompatButton ok = (AppCompatButton) dialogView.findViewById(R.id.ok);
        AppCompatButton cancel = (AppCompatButton) dialogView.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String accountNameStr = accountName.getText().toString().trim();
                if (TextUtils.isEmpty(accountNameStr)) {
                    Toast.makeText(MainActivity.this, "Book name cannot be empty！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (e1 == null) {
                    e1 = Executors.newSingleThreadExecutor();
                }
                e1.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<Classify> classifyList = DbHelper.getInstance().queryClassifyListByType(INCOME_TYPE);
                        if (classifyList == null || classifyList.size() == 0) {
                            classifyList = new ArrayList<>();
                            Resources resource = MainActivity.this.getResources();
                            String[] incomeNameArr = resource.getStringArray(R.array.income_name);
                            TypedArray taIncome = resource.obtainTypedArray(R.array.income_res);
                            for (int i = 0; i < incomeNameArr.length; i++) {
                                Classify classify = new Classify();
                                classify.setType(INCOME_TYPE);
                                classify.setName(incomeNameArr[i]);
                                MyApp.getInstance().getDetailTypeResMap().put(incomeNameArr[i], taIncome.getResourceId(i, 0));
                                classifyList.add(classify);
                            }
                            taIncome.recycle();
                            String[] expenseNameArr = resource.getStringArray(R.array.expense_name);
                            TypedArray taExpense = resource.obtainTypedArray(R.array.expense_res);
                            for (int i = 0; i < expenseNameArr.length; i++) {
                                Classify classify = new Classify();
                                classify.setType(EXPENSE_TYPE);
                                classify.setName(expenseNameArr[i]);
                                MyApp.getInstance().getDetailTypeResMap().put(expenseNameArr[i], taExpense.getResourceId(i, 0));
                                classifyList.add(classify);
                            }
                            taExpense.recycle();
                            DbHelper.getInstance().insertClassifies(classifyList);
                        }
                        final Account account = DbHelper.getInstance().queryAccountByName(accountNameStr);
                        if (account != null) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "The book has been in existence, cannot repeat creation！！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        final Account accountTmp = new Account();
                        accountTmp.setName(accountNameStr);
                        accountTmp.setCreate_time(System.currentTimeMillis());
                        final Pair<Long, Integer> result = DbHelper.getInstance().insertAccount(accountTmp);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.first > 0) {
                                    Toast.makeText(MainActivity.this, "New books successful！！", Toast.LENGTH_SHORT).show();
                                    title.setText(accountNameStr + "-detail");
                                    SPTool.getInstance().setParam(LOCAL_ACCOUNT_ID, result.second);
                                    SPTool.getInstance().setParam(LOCAL_ACCOUNT_NAME, accountNameStr);
                                    refreshFragment();
                                } else {
                                    Toast.makeText(MainActivity.this, "New books failed！！", Toast.LENGTH_SHORT).show();
                                }
                                addAccountDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int accountId = (int) SPTool.getInstance().getParam(LOCAL_ACCOUNT_ID, 0);
                if (accountId == 0) {
                    Toast.makeText(MainActivity.this, "No books, must create a new！！", Toast.LENGTH_SHORT).show();
                } else {
                    addAccountDialog.dismiss();
                }
            }
        });
        if (!addAccountDialog.isShowing()) {
            addAccountDialog.show();
        }
    }

    private void refreshFragment() {
        if (R.id.nav_detail == nav.getCheckedItem().getItemId()) {
            if (detailFragment != null) {
                detailFragment.refreshData(INCOME_TYPE);
            } else {
                detailFragment = new DetailFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content, detailFragment).commit();
            }
        } else if (R.id.nav_chart == nav.getCheckedItem().getItemId()) {
            if (chartFragment != null) {
                chartFragment.refreshData(INCOME_TYPE);
            }
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_filter, menu);
        if (menu != null) {
            this.dateMenu = menu;
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    MenuBuilder menuBuilder = (MenuBuilder) menu;
                    menuBuilder.setOptionalIconsVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.date) {
            new TimePickerBuilder(MainActivity.this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    switch (nav.getCheckedItem().getItemId()) {
                        case R.id.nav_detail:
                        default:
                            if (detailFragment != null) {
                                detailFragment.refreshDataByDate(INCOME_TYPE, CommonUtils.getDateStr(date, "yyyy/MM"));
                            }
                            break;
                        case R.id.nav_budget_detail:
                            if (budgetDetailFragment != null) {
                                budgetDetailFragment.refreshDataByDate(INCOME_TYPE, CommonUtils.getDateStr(date, "yyyy/MM"));
                            }
                            break;
                        case R.id.nav_chart:
                            if (chartFragment != null) {
                                chartFragment.refreshDataByDate(INCOME_TYPE, CommonUtils.getDateStr(date, "yyyy/MM"));
                            }
                            break;
                        case R.id.nav_bill:
                            if (annualBillFragment != null) {
                                annualBillFragment.refreshDataByDate(INCOME_TYPE, CommonUtils.getDateStr(date, "yyyy"));
                            }
                            break;
                    }
                }
            }).setType(new boolean[]{true, R.id.nav_bill != nav.getCheckedItem().getItemId(), false, false, false, false})
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
                    .setTitleBgColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor))
                    .build().show();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Switch the detail
     */
    private void switch2Detail() {
        if (detailFragment == null) {
            detailFragment = new DetailFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, detailFragment).commit();
    }

    /**
     * Switch the chart
     */
    private void switch2BudgetDetail() {
        if (budgetDetailFragment == null) {
            budgetDetailFragment = new BudgetDetailFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, budgetDetailFragment).commit();
    }

    /**
     * Switch the chart
     */
    private void switch2Chart() {
        if (chartFragment == null) {
            chartFragment = new ChartFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, chartFragment).commit();
    }

    /**
     * Switch the chart
     */
    private void switch2Bill() {
        if (annualBillFragment == null) {
            annualBillFragment = new AnnualBillFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, annualBillFragment).commit();
    }

    /**
     * Switch the about app
     */
    private void switch2App() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new AboutAppFragment()).commit();
    }

    // Used to calculate the return key click time interval
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "Again according to exit the program", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String accountNameStr = (String) SPTool.getInstance().getParam(LOCAL_ACCOUNT_NAME, "The default books");
        switch (item.getItemId()) {
            case R.id.nav_detail:
            default:
                switch2Detail();
                title.setText(accountNameStr + "-detail");
                dateMenu.getItem(0).setVisible(true);
                break;
            case R.id.nav_budget_detail:
                switch2BudgetDetail();
                title.setText(accountNameStr + "-budget-detail");
                dateMenu.getItem(0).setVisible(true);
                break;
            case R.id.nav_chart:
                switch2Chart();
                title.setText(accountNameStr + "-chart");
                dateMenu.getItem(0).setVisible(true);
                break;
            case R.id.nav_bill:
                switch2Bill();
                title.setText(accountNameStr + "-bills");
                dateMenu.getItem(0).setVisible(true);
                break;
            case R.id.nav_app:
                switch2App();
                title.setText("about-" + MainActivity.this.getResources().getString(R.string.app_name));
                dateMenu.getItem(0).setVisible(false);
                break;
            case R.id.nav_exit:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit")
                        .setMessage("Whether the book sure exit？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPTool.getInstance().setParam(LOCAL_ACCOUNT_ID, -1001);
                                SPTool.getInstance().remove(LOCAL_ACCOUNT_NAME);
                                dialog.dismiss();
                                MainActivity.this.finish();
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
                break;
        }
        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }
}