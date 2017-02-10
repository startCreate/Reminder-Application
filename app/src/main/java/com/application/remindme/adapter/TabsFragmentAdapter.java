package com.application.remindme.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.application.remindme.dto.RemindDTO;
import com.application.remindme.fragment.AbstractTabFragment;
import com.application.remindme.fragment.TodoFragment;
import com.application.remindme.fragment.CalendarView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vv_voronov on 19.01.2017.
 */

public class TabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    private List<RemindDTO> data;

    private TodoFragment todoFragment;

    public TabsFragmentAdapter(Context context, FragmentManager fm, List<RemindDTO> data) {
        super(fm);
        this.data = data;
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        todoFragment = TodoFragment.getInstance(context, data);
        tabs.put(0, todoFragment);
        tabs.put(1, CalendarView.getInstance(context));

    }

    public void setData(List<RemindDTO> data) {
        this.data = data;
        todoFragment.refreshList(data);
    }
}