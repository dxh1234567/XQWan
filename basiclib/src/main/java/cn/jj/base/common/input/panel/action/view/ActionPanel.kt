package cn.jj.base.common.input.panel.action.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.jj.base.common.input.panel.action.BaseAction
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.centerHorizontally
import org.jetbrains.anko.dip

class ActionPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mViewPager: ViewPager
    private var indicator: PagerIndicator

    private var actions = mutableListOf<BaseAction>()

    private var listener: OnActionResultListener? = null

    init {
        mViewPager = ViewPager(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            ).apply {
                bottomMargin = context.dip(20)
            }
            adapter = ActionPagerAdapter(actions)
        }
        indicator = PagerIndicator(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                context.dip(10)
            ).apply {
                bottomMargin = context.dip(10)
                alignParentBottom()
                centerHorizontally()
            }
        }
        addView(mViewPager)
        addView(indicator)
        indicator.setupWithViewPager(mViewPager)
    }


    fun addActions(list: List<BaseAction>) {
        actions.addAll(list)
        mViewPager.adapter?.notifyDataSetChanged()
        indicator.setupWithViewPager(mViewPager)
    }

    fun setOnActionResultListener(listener: OnActionResultListener) {
        this.listener = listener
        mViewPager.adapter?.let {
            if (it is ActionPagerAdapter) {
                it.setOnActionResultListener(listener)
            }
        }
    }

    class ActionPagerAdapter(
        val actions: List<BaseAction>
    ) : PagerAdapter() {

        private var listener: OnActionResultListener? = null

        fun setOnActionResultListener(listener: OnActionResultListener) {
            this.listener = listener
        }

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = (actions.size + 7) / 8

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val subList = actions.subList(
                fromIndex = position * 8,
                toIndex = if ((position + 1) * 8 > actions.size) actions.size
                else (position + 1) * 8
            )

            val pagerView = ActionPagerView(container.context).apply {
                setOnActionResultListener(listener)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setActions(subList)
            }
            container.addView(pagerView)
            return pagerView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    interface OnActionResultListener {
        fun onActionResult(message: String, baseAction: BaseAction)
    }
}
