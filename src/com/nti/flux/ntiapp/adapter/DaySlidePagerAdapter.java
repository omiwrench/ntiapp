package com.nti.flux.ntiapp.adapter;

import java.util.List;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.fragment.ScheduleDayFragment;
import com.nti.flux.ntiapp.model.Day;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class DaySlidePagerAdapter extends FragmentStatePagerAdapter {
	private List<Day> days;
    public DaySlidePagerAdapter(Context context, FragmentManager fm, List<Day> days) {
        super(fm);
        this.days = days;
    }

    @Override
    public Fragment getItem(int position){
        return ScheduleDayFragment.newInstance(days.get(position), position);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
