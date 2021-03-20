package cn.jj.base.baseclass;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import cn.jj.basiclib.R;

/**
 * Created by yangxl on 2017/5/31.
 */

public abstract class PageActivity extends BaseActivity {
    protected ViewPager viewPager;
    protected FragmentAdapter viewPagerAdapter;
    protected TabLayout tabLayout;
    protected FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.basic_page_activity_window_bg);
        initView();
    }

    @Override
    final protected int obtainLayoutResID() {
        return R.layout.common_page_activity_layout;
    }

    protected void setContentInsetStartWithNavigation(int value) {
        if (null != toolbar) {
            toolbar.setContentInsetStartWithNavigation(value);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        if (!canScrollToHeader() && null != toolbar) {
            AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            lp.setScrollFlags(0);
        }
        tabLayout = findViewById(R.id.tabs);

        List<String> titles = getFragmentTitles();
        if (titles.size() <= 1) {
            tabLayout.setVisibility(View.GONE);
            if (null != toolbar) {
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                lp.setScrollFlags(0);
            }
        } else {
            for (String title : titles) {
                tabLayout.addTab(tabLayout.newTab().setText(title));
            }
        }
        viewPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), titles);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    protected abstract Fragment getFragmentItem(int pos);

    protected abstract List<String> getFragmentTitles();

    protected boolean canScrollToHeader() {
        return true;
    }

    public void setFloatingActionClickListener(View.OnClickListener listener) {
        if (listener != null) {
            if (fab == null) {
                ViewStub floatingBtnStub = findViewById(R.id.floating_btn_stub);
                fab = (FloatingActionButton) floatingBtnStub.inflate();
            }
            fab.setOnClickListener(listener);
        }
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {
        private List<String> mTitles;

        public FragmentAdapter(FragmentManager fm, List<String> titles) {
            super(fm);
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentItem(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
}