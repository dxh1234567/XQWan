package com.jj.base.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressWebView extends WebView {

    public static final int UPDATEPROGRESS = 0;
    private ProgressBar progressbar;
    private ProgressWebView mWebView;
    private final int TIMEOUT = 10000;
    private final int TIMEOUT_ERROR = 9527;
    protected Timer mTimer;
    protected TimerTask mTimerTask;
    public static final String FAILHTML = "file:///android_asset/webfail.html";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == TIMEOUT_ERROR) {
                //  mWebView.stopLoading();

            } else if (msg.what == UPDATEPROGRESS) {
                newProgress = (Integer) msg.obj;
                if (progressbar.isShown()) {
                    if (newProgress >= 100) {
                        progressbar.setProgress(0);
                        progressbar.setVisibility(View.GONE);
                        //	  mWebView.stopLoading();
                    } else {
                        progressbar.setProgress(newProgress);
                    }
                }

            }
        }

    };
    protected int newProgress = 0;

    public Handler getmHandler() {
        return mHandler;
    }

    public void startLoadAnimication() {
        final long oldtime = System.currentTimeMillis();
        newProgress = 0;
        progressbar.setVisibility(View.VISIBLE);
        new Thread() {


            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                while (System.currentTimeMillis() - oldtime < 22000 && progressbar.isShown()) {
                    Message msg = new Message();
                    msg.what = UPDATEPROGRESS;
                    msg.obj = newProgress + 1;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(20 + newProgress * 10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }

        }.start();
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWebView = this;
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 3, 0, 0));
        addView(progressbar);
        //        setWebViewClient(new WebViewClient(){});

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                mTimer = new Timer();
                mTimerTask = new TimerTask() {


                    @Override
                    public void run() {
                        // 在TIMEOUT时间后,则很可能超时.
                        // 此时若webView进度小于100,则判断其超时
                        // 随后利用Handle发送超时的消息
                        if (mWebView.getProgress() < 100) {
                            Message msg = new Message();
                            msg.what = TIMEOUT_ERROR;
                            mHandler.sendMessage(msg);
                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer.purge();
                            }
                        }
                        if (mWebView.getProgress() == 100) {

                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer.purge();
                            }
                        }
                    }
                };
                mTimer.schedule(mTimerTask, TIMEOUT, 1);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }

            @Override
            @Deprecated
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                // TODO Auto-generated method stub
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                readHtmlFormAssets();
            }

        });
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {

            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onRequestFocus(WebView view) {
            // TODO Auto-generated method stub
            super.onRequestFocus(view);
        }

        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            // TODO Auto-generated method stub
            super.onPermissionRequestCanceled(request);
        }

    }

    private void readHtmlFormAssets() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        mWebView.setBackgroundColor(Color.TRANSPARENT);  //  WebView 背景透明效果，不知道为什么在xml配置中无法设置？
        mWebView.loadUrl(FAILHTML);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
