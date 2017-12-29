package zhangman.github.loadmoreviewlayout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import zhangman.github.loadmore.LoadMoreContainer;
import zhangman.github.loadmore.LoadMoreHandler;
import zhangman.github.loadmore.LoadMoreViewContainer;
import zhangman.github.loadmoreviewlayout.R;

/**
 * Created by zhangman on 2017/12/27 13:57.
 * Email: zhangman523@126.com
 */

public class FrameLayoutFragment extends Fragment {

  private PtrClassicFrameLayout mPtrClassicFrameLayout;
  private LoadMoreViewContainer mLoadMoreViewContainer;
  private FrameLayout mFrameLayout;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_frame_layout, container, false);
    mPtrClassicFrameLayout = root.findViewById(R.id.frame_layout);
    mLoadMoreViewContainer = root.findViewById(R.id.load_more_container);
    mFrameLayout = root.findViewById(R.id.frameLayout);
    return root;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mPtrClassicFrameLayout.setPtrHandler(new PtrHandler() {
      @Override
      public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mFrameLayout, header);
      }

      @Override
      public void onRefreshBegin(final PtrFrameLayout frame) {
        frame.postDelayed(new Runnable() {
          @Override
          public void run() {
            frame.refreshComplete();
          }
        }, 1000);
      }
    });
    mLoadMoreViewContainer.useDefaultFooter();
    //加载数据成功后调用
    mLoadMoreViewContainer.loadMoreFinish(false, true);
    mLoadMoreViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
      @Override
      public void onLoadMore(final LoadMoreContainer loadMoreContainer) {
        mLoadMoreViewContainer.postDelayed(new Runnable() {
          @Override
          public void run() {
            loadMoreContainer.loadMoreFinish(false, true);
          }
        }, 1000);
      }
    });
  }
}