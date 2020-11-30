package com.hyy.accountassis.app;

import android.app.Application;
import android.content.Context;

import com.hyy.accountassis.db.DbHelper;
import com.hyy.accountassis.util.SPTool;

import java.util.HashMap;


/**
 *
 */

public class MyApp extends Application {

    private Context mContext;

    private static MyApp mMyApp;
    private HashMap<String, Integer> mDetailTypeResMap;

    /**
     * application A single global
     *
     * @return
     */
    public static MyApp getInstance() {
        return mMyApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApp = this;
        mContext = getApplicationContext();
        SPTool.getInstance().init(mContext);
        DbHelper.getInstance().init(mContext);
        mDetailTypeResMap = new HashMap<>();
    }

    public Context getContext() {
        return mContext;
    }

    public HashMap<String, Integer> getDetailTypeResMap() {
        return mDetailTypeResMap;
    }
}
