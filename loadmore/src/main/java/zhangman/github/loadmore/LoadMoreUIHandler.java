package zhangman.github.loadmore;

/**
 * Created by zhangman on 2017/12/26 11:42.
 * Email: zhangman523@126.com
 */

public interface LoadMoreUIHandler {
  void onPulling(float percent);

  void onLoading(LoadMoreContainer container);

  void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore);

  void onWaitToLoadMore(LoadMoreContainer container);

  void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage);
}
