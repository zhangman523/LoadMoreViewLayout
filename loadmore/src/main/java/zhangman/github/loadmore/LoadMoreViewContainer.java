package zhangman.github.loadmore;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

/**
 * Created by zhangman on 2017/12/26 13:33.
 * Email: zhangman523@126.com
 */

public class LoadMoreViewContainer extends ViewGroup implements LoadMoreContainer {
  private static final String TAG = LoadMoreViewContainer.class.getSimpleName();
  public static boolean DEBUG = false;

  private int mDurationToClose = 300;

  private LoadMoreUIHandler mLoadMoreUIHandler;
  private LoadMoreHandler mLoadMoreHandler;

  private View mFooterView;
  private View mContent;

  private boolean mIsLoading;
  private boolean mHasMore = false;
  private boolean mAutoLoadMore = true;

  private boolean mListEmpty = true;

  private float mTouchX, mTouchY;
  private float mOffsetX, mOffsetY;
  private float mResistance = 1.7f;//阻尼系数

  private boolean mIsBeingDragged;
  private int mFooterHeight;
  private int mTouchSlop;
  private ScrollChecker mScrollChecker;

  public LoadMoreViewContainer(Context context) {
    this(context, null);
  }

  public LoadMoreViewContainer(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoadMoreViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray array =
        context.obtainStyledAttributes(attrs, R.styleable.LoadMoreViewContainer, 0, 0);
    if (array != null) {
      mDurationToClose = array.getInt(R.styleable.LoadMoreViewContainer_load_more_duration_to_close,
          mDurationToClose);
      mResistance =
          array.getFloat(R.styleable.LoadMoreViewContainer_load_more_auto_load_more, mResistance);
      mAutoLoadMore = array.getBoolean(R.styleable.LoadMoreViewContainer_load_more_auto_load_more,
          mAutoLoadMore);
    }
    mScrollChecker = new ScrollChecker();
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  @Override
  protected void onFinishInflate() {
    final int childCount = getChildCount();
    if (childCount == 2) {
      View child1 = getChildAt(0);
      View child2 = getChildAt(1);

      if (child1 instanceof LoadMoreUIHandler) {
        mFooterView = child1;
        mContent = child2;
      } else if (child2 instanceof LoadMoreUIHandler) {
        mFooterView = child2;
        mContent = child1;
      } else {
        if (mContent == null && mFooterView == null) {
          mContent = child1;
          mFooterView = child2;
        } else {
          if (mFooterView == null) {
            mFooterView = mContent == child1 ? child2 : child1;
          } else {
            mContent = mFooterView == child1 ? child2 : child1;
          }
        }
      }
    } else if (childCount == 1) {
      mContent = getChildAt(0);
    }
    if (mFooterView != null) {
      mFooterView.bringToFront();
    }
    super.onFinishInflate();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mScrollChecker != null) {
      mScrollChecker.destroy();
    }
  }

