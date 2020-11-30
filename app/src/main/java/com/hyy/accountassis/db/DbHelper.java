package com.hyy.accountassis.db;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.util.Pair;

import com.hyy.accountassis.bean.Account;
import com.hyy.accountassis.bean.Bill;
import com.hyy.accountassis.bean.Budget;
import com.hyy.accountassis.bean.Classify;
import com.hyy.accountassis.bean.Detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hyy.accountassis.constant.Constants.EXPENSE_TYPE;
import static com.hyy.accountassis.constant.Constants.INCOME_TYPE;

public class DbHelper {

    private static final String DB_NAME = "account_db";

    /**
     * The version of the database
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * The name of the table, in turn, be tables, books classification table, list
     */
    private final String DATABASE_TABLE_ACCOUNT = "tb_account";
    private final String DATABASE_TABLE_CLASSIFY = "tb_classify";
    private final String DATABASE_TABLE_BUDGET = "tb_budget";
    private final String DATABASE_TABLE_DETAIL = "tb_detail";


    /**
     * The name of rows in the table
     */
    private final String KEY_ID = "id";
    private final String KEY_NAME = "name";
    private final String KEY_CREATE_TIME = "create_time";

    /**
     * The name of rows in the table
     */
    private final String KEY_TYPE = "type";


    /**
     * The name of rows in the table
     */
    private final String KEY_ACCOUNT_ID = "account_id";
    private final String KEY_CLASSIFY_ID = "classify_id";
    private final String KEY_COMMENT = "comment";
    private final String KEY_AMOUNT = "amount";
    private final String KEY_UPDATE_TIME = "update_time";
    private final String KEY_CREATE_DATE = "create_date";

    private static List<String> tables;

    private Context mCtx;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database of the auxiliary class
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        /**
         * Create a table
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            for (String tableSql : tables) {
                db.execSQL(tableSql);
            }
        }

        /**
         * An updated version
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    private static class Singleton {
        private static final DbHelper INSTANCE = new DbHelper();
    }


    /**
     * For single object
     *
     * @return
     */
    public static DbHelper getInstance() {
        return DbHelper.Singleton.INSTANCE;
    }

    /**
     * Initialize the context
     *
     * @param ctx
     */
    public DbHelper init(Context ctx) {
        this.mCtx = ctx;
        tables = new ArrayList<>();
        final String CREATE_TABLE_ACCOUNT = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_ACCOUNT + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_NAME + " text not null, " + KEY_CREATE_TIME + " bigint);";
        tables.add(CREATE_TABLE_ACCOUNT);
        final String CREATE_TABLE_CLASSIFY = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_CLASSIFY + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_TYPE + " integer, " + KEY_NAME + " text not null);";
        tables.add(CREATE_TABLE_CLASSIFY);
        final String CREATE_TABLE_BUDGET = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_BUDGET + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_ACCOUNT_ID + " integer, " + KEY_AMOUNT + " real, " + KEY_CREATE_DATE + " text not null);";
        tables.add(CREATE_TABLE_BUDGET);
        final String CREATE_TABLE_DETAIL = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_DETAIL + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_ACCOUNT_ID + " integer, " + KEY_TYPE + " integer, " +
                KEY_CLASSIFY_ID + " integer, " + KEY_COMMENT + " text not null, " + KEY_AMOUNT + " real, " + KEY_CREATE_DATE + " text not null, " + KEY_CREATE_TIME + " bigint, " + KEY_UPDATE_TIME + " bigint);";
        tables.add(CREATE_TABLE_DETAIL);
        openDb();
        return this;
    }

    /**
     * Open the database
     *
     * @return
     * @throws SQLException
     */
    public DbHelper openDb() throws SQLException {
        if (mCtx != null) {
            mDbHelper = new DatabaseHelper(mCtx);
            mDb = mDbHelper.getReadableDatabase();
        }
        return this;
    }

    /**
     * Close the database
     */
    public void closeDb() {
        mDbHelper.close();
    }


