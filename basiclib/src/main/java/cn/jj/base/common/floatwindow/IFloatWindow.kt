package cn.jj.base.common.floatwindow

interface IFloatWindow {

    fun show()

    fun hide()

    fun destroy()

    fun isShowing(): Boolean

    fun getFloatView(): FloatView

    fun onConfigurationChanged()

    fun refreshMargin(
        slideLeftMargin: Int,
        slideTopMargin: Int,
        slideRightMargin: Int,
        slideBottomMargin: Int
    )
}