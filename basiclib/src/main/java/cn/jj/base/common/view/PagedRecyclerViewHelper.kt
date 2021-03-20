package cn.jj.base.common.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import cn.jj.base.common.ThreadManager
import cn.jj.base.utils.ScreenUtil

class PagedRecyclerViewHelper private constructor(val context: Context) {

    private lateinit var recyclerView: RecyclerView
    private var recyclerViewWidth = 0

    private var pagePadding = 15 // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
        set(value) {
            field = value
            changeSize(value, showLeftCardWidth)
        }
    private var showLeftCardWidth = 0   // 左边卡片显示大小
        set(value) {
            field = value
            changeSize(pagePadding, value)
        }

    private val snapHelper = CardLinearSnapHelper()
    private var lastPos: Int = 0

    private var itemWidth: Int = 0
        get() {
            if (field == 0) {
                //要重新计算，确保宽度不为0
                changeSize(pagePadding, showLeftCardWidth)
            }
            return field
        }
    private var itemHeight: Int = 0
        get() {
            if (field == 0) {
                //要重新计算，确保高度不为0
                changeSize(pagePadding, showLeftCardWidth)
            }
            return field
        }
    /**
     * itemview的宽高比，如果为null，则高度为recyclerView的高度
     * */
    private var itemRatio: Float = 0f
        set(value) {
            field = value
            changeSize(pagePadding, showLeftCardWidth)
        }

    private var itemMarginSide: Int = 0

    private var currentItemOffset: Int = 0
    private var scale = 1f // 两边视图scale

    private val onPageChangeListeners = HashSet<OnPageChangeListener>()
    private var autoScrollRunnable = object : Runnable {
        override fun run() {
            //多余1个item时，才有自动滚动的需要
            if (autoScrolling && recyclerView.adapter!!.itemCount > 1) {
//                LogUtil.i(TAG, " currentItem=$currentItem")
                setCurrentItem(currentItem + 1, true)
                ThreadManager.postUI(this, AUTO_SCROLL_DELAY)
            }
        }
    }

    var autoScrolling: Boolean = false
        set(value) {
//            LogUtil.i(TAG, " value=$value")
            if (field == value) {
                return
            }
            if (value) {
                startAutoScroll()
            } else {
                stopAutoScroll()
            }
            field = value
        }

    private fun startAutoScroll() {
        ThreadManager.removeUI(autoScrollRunnable)
        ThreadManager.postUI(autoScrollRunnable, AUTO_SCROLL_DELAY)
    }

    private fun stopAutoScroll() {
        ThreadManager.removeUI(autoScrollRunnable)
    }

    var currentItem: Int
        get() {
            val view = snapHelper.findSnapView(recyclerView.layoutManager!!)
            return if (view == null) 0 else recyclerView.layoutManager!!.getPosition(view)
        }
        set(item) = setCurrentItem(item, false)

    fun onCreateViewHolder(parent: ViewGroup, itemView: View) {
        val lp = if (itemView.layoutParams != null) {
            (itemView.layoutParams as RecyclerView.LayoutParams).apply {
                width = itemWidth
                height = itemHeight
            }
        } else RecyclerView.LayoutParams(itemWidth, itemHeight)
        itemView.layoutParams = lp
    }

    fun onBindViewHolder(itemView: View, position: Int, itemCount: Int) {
        itemView.setPadding(pagePadding, 0, pagePadding, 0)
        setViewMargin(itemView, itemMarginSide, 0, itemMarginSide, 0)
    }

    private fun setViewMargin(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        val lp = view.layoutParams as ViewGroup.MarginLayoutParams
        if (lp.leftMargin != left || lp.topMargin != top || lp.rightMargin != right || lp.bottomMargin != bottom) {
            lp.setMargins(left, top, right, bottom)
            view.layoutParams = lp
        }
    }

    private fun changeSize(pagePadding: Int, showLeftCardWidth: Int) {
        if (recyclerViewWidth == 0 && (!::recyclerView.isInitialized || recyclerView.width == 0)) {
            //暂时不能计算出真正需要的值，推迟到需要时再次计算
//            LogUtil.i(TAG, "1.")
            return
        }
        recyclerViewWidth = if (recyclerViewWidth > 0) recyclerViewWidth else recyclerView.width
        val width = recyclerViewWidth - 2 * (pagePadding + showLeftCardWidth)
        itemHeight =
                if (itemRatio > 0f) (itemRatio * width).toInt() else RecyclerView.LayoutParams.MATCH_PARENT
        itemMarginSide = pagePadding + showLeftCardWidth
        itemWidth = width
//        LogUtil.i(TAG, "2. itemWidth=$itemWidth  itemHeight=$itemHeight ")
    }

    fun attachToRecyclerView(rv: RecyclerView?) {
        if (rv == null) {
            return
        }
        this.recyclerView = rv
//        LogUtil.i(TAG, "attachToRecyclerView")
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//                LogUtil.i(TAG, "")
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    snapHelper.mNoNeedToScroll = currentItem == 0 ||
                            currentItem == rv.adapter!!.itemCount - 2
                    if (snapHelper.finalSnapDistance[0] == 0 && snapHelper.finalSnapDistance[1] == 0) {
                        currentItemOffset = 0
                        lastPos = currentItem
                        //认为是一次滑动停止 这里可以写滑动停止回调
                        dispatchOnPageSelected(lastPos)
                        //Log.e("TAG", "滑动停止后最终位置为" + getCurrentItem());
                    }
                } else {
                    snapHelper.mNoNeedToScroll = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                currentItemOffset += dx
                onScrolledChangedCallback()
            }

        })
        snapHelper.attachToRecyclerView(rv)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
