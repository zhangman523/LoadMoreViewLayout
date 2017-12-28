package zhangman.github.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by zhangman on 2017/12/26 15:46.
 * Email: zhangman523@126.com
 */

public class LoadMoreDefaultFooterView extends RelativeLayout implements LoadMoreUIHandler {

  private TextView mTextView;

  public LoadMoreDefaultFooterView(Context context) {
    this(context, null);
  }

  public LoadMoreDefaultFooterView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoadMoreDefaultFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setupViews();
  }

  private void setupViews() {
    LayoutInflater.from(getContext()).inflate(R.layout.load_more_default_footer_view, this);
    mTextView = findViewById(R.id.load_more_default_footer_text_view);
  }

  @Override
  public void onPulling(float percent) {
    if (percent >= 1) {
      mTextView.setText(R.string.load_more_release_to_load);
    } else {
      mTextView.setText(R.string.load_more_pull_to_load);
    }
  }

  @Override
  public void onLoading(LoadMoreContainer container) {
    mTextView.setText(R.string.load_more_loading);
  }

  @Override
  public void onLoadFinish(final LoadMoreContainer container, boolean empty, boolean hasMore) {
    if (!hasMore) {
      if (empty) {
        mTextView.setText(R.string.load_more_loaded_empty);
      } else {
        mTextView.setText(R.string.load_more_loaded_no_more);
      }
    } else {
      mTextView.setText(R.string.load_more_success);
    }
    mTextView.postDelayed(new Runnable() {
      @Override
      public void run() {
        container.loadMoreFinish();
      }
    }, 300);
  }

  @Override
  public void onWaitToLoadMore(LoadMoreContainer container) {
    mTextView.setText(R.string.load_more_click_to_load_more);
  }

  @Override
  public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage) {
    mTextView.setText(R.string.load_more_error);
  }
}
