package zhangman.github.loadmoreviewlayout.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import zhangman.github.loadmore.LoadMoreContainer;
import zhangman.github.loadmore.LoadMoreHandler;
import zhangman.github.loadmore.LoadMoreViewContainer;
import zhangman.github.loadmoreviewlayout.R;

/**
 * Created by zhangman on 2017/12/27 13:53.
 * Email: zhangman523@126.com
 */

public class RecyclerViewFragment extends Fragment {
  private final static String RECYCLER_VIEW_FLAG = "RecyclerViewFlag";
  public final static int LINEAR_LAYOUT_RECYCLER = 0;
  public final static int GRID_LAYOUT_RECYCLER = 1;

  private PtrClassicFrameLayout mPtrClassicFrameLayout;
  private LoadMoreViewContainer mLoadMoreViewContainer;
  private RecyclerView mRecyclerView;

  public static RecyclerViewFragment getInstance(int flag) {
    RecyclerViewFragment fragment = new RecyclerViewFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(RECYCLER_VIEW_FLAG, flag);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_recycler_view, container, false);
    mPtrClassicFrameLayout = root.findViewById(R.id.frame_layout);
    mLoadMoreViewContainer = root.findViewById(R.id.load_more_container);
    mRecyclerView = root.findViewById(R.id.recycler_view);
    return root;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mPtrClassicFrameLayout.setPtrHandler(new PtrHandler() {
      @Override
      public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
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
    mLoadMoreViewContainer.loadMoreFinish(false, true);
    mLoadMoreViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
      @Override
      public void onLoadMore(LoadMoreContainer loadMoreContainer) {
        mLoadMoreViewContainer.postDelayed(new Runnable() {
          @Override
          public void run() {
            mLoadMoreViewContainer.loadMoreFinish(false, true);
          }
        }, 1000);
      }
    });
    RecyclerView.LayoutManager layoutManager;
    if (getArguments().getInt(RECYCLER_VIEW_FLAG) == LINEAR_LAYOUT_RECYCLER) {
      layoutManager = new LinearLayoutManager(getActivity());
    } else {
      layoutManager = new GridLayoutManager(getActivity(), 2);
      mRecyclerView.addItemDecoration(new SpaceItemDecoration(2));
    }
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerView.setAdapter(new RecyclerViewAdapter());
  }

  public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
      this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
        RecyclerView.State state) {
      outRect.left = space;
      outRect.bottom = space;
      if (parent.getChildLayoutPosition(view) % 2 == 0) {
        outRect.left = 0;
      }
    }
  }
}