  public void useDefaultFooter() {
    LoadMoreDefaultFooterView footerView = new LoadMoreDefaultFooterView(getContext());
    setLoadMoreView(footerView);
    setLoadMoreUIHandler(footerView);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (mContent != null) {
      final MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
      final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
          getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
      final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
          getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);
      mContent.measure(childWidthMeasureSpec, childHeightMeasureSpec);
      if (DEBUG) {
        Log.d(TAG, " onMeasure contentHeight:" + mContent.getMeasuredHeight());
      }
    }
    if (mFooterView != null) {
      measureChildWithMargins(mFooterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
      MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
      mFooterHeight = mFooterView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
      if (DEBUG) {
        Log.d(TAG, "onMeasure footerHeight:" + mFooterHeight);
      }
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (mContent != null) {
      MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
      int left = getPaddingLeft() + lp.leftMargin;
      int top = getPaddingTop() + lp.topMargin;
      int right = left + mContent.getMeasuredWidth();
      int bottom = top + mContent.getMeasuredHeight();
      mContent.layout(left, top, right, bottom);
      if (DEBUG) {
        Log.d(TAG, "onLayout contentView: left "
            + left
            + " top "
            + top
            + " right "
            + right
            + " bottom "
            + bottom);
      }
    }
    if (mFooterView != null) {
      MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
      int left = getPaddingLeft() + lp.leftMargin;
      int bottom = getMeasuredHeight() - getPaddingBottom() - lp.bottomMargin + mFooterHeight;
      int top = bottom - mFooterHeight;
      int right = left + mFooterView.getMeasuredWidth();
      mFooterView.layout(left, top, right, bottom);
      if (DEBUG) {
        Log.d(TAG, "onLayout footerView: left "
            + left
            + " top "
            + top
            + " right "
            + right
            + " bottom "
            + bottom);
      }
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (!isEnabled() || mIsLoading || !mHasMore) {
      return false;
    }
    final int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mTouchX = ev.getX();
        mTouchY = ev.getY();
        mIsBeingDragged = false;
        mScrollChecker.abortIfWorking();
        break;
      case MotionEvent.ACTION_MOVE:
        mOffsetX = ev.getX() - mTouchX;
        mOffsetY = ev.getY() - mTouchY;
        if (DEBUG) {
          Log.d(TAG, "onInterceptTouchEvent offsetY:"
              + mOffsetY
              + " canChildScrollDown: "
              + canChildScrollDown());
        }
        if (mOffsetY < 0 && !canChildScrollDown() && Math.abs(mOffsetY) > mTouchSlop) {
          mIsBeingDragged = true;
          return true;
        }
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        mIsBeingDragged = false;
        break;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!mIsBeingDragged) {
      return super.onTouchEvent(event);
    }
    final int action = event.getAction();
    switch (action) {
      case MotionEvent.ACTION_MOVE:
        float offsetY = (event.getY() - mTouchY) / mResistance;
        if (DEBUG) {
          Log.d(TAG, "onTouchEvent offsetY:"
              + offsetY
              + " y:"
              + mContent.getY()
              + " top:"
              + mContent.getTop());
        }
        movePos(offsetY);
        mTouchX = event.getX();
        mTouchY = event.getY();
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if (Math.abs(mContent.getTop()) >= mFooterHeight) {
          onReachBottom();
        } else {
          tryScrollBackToBottom();
        }
        break;
    }
    return true;
  }

  private void tryScrollBackToBottom() {
    if (DEBUG) {
      Log.d(TAG,
          " tryScrollBackToBottom content y:" + mContent.getY() + " top:" + mContent.getTop());
    }
    if (mContent.getTop() < 0) {
      mScrollChecker.tryToScrollTo((int) mTouchY, (int) (mTouchY + Math.abs(mContent.getTop())),
          mDurationToClose);
    }
  }

  private void movePos(float deltaY) {
    //over top
    if (deltaY > 0 && Math.abs(deltaY) > Math.abs(mContent.getTop())) {
      if (DEBUG) {
        Log.d(TAG, "movePos : deltaY:" + deltaY + " content top:" + mContent.getTop());
      }
      deltaY = Math.abs(mContent.getTop());
    }
    if (mContent != null) {
      mContent.offsetTopAndBottom((int) deltaY);
    }
    if (mFooterView != null) {
      mFooterView.offsetTopAndBottom((int) deltaY);
    }
    if (mLoadMoreUIHandler != null) {
      mLoadMoreUIHandler.onPulling(Math.abs(mContent.getTop()) / mFooterHeight);
    }
  }

  private void onReachBottom() {
    mScrollChecker.tryToScrollTo((int) mTouchY,
        (int) mTouchY + Math.abs(mContent.getTop()) - mFooterHeight, mDurationToClose);
    if (mAutoLoadMore) {
      tryToPerformLoadMore();
    } else {
      if (mHasMore) {
        mLoadMoreUIHandler.onWaitToLoadMore(this);
      }
    }
  }

  private void tryToPerformLoadMore() {
    if (mIsLoading) {
      return;
    }

    // no more content and also not load for first page
    if (!mHasMore && !mListEmpty) {
      return;
    }

    mIsLoading = true;

    if (mLoadMoreUIHandler != null) {
      mLoadMoreUIHandler.onLoading(this);
    }
    if (null != mLoadMoreHandler) {
      mLoadMoreHandler.onLoadMore(this);
    }
  }

  /**
   * Whether it is possible for the child view of this layout to
   * scroll down. Override this if the child view is a custom view.
   */
  protected boolean canChildScrollDown() {
    if (android.os.Build.VERSION.SDK_INT < 14) {
      if (mContent instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) mContent;
        return absListView.getChildCount() > 0 && (absListView.getLastVisiblePosition()
            < absListView.getChildCount() - 1
            || absListView.getChildAt(absListView.getChildCount() - 1).getBottom()
            > absListView.getPaddingBottom());
      } else {
        return ViewCompat.canScrollVertically(mContent, 1) || mContent.getScrollY() < 0;
      }
    } else {
      return ViewCompat.canScrollVertically(mContent, 1);
    }
  }

