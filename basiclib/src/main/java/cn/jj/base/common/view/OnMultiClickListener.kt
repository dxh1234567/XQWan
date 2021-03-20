package cn.jj.base.common.view

import android.os.SystemClock
import android.view.View

/**
 * 防止控件快速点击
 *
 * @author Richie on 2018.11.09
 */
abstract class OnMultiClickListener : View.OnClickListener {
    private var mLastClickTime: Long = 0
    private var mViewId = View.NO_ID

    /**
     * 处理后的点击事件
     *
     * @param v
     */
    protected abstract fun onMultiClick(v: View)

    override fun onClick(v: View) {
        val curClickTime = SystemClock.elapsedRealtime()
        val viewId = v.id
        if (mViewId == viewId) {
            if (curClickTime - mLastClickTime in 0..MIN_CLICK_DELAY_TIME) {
                mLastClickTime = curClickTime
                onMultiClick(v)
            }
        } else {
            mViewId = viewId
            mLastClickTime = curClickTime
            onMultiClick(v)
        }
    }

    companion object {
        private val MIN_CLICK_DELAY_TIME = 500
    }
}