//        LogUtil.i(TAG, "setCurrentItem： item=$item")
        if (smoothScroll) {
            recyclerView.smoothScrollToPosition(item)
        } else {
            recyclerView.scrollToPosition(item)
//            ThreadManager.postUI {
//                //@fixbug 设置初始位置时，item没有剧中
//                recyclerView.findViewHolderForAdapterPosition(item)?.itemView?.let {
//                    recyclerView.scrollBy((it.width - recyclerView.width), 0)
//                }
//            }
            currentItemOffset = 0
        }
    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private fun onScrolledChangedCallback() {
        if (itemWidth == 0 || scale >= 1f) {
            return
        }
        val currentItemPos = currentItem
        val offset = currentItemOffset - (currentItemPos - lastPos) * itemWidth
        val percent = Math.max(Math.abs(offset) * 1.0 / itemWidth, 0.0001).toFloat()
        recyclerView.layoutManager!!.apply {
            //left view
            if (currentItemPos > 0) {
                findViewByPosition(currentItemPos - 1)?.apply {
                    scaleY = (1 - scale) * percent + scale
                }
            }
            //current view
            findViewByPosition(currentItemPos)?.apply {
                scaleY = (scale - 1) * percent + 1
            }
            //right view
            if (currentItemPos < recyclerView.adapter!!.itemCount - 1)
                findViewByPosition(currentItemPos + 1)?.apply {
                    scaleY = (1 - scale) * percent + scale
                }
        }
    }

    /**
     * 防止卡片在第一页和最后一页因无法"居中"而一直循环调用onScrollStateChanged-->SnapHelper.snapToTargetExistingView-->onScrollStateChanged
     */
    private class CardLinearSnapHelper : PagerSnapHelper() {
        var mNoNeedToScroll = false
        var finalSnapDistance: IntArray = intArrayOf(0, 0)

        override fun calculateDistanceToFinalSnap(
                layoutManager: RecyclerView.LayoutManager,
                targetView: View
        ): IntArray? {
//            LogUtil.i(TAG, "calculateDistanceToFinalSnap mNoNeedToScroll=$mNoNeedToScroll  ");
            if (mNoNeedToScroll) {
                finalSnapDistance[0] = 0
                finalSnapDistance[1] = 0
            } else {
                val distance = super.calculateDistanceToFinalSnap(layoutManager, targetView)
//                LogUtil.i(TAG, "calculateDistanceToFinalSnap distance=$distance  ");
                if (distance != null) {
                    finalSnapDistance[0] = distance[0]
                    finalSnapDistance[1] = distance[1]
                } else {
                    finalSnapDistance[0] = 0
                    finalSnapDistance[1] = 0
                }
            }
            return finalSnapDistance
        }
    }

    fun dispatchOnPageSelected(position: Int) {
        onPageChangeListeners.forEach {
            it.onPageSelected(position)
        }
    }

    fun clearOnPageChangeListeners() {
        onPageChangeListeners.clear()
    }

    fun removeOnPageChangeListener(listener: OnPageChangeListener) {
        onPageChangeListeners.remove(listener)
    }

    fun addOnPageChangeListener(listener: OnPageChangeListener?) {
        if (listener != null) {
            onPageChangeListeners.add(listener)
        }
    }


    interface OnPageChangeListener {
        fun onPageSelected(position: Int)
    }

    private class Params {
        internal lateinit var context: Context
        internal var pagePadding: Int = 0
        internal var sideCardShowWidth: Int = 0
        internal var listener: OnPageChangeListener? = null
        internal var recyclerView: RecyclerView? = null
        internal var autoScrolling: Boolean = true
        internal var scale: Float = 1f
        internal var itemRatio: Float = 0f
        internal var recyclerViewWidth: Int = 0 //@note 本來需要計算出來，但recyclerView的寬度不好确定，暂时由调用方指定

        fun apply(helper: PagedRecyclerViewHelper) {
            helper.attachToRecyclerView(recyclerView)
            helper.recyclerViewWidth = recyclerViewWidth
            helper.pagePadding = pagePadding
            helper.showLeftCardWidth = sideCardShowWidth
            helper.addOnPageChangeListener(listener)
            helper.autoScrolling = autoScrolling
            helper.scale = scale
            helper.itemRatio = itemRatio
        }
    }

    class Builder(context: Context) {
        private val P = Params()

        init {
            P.context = context
        }

        fun pagePadding(pagePadding: Int): Builder {
            P.pagePadding = pagePadding
            return this
        }

        fun sideCardShowWidth(sideCardShowWidth: Int): Builder {
            P.sideCardShowWidth = sideCardShowWidth
            return this
        }

        fun listener(listener: OnPageChangeListener): Builder {
            P.listener = listener
            return this
        }

        fun autoScrolling(autoScrolling: Boolean): Builder {
            P.autoScrolling = autoScrolling
            return this
        }

        fun scale(scale: Float): Builder {
            P.scale = scale
            return this
        }

        fun itemRatio(itemRatio: Float): Builder {
            P.itemRatio = itemRatio
            return this
        }

        fun recyclerViewWidth(recyclerViewWidth: Int): Builder {
            P.recyclerViewWidth = recyclerViewWidth
            return this
        }

        fun attach(recyclerView: RecyclerView): PagedRecyclerViewHelper {
            P.recyclerView = recyclerView
            val helper = PagedRecyclerViewHelper(P.context)
            P.apply(helper)
            return helper
        }
    }

    companion object {
        private const val TAG = "PagedRecyclerViewHelper"
        private const val AUTO_SCROLL_DELAY = 5000L
        private val SCREEN_WIDTH = ScreenUtil.getScreenWidth()
    }
}