  @Override
  public void setAutoLoadMore(boolean autoLoadMore) {
    mAutoLoadMore = autoLoadMore;
  }

  public void setResistance(float resistance) {
    mResistance = resistance;
  }

  public void setDurationToClose(int DURATION_TO_CLOSE) {
    this.mDurationToClose = DURATION_TO_CLOSE;
  }

  @Override
  public void setLoadMoreView(View footer) {
    if (mFooterView != null && footer != null && mFooterView != footer) {
      removeView(mFooterView);
    }
    ViewGroup.LayoutParams lp = footer.getLayoutParams();
    if (lp == null) {
      lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      footer.setLayoutParams(lp);
    }
    mFooterView = footer;
    mFooterView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        tryToPerformLoadMore();
      }
    });
    addView(footer);
  }

  @Override
  public void setLoadMoreUIHandler(LoadMoreUIHandler uiHandler) {
    mLoadMoreUIHandler = uiHandler;
  }

  @Override
  public void setLoadMoreHandler(LoadMoreHandler loadMoreHandler) {
    mLoadMoreHandler = loadMoreHandler;
  }

  @Override
  public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
    mListEmpty = emptyResult;
    mIsLoading = false;
    mHasMore = hasMore;
    if (mLoadMoreUIHandler != null) {
      mLoadMoreUIHandler.onLoadFinish(this, emptyResult, hasMore);
    }
  }

  @Override
  public void loadMoreError(int errorCode, String errorMessage) {
    mIsLoading = false;
    if (mLoadMoreUIHandler != null) {
      mLoadMoreUIHandler.onLoadError(this, errorCode, errorMessage);
    }
  }

  @Override
  public void loadMoreFinish() {
    tryScrollBackToBottom();
  }

  @Override
  protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
  }

  @Override
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  @Override
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  public static class LayoutParams extends MarginLayoutParams {

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    @SuppressWarnings({ "unused" })
    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }

  class ScrollChecker implements Runnable {
    private int mLastFlingY;
    private Scroller mScroller;
    private boolean mIsRunning = false;
    private int mStart;
    private int mTo;

    public ScrollChecker() {
      mScroller = new Scroller(getContext());
    }

    @Override
    public void run() {
      boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
      int curY = mScroller.getCurrY();
      int deltaY = curY - mLastFlingY;
      if (DEBUG) {
        //if (deltaY != 0) {
        //  Log.v(TAG, "scroll: "
        //      + finish
        //      + ", start: "
        //      + mStart
        //      + ", to: "
        //      + mTo
        //      + ", currentPos: "
        //      + 0
        //      + ", "
        //      + "current"
        //      + curY
        //      + "last:"
        //      + mLastFlingY
        //      + "delta: "
        //      + deltaY);
        //}
      }
      if (!finish && mContent != null && mContent.getTop() != 0) {
        mLastFlingY = curY;
        if (mFooterView != null) {
          mFooterView.offsetTopAndBottom(deltaY);
        }
        mContent.offsetTopAndBottom(deltaY);
        post(this);
      } else {
        finish();
      }
    }

    private void finish() {
      if (DEBUG) {
        Log.v(TAG, "finish, currentPos:"
            + mTouchY
            + " content y:"
            + mContent.getY()
            + " top:"
            + mContent.getTop());
      }
      reset();
    }

    private void reset() {
      mIsRunning = false;
      mLastFlingY = 0;
      removeCallbacks(this);
    }

    private void destroy() {
      reset();
      if (!mScroller.isFinished()) {
        mScroller.forceFinished(true);
      }
    }

    public void abortIfWorking() {
      if (mIsRunning) {
        if (!mScroller.isFinished()) {
          mScroller.forceFinished(true);
        }
        reset();
      }
    }

    public void tryToScrollTo(int start, int to, int duration) {
      if (mContent.getTop() == 0) {
        return;
      }
      mStart = start;
      mTo = to;
      int distance = to - mStart;
      if (DEBUG) {
        Log.d(TAG, "tryToScrollTo: start: " + mStart + ", distance:" + distance + ", to:" + to);
      }
      removeCallbacks(this);

      mLastFlingY = 0;

      if (!mScroller.isFinished()) {
        mScroller.forceFinished(true);
      }
      mScroller.startScroll(0, 0, 0, distance, duration);
      post(this);
      mIsRunning = true;
    }
  }
}
