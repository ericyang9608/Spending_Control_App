package com.hyy.accountassis.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();

    protected AppCompatActivity activity;
    protected Context context;
    protected ExecutorService e1;
    protected OnFragmentCallback onFragmentCallback;

    public void setOnFragmentCallback(OnFragmentCallback onFragmentCallback) {
        this.onFragmentCallback = onFragmentCallback;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (AppCompatActivity) getActivity();
        e1 = Executors.newSingleThreadExecutor();
    }

    public void refreshData(int type) {

    }

    public void refreshDataByDate(int type, String createDate) {

    }

    public interface OnFragmentCallback {
        void onCallback(int type);
    }
}
