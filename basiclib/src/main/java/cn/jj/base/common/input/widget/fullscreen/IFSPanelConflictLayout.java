package cn.jj.base.common.input.widget.fullscreen;

import android.app.Activity;
import android.view.Window;

public interface IFSPanelConflictLayout {

    /**
     * {@link Activity# onPause()}时候记录当前键盘状态，{@link Activity# onResume()}时候并自动恢复
     * <p/>
     * 建议在{@link Activity# onPause()} 时候调用这个方法
     * <p/>
     */
    void recordKeyboardStatus(Window window);
}
