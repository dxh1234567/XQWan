package cn.jj.base.common.floatwindow

interface FloatWindowStateListener {
    fun onShow(x: Int, y: Int) {}

    fun onHide() {}

    fun onDestroy() {}

    fun onMoving(x: Int, y: Int) {}

    fun onMoveEnd(x: Int, y: Int) {}

    fun onMoveAnimStart() {}

    fun onMoveAnimIng(x: Int, y: Int) {}

    fun onMoveAnimEnd(x: Int, y: Int) {}
}