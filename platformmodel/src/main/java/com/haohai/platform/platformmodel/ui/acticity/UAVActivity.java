package com.haohai.platform.platformmodel.ui.acticity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.fragment.VideoUAVFragment;
import com.ruyiruyi.rylibrary.base.BaseFragmentActivity;
import com.ruyiruyi.rylibrary.cell.HomeTabsCell;
import com.ruyiruyi.rylibrary.cell.NoCanSlideViewPager;
import com.ruyiruyi.rylibrary.ui.adapter.FragmentViewPagerAdapter;
import com.ruyiruyi.rylibrary.utils.AndroidUtilities;
import com.ruyiruyi.rylibrary.utils.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

public class UAVActivity extends BaseFragmentActivity {
    private static final String TAG = UAVActivity.class.getSimpleName();
    private FrameLayout content;
    private NoCanSlideViewPager viewPager;
    private HomeTabsCell tabsCell;
    private List<String> titles;
    private HomePagerAdapeter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕不息屏
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//点亮屏幕
        super.onCreate(savedInstanceState);


        content = new FrameLayout(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));


        viewPager = new NoCanSlideViewPager(this);

        tabsCell = new HomeTabsCell(this);
        content.addView(tabsCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, /*AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT)*/0, Gravity.BOTTOM));
        tabsCell.setViewPager(viewPager);

        content.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, /*AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT)*/0));

        initTitle();
        pagerAdapter = new HomePagerAdapeter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置不能左右滑动
        viewPager.setNoScroll(true);
        //设置不显示滑动动画
        viewPager.setScrollAnim(false);
        viewPager.setCurrentItem(0);
        tabsCell.setSelected(0);
    }

    class HomePagerAdapeter extends FragmentViewPagerAdapter {

        private final List<String> mPageTitle = new ArrayList<>();

        public HomePagerAdapeter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
            super(fragmentManager, fragments);
            mPageTitle.clear();
            mPageTitle.addAll(pageTitles);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitle.get(position);
        }
    }

    private void initTitle() {
        tabsCell.addView(R.drawable.ic_jiankong, R.drawable.ic_jiankong_selected, "无人机");

    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new VideoUAVFragment());
        return fragments;
    }

    protected List<String> initPagerTitle() {
        titles = new ArrayList<>();
        titles.add("无人机");
        return titles;
    }
}
