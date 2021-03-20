package cn.jj.base.common.input.widget.fullscreen;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Window;
import android.widget.LinearLayout;

import cn.jj.base.common.input.util.KeyboardUtil;
import cn.jj.base.common.input.util.ViewUtil;

/**
 * 全屏主题窗口的面板容器LinearLayout，并且此布局的高度始终等于键盘的高度
 * <p/>
 *
 * @see cn.jj.base.common.input.util.KeyboardUtil#attach(android.app.Activity, IPanelHeightTarget)
 * @see #recordKeyboardStatus(Window)
 */
public class FSPanelLinearLayout extends LinearLayout implements IPanelHeightTarget,
        IFSPanelConflictLayout {

    private FSPanelLayoutHandler panelHandler;

    public FSPanelLinearLayout(Context context) {
        super(context);
        init();
    }

    public FSPanelLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FSPanelLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FSPanelLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        panelHandler = new FSPanelLayoutHandler(this);
    }

    @Override
    public void refreshHeight(int panelHeight) {
        ViewUtil.refreshHeight(this, panelHeight);
    }

    @Override
    public void onKeyboardShowing(boolean showing) {
        panelHandler.onKeyboardShowing(showing);
        if (listener != null) {
            listener.onKeyboardShowing(showing);
        }
    }


    @Override
    public void recordKeyboardStatus(final Window window) {
        panelHandler.recordKeyboardStatus(window);
    }

    private KeyboardUtil.OnKeyboardShowingListener listener;

    public void setOnKeyboardShowingListener(KeyboardUtil.OnKeyboardShowingListener listener) {
        this.listener = listener;
    }
}
