package com.tangtang.webviewcache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.tangtang.webviewcache.bean.Resource;
import com.tangtang.webviewcache.downlod.DownloadResourcesManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Author:
 * Version    V1.0
 * Date:      2019/4/30
 * Description:WebView帮助类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/5/15                  1.0                    1.0
 * Why & What is modified:
 */

public class WebViewUtils {
  private static final String TAG = WebViewUtils.class.getSimpleName();

  @SuppressLint("StaticFieldLeak")
  private final Context context;

  private List<Resource> resourceList;
  private WebView loadWebView;
  private String url;
  //下载状态是否已经ready
  private boolean isReady;
  private boolean isNeedRefresh = true;
  private DownloadResourcesManager downloadResourcesManager;

  public WebViewUtils(WebView webView, Context context){
    loadWebView = webView;
    this.context = context;
    initWebView();
    initDownloadsManager();
  }

  public void initWebView() {
    WebSettings mWebSettings = loadWebView.getSettings();
    mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
    mWebSettings.setSupportZoom(true);
    mWebSettings.setLoadWithOverviewMode(true);
    mWebSettings.setUseWideViewPort(true);
    mWebSettings.setJavaScriptEnabled(true);
    mWebSettings.setDefaultTextEncodingName("GBK");
    mWebSettings.setSupportMultipleWindows(false);
    mWebSettings.setLoadsImagesAutomatically(true);
    mWebSettings.setAppCacheEnabled(true);
//    mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//    mWebSettings.setDomStorageEnabled(true);
//    mWebSettings.setDatabaseEnabled(true);
    //对数据进行512mb缓存，防止本地加载失败
//    String cacheDirPath = Contacts.APP_CACHE_PATH;
//    mWebSettings.setDatabasePath(cacheDirPath);
//    mWebSettings.setAppCachePath(cacheDirPath);
//    mWebSettings.setAppCacheMaxSize(512 * 1024 * 1024);
    /*webView.addJavascriptInterface(new WebAppInterface(context.getApplicationContext()), "AndroidJsInterface");*/
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mWebSettings.setAllowUniversalAccessFromFileURLs(true);
      mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mWebSettings.setMediaPlaybackRequiresUserGesture(false);
    }
    mWebSettings.setAllowFileAccess(true);
    mWebSettings.setDisplayZoomControls(false);
    loadWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "onPageFinished");
        asyncDownloadResource();
        isReady = true;
      }

      /**
       * 防止加载网页时调起系统浏览器
       */
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http")) {
          view.loadUrl(url);
        }
        return true;
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
      }

      @Nullable
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        collectResource(url);
        return super.shouldInterceptRequest(view, url);
      }

      @Nullable
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          String url = request.getUrl().toString();
          Log.d(TAG, "shouldInterceptRequest url -->" + url);
          collectResource(url);
        }
        return super.shouldInterceptRequest(view, request);
      }

      @Override
      public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        if (sslError.getPrimaryError() == SslError.SSL_DATE_INVALID
            || sslError.getPrimaryError() == SslError.SSL_EXPIRED
            || sslError.getPrimaryError() == SslError.SSL_INVALID
            || sslError.getPrimaryError() == SslError.SSL_UNTRUSTED) {
          sslErrorHandler.proceed();
        } else {
          sslErrorHandler.cancel();
        }
        sslErrorHandler.proceed();
      }

    });
  }

  public void loadUrl(String url){
    this.url = url;
    if (resourceList != null && !resourceList.isEmpty()){
      resourceList.clear();
    }
    load(url);
  }

  private void load(String url){
    loadWebView.loadDataWithBaseURL("file:///android_asset/", url, "text/html", "utf-8", null);
  }

  private void collectResource(String url){
    if (resourceList == null){
      resourceList = new ArrayList<>();
    }
    Log.d(TAG, "collectResource url 1 -->" + url);
    if (!TextUtils.isEmpty(url) && url.startsWith("http") && (FileUtils.isVideo(url) || FileUtils.isPicture(url))){
      Log.d(TAG, "collectResource url 2 -->" + url);
      if (!FileUtils.isContainsUrl(resourceList, url)){
        String name = FileUtils.getFileName(url);
        String localPicUrl = FileUtils.getLocalPath(name);
        if (!FileUtils.isFileExist(localPicUrl)){
          isNeedRefresh = false;
        }
        Resource resource = new Resource();
        resource.setUrl(url);
        String local = Contacts.APP_ROOT_PATH;
        FileUtils.isFolderExists(local);
        resource.setLocal(local + name);
        Log.d(TAG, "url -->" + url);
        resourceList.add(resource);
      }
    }
  }

  private void asyncDownloadResource(){
    if (resourceList != null && !resourceList.isEmpty() && !isReady){
      Log.d(TAG, "asyncDownloadResource");
      //开始下载
      downloadResourcesManager.downloadResources(resourceList, (completed, failed, total, u, status) -> {
        Log.d(TAG, "asyncDownloadResource url -->" + u);
        if (completed >= total){
          //下载完成,替换之前的url
          for (Resource source : resourceList) {
            String name = FileUtils.getFileName(source.getUrl());
            String localPicUrl = FileUtils.getLocalPath(name);
            Log.d(TAG, "asyncDownloadResource localPicUrl -->" + localPicUrl + " url:" + source.getUrl());
            url = url.replace(source.getUrl(), localPicUrl);
          }
          //刷新
          if (isNeedRefresh){
            load(url);
          }
        }
      });
    }
  }

  private void initDownloadsManager(){
    downloadResourcesManager = new DownloadResourcesManager
            .Builder()
            .setContext(context)
            .setDownloadType(DownloadResourcesManager.DOWNLOAD_TYPE_MULTI_PARALLEL)
            .build();
  }

  public void release(){
    downloadResourcesManager.onDestroy();
  }

}
