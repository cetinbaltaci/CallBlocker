package com.cb.callblocker.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cb.callblocker.AboutFragment;
import com.cb.callblocker.SettingsFragment;
import com.cb.callblocker.LogsFragment;
import com.cb.callblocker.R;
import com.cb.callblocker.StatusFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3,R.string.tab_text_4};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null ;
        switch (position) {
            case 0:
                fragment = new StatusFragment() ;
                break ;
            case 1:
                fragment = new LogsFragment();
                break ;
            case 2:
                fragment = new SettingsFragment();
                break ;
            case 3:
                fragment = new AboutFragment();
                break ;
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 4;
    }
}