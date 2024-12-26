package com.stkj.common.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentUtils {
    
    public final static String TAG = "FragmentUtils";

    public static void safeAddFragment(FragmentManager fragmentManager, Fragment fragment, @NonNull String tag) {
        try {
            fragmentManager.beginTransaction()
                    .add(fragment, tag)
                    .commitNowAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void safeAddFragment(FragmentManager fragmentManager, Fragment fragment, int containViewId) {
        try {
            fragmentManager.beginTransaction()
                    .add(containViewId, fragment)
                    .commitNowAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void safeReplaceFragment(FragmentManager fragmentManager, Fragment fragment, int containViewId, @NonNull String tag) {
        try {
            fragmentManager.beginTransaction()
                    .replace(containViewId, fragment, tag)
                    .commitNowAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void safeReplaceFragment(FragmentManager fragmentManager, Fragment fragment, int containViewId) {
        try {
            Log.d(TAG, "lime==== settings: 45");
            fragmentManager.beginTransaction()
                    .replace(containViewId, fragment)
                    .commitNowAllowingStateLoss();

        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "lime==== settings: 51 " + e.getMessage());
        }
    }

    public static void safeRemoveFragment(FragmentManager fragmentManager, Fragment fragment) {
        try {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void safeAttachFragment(FragmentManager fragmentManager, Fragment fragment) {
        try {
            fragmentManager.beginTransaction()
                    .attach(fragment)
                    .commitNowAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void safeDetachFragment(FragmentManager fragmentManager, Fragment fragment) {
        try {
            fragmentManager.beginTransaction()
                    .detach(fragment)
                    .commitNowAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