    /**
     * The new books
     *
     * @param account
     * @return
     */
    public Pair<Long, Integer> insertAccount(Account account) {
        Pair<Long, Integer> result = null;
        mDb.beginTransaction();
        try {
            ContentValues accountValue = new ContentValues();
            accountValue.put(KEY_NAME, account.getName());
            accountValue.put(KEY_CREATE_TIME, account.getCreate_time());
            long isSuccess = mDb.insert(DATABASE_TABLE_ACCOUNT, null, accountValue);
            Cursor cursor = mDb.rawQuery("select last_insert_rowid() from " + DATABASE_TABLE_ACCOUNT, null);
            int id = 0;
            if (cursor.moveToFirst()) id = cursor.getInt(0);
            result = new Pair<>(isSuccess, id);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return result;
    }

    /**
     * According to the name lookup books
     *
     * @param name
     * @return
     */
    public Account queryAccountByName(String name) {
        Account account = null;
        Cursor cursor = mDb.query(DATABASE_TABLE_ACCOUNT, null, KEY_NAME + "= ?", new String[]{name},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                account = getAccount(cursor);
            }
            cursor.close();
        }
        return account;
    }

    /**
     * According to the id to find books
     *
     * @param id
     * @return
     */
    public Account queryAccountById(int id) {
        Account account = null;
        Cursor cursor = mDb.query(DATABASE_TABLE_ACCOUNT, null, KEY_ID + "= ?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                account = getAccount(cursor);
            }
            cursor.close();
        }
        return account;
    }

    private Account getAccount(Cursor cursor) {
        Account account;
        account = new Account();
        account.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        account.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        account.setCreate_time(cursor.getLong(cursor.getColumnIndex(KEY_CREATE_TIME)));
        return account;
    }

    /**
     * Find all the books
     *
     * @return
     */
    public List<Account> queryAccountList() {
        List<Account> accountList = new ArrayList<>();
        Cursor cursor = mDb.query(DATABASE_TABLE_ACCOUNT, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                accountList.add(getAccount(cursor));
            }
            cursor.close();
        }
        return accountList;
    }


    /**
     * The new classification information
     *
     * @param classifyList
     * @return
     */
    public long insertClassifies(List<Classify> classifyList) {
        long result = -1;
        mDb.beginTransaction();
        try {
            for (Classify classify : classifyList) {
                ContentValues classifyValue = new ContentValues();
                classifyValue.put(KEY_TYPE, classify.getType());
                classifyValue.put(KEY_NAME, classify.getName());
                result = mDb.insert(DATABASE_TABLE_CLASSIFY, null, classifyValue);
                if (result < 0) return result;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return result;
    }


    /**
     * According to the type all query classification
     *
     * @return
     */
    public List<Classify> queryClassifyListByType(int type) {
        List<Classify> classifyList = new ArrayList<>();
        Cursor cursor = mDb.query(DATABASE_TABLE_CLASSIFY, null, KEY_TYPE + "= ?", new String[]{String.valueOf(type)}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                classifyList.add(getClassify(cursor));
            }
            cursor.close();
        }
        return classifyList;
    }

    /**
     * According to the id to find classification information
     *
     * @param id
     * @return
     */
    public Classify queryClassifyById(int id) {
        Classify classify = null;
        Cursor cursor = mDb.query(DATABASE_TABLE_CLASSIFY, null, KEY_ID + "= ?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                classify = getClassify(cursor);
            }
            cursor.close();
        }
        return classify;
    }

    private Classify getClassify(Cursor cursor) {
        Classify classify = new Classify();
        classify.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        classify.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        classify.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
        return classify;
    }

    /**
     * setup budget
     *
     * @param budget
     * @return
     */
    public long insertBudget(Budget budget) {
        long result = -1;
        mDb.beginTransaction();
        try {
            Budget budgetTmp = queryBudgetByMonth(budget.getAccount_id(), budget.getMonth());
            if (budgetTmp == null) {
                ContentValues budgetValue = new ContentValues();
                budgetValue.put(KEY_AMOUNT, budget.getAmount());
                budgetValue.put(KEY_ACCOUNT_ID, budget.getAccount_id());
                budgetValue.put(KEY_CREATE_DATE, budget.getMonth());
                result = mDb.insert(DATABASE_TABLE_BUDGET, null, budgetValue);
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return result;
    }

    /**
     * Update the budget information
     *
     * @param budget
     * @return
     */
    public long updateBudgetById(Budget budget) {
        long result = -1;
        ContentValues budgetValue = new ContentValues();
        budgetValue.put(KEY_AMOUNT, budget.getAmount());
        result = mDb.update(DATABASE_TABLE_BUDGET, budgetValue, KEY_ID + " =?", new String[]{String.valueOf(budget.getId())});
        return result;
    }

    /**
     * @param accountId
     * @param month
     * @return
     */
    public Budget queryBudgetByMonth(int accountId, String month) {
        Budget budget = null;
        String selection = KEY_ACCOUNT_ID + "=? and " + KEY_CREATE_DATE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(accountId), month};
        Cursor cursor = mDb.query(DATABASE_TABLE_BUDGET, null, selection, selectionArgs, null, null, null, null);
        if (cursor.moveToFirst()) {
            budget = new Budget();
            budget.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            budget.setAccount_id(accountId);
            budget.setAmount(cursor.getFloat(cursor.getColumnIndex(KEY_AMOUNT)));
            budget.setAccount(queryAccountById(accountId));
            budget.setMonth(cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE)));
        }
        cursor.close();
        return budget;
    }


    /**
     * The construction of a new subsidiary
     *
     * @param detail
     * @return
     */
    public long insertDetail(Detail detail) {
        long result = -1;
        mDb.beginTransaction();
        try {
            ContentValues detailValue = new ContentValues();
            detailValue.put(KEY_ACCOUNT_ID, detail.getAccount_id());
            detailValue.put(KEY_TYPE, detail.getType());
            detailValue.put(KEY_CLASSIFY_ID, detail.getClassify_id());
            detailValue.put(KEY_AMOUNT, detail.getAmount());
            detailValue.put(KEY_COMMENT, detail.getComment());
            detailValue.put(KEY_CREATE_DATE, detail.getCreate_date());
            detailValue.put(KEY_CREATE_TIME, detail.getCreate_time());
            detailValue.put(KEY_UPDATE_TIME, detail.getUpdate_time());
            result = mDb.insert(DATABASE_TABLE_DETAIL, null, detailValue);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return result;
    }

    /**
     * According to the classification and the creation date for detailed list
     *
     * @param type
     * @param createDate
     * @return
     */
    public Pair<Double, List<Detail>> queryDetailListByTypeDate(int accountId, int type, String createDate, String groupBy) {
        List<Detail> detailList = new ArrayList<>();
        double amountSum = 0;
        String selection = KEY_ACCOUNT_ID + "=? and " + KEY_TYPE + "=? and " + KEY_CREATE_DATE + " LIKE '" + createDate + "%'";
        String[] selectionArgs = new String[]{String.valueOf(accountId), String.valueOf(type)};
        Cursor cursor = mDb.query(DATABASE_TABLE_DETAIL, null, selection, selectionArgs, groupBy, null, KEY_CREATE_DATE + " DESC", null);
        while (cursor.moveToNext()) {
            detailList.add(getDetail(cursor));
        }
        cursor = mDb.rawQuery("select sum(" + KEY_AMOUNT + ") from " + DATABASE_TABLE_DETAIL + " where " + selection, selectionArgs);
        if (cursor.moveToFirst()) {
            amountSum = cursor.getDouble(0);
        }
        cursor.close();
        return new Pair<>(amountSum, detailList);
    }

    /**
     * According to the classification and the creation date for detailed list
     *
     * @param type
     * @param createDate
     * @return
     */
    public Float queryAmountTotalByTypeDate(int accountId, int type, String createDate) {
        float amountSum = 0;
        String selection = KEY_ACCOUNT_ID + "=? and " + KEY_TYPE + "=? and " + KEY_CREATE_DATE + " LIKE '" + createDate + "%'";
        String[] selectionArgs = new String[]{String.valueOf(accountId), String.valueOf(type)};
        Cursor cursor = mDb.rawQuery("select sum(" + KEY_AMOUNT + ") from " + DATABASE_TABLE_DETAIL + " where " + selection, selectionArgs);
        if (cursor.moveToFirst()) {
            amountSum = cursor.getFloat(0);
        }
        cursor.close();
        return amountSum;
    }

    /**
     * According to the classification and the creation date for detailed list
     *
     * @param type
     * @param createDate
     * @return
     */
    public List<Pair<Detail, Float>> queryClassifyDetailByTypeDate(int accountId, int type, String createDate) {
        List<Pair<Detail, Float>> detailList = new ArrayList<>();
        String selection = KEY_ACCOUNT_ID + "=? and " + KEY_TYPE + "=? and " + KEY_CREATE_DATE + " LIKE '" + createDate + "%'";
        String[] selectionArgs = new String[]{String.valueOf(accountId), String.valueOf(type)};
        Cursor cursor = mDb.rawQuery("select *, sum(" + KEY_AMOUNT + ") from " + DATABASE_TABLE_DETAIL + " where " + selection + " group by " + KEY_CLASSIFY_ID + " order by " + KEY_CREATE_DATE + " DESC", selectionArgs);
        while (cursor.moveToNext()) {
            detailList.add(new Pair<>(getDetail(cursor), cursor.getFloat(cursor.getColumnIndex("sum(" + KEY_AMOUNT + ")"))));
        }
        cursor.close();
        return detailList;
    }

    /**
     * According to the query in the bill
     *
     * @param accountId
     * @param year
     * @return
     */
    @SuppressLint("DefaultLocale")
    public Pair<Pair<Float, Float>, List<Bill>> queryBillsByTypeYear(int accountId, String year) {
        HashMap<String, Float> incomeMap = new HashMap<>();
        HashMap<String, Float> expenseMap = new HashMap<>();
        String selection = KEY_ACCOUNT_ID + "=? and " + KEY_TYPE + "=? and " + KEY_CREATE_DATE + " LIKE '" + year + "/%'";
        String[] selectionArgsIncome = new String[]{String.valueOf(accountId), String.valueOf(INCOME_TYPE)};
        Cursor cursorIncome = mDb.rawQuery("select " + KEY_CREATE_DATE + ", sum(" + KEY_AMOUNT + ") from " + DATABASE_TABLE_DETAIL + " where " + selection + " group by " + KEY_CREATE_DATE + " order by " + KEY_CREATE_DATE + " DESC", selectionArgsIncome);
        while (cursorIncome.moveToNext()) {
            incomeMap.put(cursorIncome.getString(cursorIncome.getColumnIndex(KEY_CREATE_DATE)), cursorIncome.getFloat(cursorIncome.getColumnIndex("sum(" + KEY_AMOUNT + ")")));
        }
        cursorIncome.close();

        String[] selectionArgsExpense = new String[]{String.valueOf(accountId), String.valueOf(EXPENSE_TYPE)};
        Cursor cursorExpense = mDb.rawQuery("select " + KEY_CREATE_DATE + ", sum(" + KEY_AMOUNT + ") from " + DATABASE_TABLE_DETAIL + " where " + selection + " group by " + KEY_CREATE_DATE + " order by " + KEY_CREATE_DATE + " DESC", selectionArgsExpense);
        while (cursorExpense.moveToNext()) {
            expenseMap.put(cursorExpense.getString(cursorExpense.getColumnIndex(KEY_CREATE_DATE)), cursorExpense.getFloat(cursorExpense.getColumnIndex("sum(" + KEY_AMOUNT + ")")));
        }
        cursorExpense.close();

        List<Bill> billList = new ArrayList<>();
        float incomeTotal = 0;
        float expenseTotal = 0;
        for (int i = 12; i > 0; i--) {
            String createDate = year + "/" + String.format("%02d", i);
            Bill bill = new Bill();
            bill.setCreateDate(createDate);
            Float incomeAmount = incomeMap.get(createDate);
            Float expenseAmount = expenseMap.get(createDate);
            incomeAmount = incomeAmount == null ? 0 : incomeAmount;
            expenseAmount = expenseAmount == null ? 0 : expenseAmount;
            incomeTotal += incomeAmount;
            expenseTotal += expenseAmount;
            bill.setIncomeAmount(incomeAmount);
            bill.setExpenseAmount(expenseAmount);
            bill.setBalance(incomeAmount - expenseAmount);
            billList.add(bill);
        }
        return new Pair<>(new Pair<>(incomeTotal, expenseTotal), billList);
    }

    /**
     * According to the classification and the creation date for detailed list
     *
     * @param type
     * @param createDate
     * @return
     */
    public Pair<Double, List<Detail>> queryDetailListByTypeDate(int accountId, int type, String createDate) {
        return queryDetailListByTypeDate(accountId, type, createDate, null);
    }

    /**
     * According to the classification of ids and the creation date for detailed list
     *
     * @param classifyId
     * @param createDate
     * @return
     */
    public List<Detail> queryDetailListByClassifyDate(int accountId, int classifyId, String createDate) {
        List<Detail> detailList = new ArrayList<>();
        Cursor cursor = mDb.query(DATABASE_TABLE_DETAIL, null, KEY_ACCOUNT_ID + "=? and " + KEY_CLASSIFY_ID + "=? and " + KEY_CREATE_DATE + "=?", new String[]{String.valueOf(accountId), String.valueOf(classifyId), createDate}, null, null, KEY_CREATE_TIME + " DESC", null);
        while (cursor.moveToNext()) {
            detailList.add(getDetail(cursor));
        }
        cursor.close();
        return detailList;
    }

    /**
     * According to the classification for detail list
     *
     * @param type
     * @param type
     * @return
     */
    public Pair<Double, List<Detail>> queryDetailListByType(int accountId, int type) {
        List<Detail> detailList = new ArrayList<>();
        double amountSum = 0;
        String selection = KEY_ACCOUNT_ID + "=? and " + KEY_TYPE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(accountId), String.valueOf(type)};
        Cursor cursor = mDb.query(DATABASE_TABLE_DETAIL, null, selection, selectionArgs, null, null, KEY_CREATE_TIME + " DESC", null);
        while (cursor.moveToNext()) {
            detailList.add(getDetail(cursor));
        }
        cursor = mDb.rawQuery("select sum(" + KEY_AMOUNT + ") from " + DATABASE_TABLE_DETAIL + " where " + selection, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                amountSum = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return new Pair<>(amountSum, detailList);
    }


    /**
     * Update the detail information
     *
     * @param detail
     * @return
     */
    public long updateDetailById(Detail detail) {
        long result = -1;
        ContentValues detailValue = new ContentValues();
        detailValue.put(KEY_CLASSIFY_ID, detail.getClassify_id());
        detailValue.put(KEY_AMOUNT, detail.getAmount());
        detailValue.put(KEY_COMMENT, detail.getComment());
        detailValue.put(KEY_CREATE_DATE, detail.getCreate_date());
        detailValue.put(KEY_UPDATE_TIME, detail.getUpdate_time());
        result = mDb.update(DATABASE_TABLE_DETAIL, detailValue, KEY_ID + " =?", new String[]{String.valueOf(detail.getId())});
        return result;
    }

    /**
     * Delete the detail information
     *
     * @param detailId
     * @return
     */
    public long deleteDetailById(int detailId) {
        return mDb.delete(DATABASE_TABLE_DETAIL, KEY_ID + " =?", new String[]{String.valueOf(detailId)});
    }


    private Detail getDetail(Cursor cursor) {
        Detail detail = new Detail();
        detail.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        int accountId = cursor.getInt(cursor.getColumnIndex(KEY_ACCOUNT_ID));
        detail.setAccount_id(accountId);
        detail.setAccount(queryAccountById(accountId));
        detail.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
        int classifyId = cursor.getInt(cursor.getColumnIndex(KEY_CLASSIFY_ID));
        detail.setClassify_id(classifyId);
        detail.setClassify(queryClassifyById(classifyId));
        detail.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)));
        detail.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
        detail.setCreate_date(cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE)));
        detail.setCreate_time(cursor.getLong(cursor.getColumnIndex(KEY_CREATE_TIME)));
        detail.setUpdate_time(cursor.getLong(cursor.getColumnIndex(KEY_UPDATE_TIME)));
        return detail;
    }

}
