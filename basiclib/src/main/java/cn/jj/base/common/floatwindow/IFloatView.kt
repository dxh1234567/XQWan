package cn.jj.base.common.floatwindow

import android.view.View

interface IFloatView {

    fun setSize(width: Int, height: Int)

    fun setView(view: View)

    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int)

    fun init()

    fun destroy()

    fun updateXY(x: Int, y: Int)

    fun updateX(x: Int)

    fun updateY(y: Int)

    fun getPositionX(): Int

    fun getPositionY(): Int
}