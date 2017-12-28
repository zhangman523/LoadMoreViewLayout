package zhangman.github.loadmore;

import android.view.View;

/**
 * Created by zhangman on 2017/12/26 11:38.
 * Email: zhangman523@126.com
 */

public interface LoadMoreContainer {

  void setAutoLoadMore(boolean autoLoadMore);

  void setLoadMoreView(View view);

  void setLoadMoreUIHandler(LoadMoreUIHandler uiHandler);

  void setLoadMoreHandler(LoadMoreHandler loadMoreHandler);

  void loadMoreFinish(boolean emptyResult,boolean hasMore);

  void loadMoreError(int errorCode,String errorMessage);

  /**
   * try to scroll back bottom
   */
  void loadMoreFinish();
}
