package zhangman.github.loadmoreviewlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import zhangman.github.loadmoreviewlayout.fragment.FrameLayoutFragment;
import zhangman.github.loadmoreviewlayout.fragment.GridViewFragment;
import zhangman.github.loadmoreviewlayout.fragment.ImageViewFragment;
import zhangman.github.loadmoreviewlayout.fragment.LinearLayoutFragment;
import zhangman.github.loadmoreviewlayout.fragment.ListViewFragment;
import zhangman.github.loadmoreviewlayout.fragment.RecyclerViewFragment;
import zhangman.github.loadmoreviewlayout.fragment.RelativeLayoutFragment;
import zhangman.github.loadmoreviewlayout.fragment.ScrollViewFragment;
import zhangman.github.loadmoreviewlayout.fragment.TextViewFragment;
import zhangman.github.loadmoreviewlayout.fragment.WebViewFragment;

/**
 * Created by zhangman on 2017/12/26 16:33.
 * Email: zhangman523@126.com
 */

public class MainActivity extends AppCompatActivity {
  private TabLayout mTabLayout;
  private ViewPager mViewPager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mTabLayout = findViewById(R.id.tabs);
    mViewPager = findViewById(R.id.container);
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    mViewPager.setAdapter(adapter);
    mViewPager.setOffscreenPageLimit(11);
    mTabLayout.setupWithViewPager(mViewPager);
    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
  }

  private class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTitles;

    public ViewPagerAdapter(FragmentManager fm) {
      super(fm);
      mFragments = new ArrayList<>();
      mFragments.add(new ListViewFragment());
      mFragments.add(new GridViewFragment());
      mFragments.add(RecyclerViewFragment.getInstance(RecyclerViewFragment.LINEAR_LAYOUT_RECYCLER));
      mFragments.add(RecyclerViewFragment.getInstance(RecyclerViewFragment.GRID_LAYOUT_RECYCLER));
      mFragments.add(new ScrollViewFragment());
      mFragments.add(new WebViewFragment());
      mFragments.add(new FrameLayoutFragment());
      mFragments.add(new RelativeLayoutFragment());
      mFragments.add(new LinearLayoutFragment());
      mFragments.add(new ImageViewFragment());
      mFragments.add(new TextViewFragment());

      mTitles = new ArrayList<>();
      mTitles.add("ListView");
      mTitles.add("GridView");
      mTitles.add("RecyclerView");
      mTitles.add("Grid RecyclerView");
      mTitles.add("ScrollView");
      mTitles.add("WebView");
      mTitles.add("FrameLayout");
      mTitles.add("RelativeLayout");
      mTitles.add("LinearLayout");
      mTitles.add("ImageView");
      mTitles.add("TextView");
    }

    @Override
    public Fragment getItem(int position) {
      return mFragments.get(position);
    }

    @Override
    public int getCount() {
      return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return mTitles.get(position);
    }
  }
}
