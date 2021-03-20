package cn.jj.base.common.floatwindow

enum class FloatWindowType {
    /**
     * 无法移动
     */
    NORMAL,

    /**
     * 自动贴边
     */
    SLIDE,

    /**
     * 自动回到原位置
     */
    BACK,

    /**
     * 自由移动
     */
    MOVE
}