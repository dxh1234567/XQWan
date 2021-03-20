package cn.jj.base.common.glide

class Corners {
    companion object {
        const val CORNER_TOP_LEFT = 1
        const val CORNER_TOP_RIGHT = 1 shl 1
        const val CORNER_BOTTOM_LEFT = 1 shl 2
        const val CORNER_BOTTOM_RIGHT = 1 shl 3
        const val CORNER_ALL =
            CORNER_TOP_LEFT or CORNER_TOP_RIGHT or CORNER_BOTTOM_LEFT or CORNER_BOTTOM_RIGHT
    }
}