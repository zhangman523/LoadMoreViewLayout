package zhangman.github.loadmoreviewlayout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import zhangman.github.loadmore.LoadMoreContainer;
import zhangman.github.loadmore.LoadMoreHandler;
import zhangman.github.loadmore.LoadMoreViewContainer;
import zhangman.github.loadmoreviewlayout.R;

/**
 * Created by zhangman on 2017/12/27 13:56.
 * Email: zhangman523@126.com
 */

public class WebViewFragment extends Fragment {
  private PtrClassicFrameLayout mPtrClassicFrameLayout;
  private LoadMoreViewContainer mLoadMoreViewContainer;
  private WebView mWebView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_web_view, container, false);
    mPtrClassicFrameLayout = root.findViewById(R.id.frame_layout);
    mLoadMoreViewContainer = root.findViewById(R.id.load_more_container);
    mWebView = root.findViewById(R.id.web_view);
    return root;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mPtrClassicFrameLayout.refreshComplete();
        mLoadMoreViewContainer.loadMoreFinish(false, true);
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
        super.onReceivedError(view, request, error);
        mPtrClassicFrameLayout.refreshComplete();
        mLoadMoreViewContainer.loadMoreFinish(false, true);
      }

      @Override
      public void onReceivedHttpError(WebView view, WebResourceRequest request,
          WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        mPtrClassicFrameLayout.refreshComplete();
        mLoadMoreViewContainer.loadMoreFinish(false, true);
      }
    });
    mWebView.loadUrl("https://www.baidu.com");
    mPtrClassicFrameLayout.setPtrHandler(new PtrHandler() {
      @Override
      public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mWebView, header);
      }

      @Override
      public void onRefreshBegin(final PtrFrameLayout frame) {
        mWebView.reload();
        //frame.postDelayed(new Runnable() {
        //  @Override
        //  public void run() {
        //    frame.refreshComplete();
        //  }
        //}, 1000);
      }
    });
    mLoadMoreViewContainer.useDefaultFooter();
    mLoadMoreViewContainer.loadMoreFinish(false, true);
    mLoadMoreViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
      @Override
      public void onLoadMore(final LoadMoreContainer loadMoreContainer) {
        mWebView.reload();
        //mLoadMoreViewContainer.postDelayed(new Runnable() {
        //  @Override
        //  public void run() {
        //    loadMoreContainer.loadMoreFinish(false, true);
        //  }
        //}, 1000);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mWebView != null) {
      mWebView.onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mWebView != null) {
      mWebView.onPause();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mWebView != null) {
      mWebView.destroy();
    }
  }
}