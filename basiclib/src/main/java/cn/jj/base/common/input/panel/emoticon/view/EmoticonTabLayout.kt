package cn.jj.base.common.input.panel.emoticon.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import cn.jj.base.common.input.panel.emoticon.data.EmoticonSet
import org.jetbrains.anko.dip

internal class EmoticonTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : HorizontalScrollView(context, attrs), View.OnClickListener {

    private val selectColor = Color.parseColor("#f7f7f9")
    private val unSelectColor = 0

    private val tabList: MutableList<View> = mutableListOf()

    private val root: LinearLayout = LinearLayout(context)
        .apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

    init {
        addView(
            root,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        overScrollMode = View.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
    }

    /**
     * 添加表情的tab
     */
    fun addTab(item: EmoticonSet) {
        addTab(item, tabList.size)
    }

    var selectPosition: Int = 0

    fun setSelectTab(position: Int) {
        if (selectPosition == position) {
            return
        }

        tabList[selectPosition].setBackgroundColor(unSelectColor)
        tabList[position].setBackgroundColor(selectColor)
        selectPosition = position
        scrollToPosition(selectPosition)
    }

    fun scrollToPosition(position: Int) {

        val childX = root.getChildAt(position * 2).left
        if (childX < scaleX) {
            scrollTo(childX, 0)
            return
        }

        val childWidth = root.getChildAt(position * 2).width
        val childRight = childX + childWidth
        val right = scaleX + width
        if (childRight > right) {
            scrollTo((childRight - right).toInt(), 0)
        }
    }

    /**
     * 添加表情的tab
     */
    fun addTab(item: EmoticonSet, index: Int) {
        if (index < selectPosition) {
            selectPosition += 1
        }
        val imageView = inflaterTabView()
        imageView.setImageResource(item.icon)
        imageView.setOnClickListener(this)
        imageView.setBackgroundColor(if (index == selectPosition) selectColor else unSelectColor)
        tabList.add(index, imageView)
        root.addView(imageView, index * 2)
        root.addView(inflaterDivider(), index * 2 + 1)
    }

    override fun onClick(v: View) {
        val indexOf = tabList.indexOf(v)
        if (indexOf == -1) {
            return
        }
        setSelectTab(indexOf)
        viewPager?.setCurrentEmoticonSet(indexOf)
        listener?.onClick(indexOf)
    }

    private fun inflaterTabView(): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                context.dip(50),
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    private fun inflaterDivider(): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                context.dip(1),
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                topMargin = context.dip(5)
                bottomMargin = context.dip(5)
            }
            setBackgroundColor(Color.parseColor("#e7e7e7"))
        }
    }

    var viewPager: EmoticonViewPager? = null

    fun setUpWith(viewPager: EmoticonViewPager) {
        this.viewPager = viewPager
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                setSelectTab(viewPager.getEmoticonSetIndex(position))
            }
        })
    }

    var listener: OnTabClickListener? = null

    fun setOnTabClickListener(listener: OnTabClickListener) {
        this.listener = listener
    }

    interface OnTabClickListener {
        fun onClick(position: Int)
    }
}
