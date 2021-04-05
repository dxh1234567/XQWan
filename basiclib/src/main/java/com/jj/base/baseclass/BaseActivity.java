package com.jj.base.baseclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;



import java.util.List;

import com.jj.base.BaseContractNew;
import com.jj.base.LoadingLayout;
import com.jj.base.UGIndicatorManager;
import com.jj.base.UIUtils;
import com.jj.base.common.helper.FragmentBackHandler;
import com.jj.basiclib.R;

/**
 * Created By duXiaHui
 * on 2021/3/28
 */
public abstract class BaseActivity extends FragmentActivity
        implements BaseContractNew.BaseView,
        LoadingLayout.EmptyRefreshListener, LoadingLayout.ErrorRefreshListener {

    protected LoadingLayout mLoadingLayout;

    protected String mPageName = "";

    protected ViewGroup rootView;


    /**
     * 上次点击返回的时间
     */
    private long lastBackTime = 0;

    /**
     * 是否需要返回提示
     */
    protected boolean showBackTip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        rootView = findViewById(R.id.activity_base);
        // 添加标题栏
        View titleBar = initTitleBar();
        if (titleBar != null) {
            rootView.addView(titleBar, 0,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            UIUtils.getDimensionPixelSize(R.dimen.title_height)
                    ));
        }
        mLoadingLayout = findViewById(R.id.loading_layout);
        mLoadingLayout.addEmptyRefreshListener(this);
        mLoadingLayout.addErrorRefreshListener(this);
        // 开启心跳
        openHeartService();
        initView();
        // 加载数据
        loadData();

    }


    protected void initView() {
    }

    /**
     * 加载网络数据
     */
    protected void loadData() {
    }




    /**
     * 开启心跳服务
     */
    private void openHeartService() {
    }

    /**
     * 初始化控件
     *
     * @param id  layoutId
     * @param <T> 类型
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewById(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //网络监听

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments.size() > 0) {
            for (Fragment f : fragments) {
                if (f instanceof BaseFragment) {
                    ((BaseFragment) f).finish();
                }
            }
        }
        super.finish();
    }

    public LoadingLayout getLoadingLayout() {
        return mLoadingLayout;
    }

    private void setBaseContentView(View view) {
        mLoadingLayout.addView(view);
        showBaseContent();
    }

    protected void setBaseContentView(int resId) {
        View view = getLayoutInflater().inflate(resId, mLoadingLayout, false);
        setBaseContentView(view);
    }

    @Override
    public void showBaseLoading() {
        mLoadingLayout.showLoading();
    }

    @Override
    public void showBaseContent() {
        mLoadingLayout.showContent();
    }

    @Override
    public void showBaseEmpty() {
        mLoadingLayout.showEmpty();
    }

    @Override
    public void showBaseNetError() {
        mLoadingLayout.showError();
    }

    @Override
    public void showLoading() {
        UGIndicatorManager.showLoading(this);
    }

    public void hideLoading() {
        UGIndicatorManager.dismissLoading();
    }

    @Override
    public boolean isShowBaseError() {
        return mLoadingLayout.isShowBaseError();
    }

    @Override
    public boolean isContentShow() {
        return mLoadingLayout.isInContentState();
    }

    @Override
    public void refreshComplete() {

    }

    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(BaseActivity.this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public static void openActivity(Context context, Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(context, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        context.startActivity(intent);
    }


    @Override
    public void emptyRefresh() {
        loadData();
    }

    @Override
    public void errorRefresh() {
        loadData();
    }

    @Override
    public void showErrorMsg(String msg) {
        UGIndicatorManager.showError(msg);
    }

    public void showErrorMsg(Throwable e) {
    }

    @Override
    public void showErrorMsg(@StringRes int stringId) {
        UGIndicatorManager.showError(stringId);
    }

    @Override
    public void showSuccess(String msg) {
        UGIndicatorManager.showSuccess(msg);
    }


    @Override
    public void onBackPressed() {
        try {
            if (!FragmentBackHandler.INSTANCE.handleBackPress(this)) {
                if (!dealBack()) {
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            finish();
        }

    }

    protected boolean dealBack() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (showBackTip) {
                if (System.currentTimeMillis() - lastBackTime < 2000) {
                    confirmBack();
                } else {
                    // 执行确认返回的操作
                    showBackTip();
                    lastBackTime = System.currentTimeMillis();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 点击返回的提示
     */
    protected void showBackTip() {
    }

    /**
     * 确认返回的操作
     */
    protected void confirmBack() {
    }

    /**
     * 重写此方法，子fragment才能出发回调onActivityResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment f : fragments) {
                f.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void setShowBackTip(boolean showBackTip) {
        this.showBackTip = showBackTip;
    }

    /**
     * 初始化标题栏
     */
    protected View initTitleBar() {
        return null;
    }
}
