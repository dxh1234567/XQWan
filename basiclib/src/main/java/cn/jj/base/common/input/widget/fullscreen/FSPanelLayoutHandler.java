package cn.jj.base.common.input.widget.fullscreen;

import android.view.View;
import android.view.Window;

import cn.jj.base.common.input.util.KeyboardUtil;


/**
 * 在全屏布局中处理键盘和面板
 */
public class FSPanelLayoutHandler implements IFSPanelConflictLayout {

    private final View panelLayout;
    private boolean isKeyboardShowing;

    public FSPanelLayoutHandler(final View panelLayout) {
        this.panelLayout = panelLayout;
    }

    public void onKeyboardShowing(boolean showing) {
        isKeyboardShowing = showing;
        if (!showing && panelLayout.getVisibility() == View.INVISIBLE) {
            panelLayout.setVisibility(View.GONE);
        }

        if (!showing && recordedFocusView != null) {
            restoreFocusView();
            recordedFocusView = null;
        }
    }

    @Override
    public void recordKeyboardStatus(Window window) {
        final View focusView = window.getCurrentFocus();
        if (focusView == null) {
            return;
        }

        if (isKeyboardShowing) {
            saveFocusView(focusView);
        } else {
            focusView.clearFocus();
        }
    }

    private View recordedFocusView;

    private void saveFocusView(final View focusView) {
        recordedFocusView = focusView;
        focusView.clearFocus();
        panelLayout.setVisibility(View.GONE);
    }

    private void restoreFocusView() {
        panelLayout.setVisibility(View.INVISIBLE);
        KeyboardUtil.showKeyboard(recordedFocusView);

    }
}
