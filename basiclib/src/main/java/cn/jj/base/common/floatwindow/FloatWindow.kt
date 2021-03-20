package cn.jj.base.common.floatwindow

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import cn.jj.base.utils.ForegroundCallbacks
import cn.jj.base.utils.ScreenUtil
import kotlin.math.abs

class FloatWindow private constructor(val builder: Builder) : IFloatWindow,
    FloatLifecycleListener, View.OnTouchListener {

    private var mAnimator: ValueAnimator? = null
    private val touchSlop = ViewConfiguration.get(builder.context).scaledTouchSlop

    private var downX = 0f
    private var downY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var isMoving = false
    private var downFlag = false

    private val mFloatView: FloatView
    private val mFloatLifecycle: FloatLifecycle
    private var inited = false
    private var isShow = false
    private var hideByUser = false
    private var background = ForegroundCallbacks.get().isBackground
    private var showByFilter = true

    init {
        mFloatView = FloatView(
            builder.context,
            builder.mOutScreen,
            builder.mRequestPermission,
            builder.mPermissionListener
        ).apply {
            setSize(builder.mWidth, builder.mHeight)
            setGravity(builder.mGravity, builder.xOffset, builder.yOffset)
            setView(builder.mView!!)
        }
        mFloatLifecycle = FloatLifecycle(
            builder.context.applicationContext,
            builder.mFilterFlag,
            builder.mFilterActivities,
            this
        )
        initTouchEvent()
    }

    /*FloatLifecycleListener*/
    override fun onShowByFilter() {
        showByFilter = true
        if (!hideByUser) {
            showInternal()
        }
    }

    override fun onHideByFilter() {
        showByFilter = false
        hideInternal()
    }

    override fun onBackToDesktop() {
        background = true
        if (!builder.mDesktopShow) {
            hideInternal()
        }
    }

    override fun onBackToApp() {
        background = false
        if (!builder.mDesktopShow && !hideByUser) {
            showInternal()
        }
    }

    private fun checkFilterStatus(): Boolean {
        if (!showByFilter) {
            return false
        }
        if (!builder.mDesktopShow && background) {
            return false
        }
        return true
    }

    /*FloatLifecycleListener*/

    /*View.OnTouchListener*/
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //记录按下位置
                downX = event.rawX
                downY = event.rawY
                moveX = downX
                moveY = downY
                cancelAnimator()
                downFlag = true
            }
            MotionEvent.ACTION_MOVE -> {
                //计算移动距离
                val nowX = event.rawX
                val nowY = event.rawY
                val dX = nowX - moveX
                val dY = nowY - moveY
                moveX = nowX
                moveY = nowY

                //判断是否移动
                if (!isMoving) {
                    isMoving = abs(moveX - downX) > touchSlop || abs(moveY - downY) > touchSlop
                }

                if (isMoving) {
                    //取消长按事件
                    if (downFlag) {
                        downFlag = false
                        mFloatView.cancelLongPress()
                    }
                    //检查是否超出范围
                    var newX = mFloatView.getPositionX() + dX.toInt()
                    var newY = mFloatView.getPositionY() + dY.toInt()
                    if (!builder.mOutScreen) {
                        newX = newX.coerceIn(
                            builder.mSlideLeftMargin,
                            ScreenUtil.getScreenWidth() - builder.mSlideRightMargin - view.width
                        )
                        newY = newY.coerceIn(
                            builder.mSlideTopMargin,
                            ScreenUtil.getScreenHeight() - builder.mSlideBottomMargin - view.height
                        )
                    }
                    //更新位置
                    mFloatView.updateXY(newX, newY)
                    builder.mStateListener?.onMoving(newX, newY)
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (isMoving) {
                    isMoving = false
                    builder.mStateListener?.onMoveEnd(
                        mFloatView.getPositionX(),
                        mFloatView.getPositionY()
                    )
                    checkPosition(view)
                    return true
                }
            }
            else -> {
            }
        }
        return false
    }
    /*View.OnTouchListener*/

    @SuppressLint("ClickableViewAccessibility")
    private fun initTouchEvent() {
        when (builder.mType) {
            FloatWindowType.NORMAL -> {
            }
            else -> {
                mFloatView.setOnTouchListener(this)
            }
        }
    }

    private fun checkPosition(v: View) {
        when (builder.mType) {
            FloatWindowType.SLIDE -> {
                val startX = mFloatView.getPositionX()
                val centerX = startX + v.width / 2f
                val screenWidth = ScreenUtil.getScreenWidth()
                val screenHeight = ScreenUtil.getScreenHeight()
                val halfOfScreenWidth = screenWidth / 2
                val pvhX =
                    PropertyValuesHolder.ofInt(
                        "x", startX,
                        if (centerX > halfOfScreenWidth) {
                            screenWidth - v.width - builder.mSlideRightMargin
                        } else {
                            builder.mSlideLeftMargin
                        }
                    )
                val pvhY =
                    PropertyValuesHolder.ofInt(
                        "y",
                        mFloatView.getPositionY(),
                        mFloatView.getPositionY().coerceIn(
                            builder.mSlideTopMargin,
                            screenHeight - builder.mSlideBottomMargin - v.height
                        )
                    )
                mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY)
                startAnimator()
            }
            FloatWindowType.BACK -> {
                val pvhX =
                    PropertyValuesHolder.ofInt("x", mFloatView.getPositionX(), builder.xOffset)
                val pvhY =
                    PropertyValuesHolder.ofInt("y", mFloatView.getPositionY(), builder.yOffset)
                mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY)
                startAnimator()
            }
            else -> {
            }
        }
    }

    private fun startAnimator() {
        if (builder.mInterpolator == null) {
            builder.mInterpolator = DecelerateInterpolator()
        }
        mAnimator?.apply {
            interpolator = builder.mInterpolator
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mAnimator!!.removeAllUpdateListeners()
                    mAnimator!!.removeAllListeners()
                    mAnimator = null
                    builder.mStateListener?.onMoveAnimEnd(
                        mFloatView.getPositionX(),
                        mFloatView.getPositionY()
                    )
                }
            })
            addUpdateListener { animation ->
                val x = animation.getAnimatedValue("x") as Int
                val y = animation.getAnimatedValue("y") as Int
                mFloatView.updateXY(x, y)
                builder.mStateListener?.onMoveAnimIng(x, y)
            }
            duration = builder.mDuration
            start()
        }
        builder.mStateListener?.onMoveAnimStart()
    }

    private fun cancelAnimator() {
        if (mAnimator?.isRunning == true) {
            mAnimator?.cancel()
        }
    }
    override fun getFloatView() = mFloatView
    /*IFloatWindow*/
    override fun show() {
        hideByUser = false
        if (!inited) {
            inited = true
            mFloatView.init()
            mFloatLifecycle.init()
        }
        if (!checkFilterStatus()) {
            return
        }
        showInternal()
    }

    private fun showInternal() {
        if (!inited || isShow) {
            return
        }
        isShow = true
        mFloatView.visibility = View.VISIBLE
        builder.mStateListener?.onShow(mFloatView.getPositionX(), mFloatView.getPositionY())
    }

    override fun hide() {
        hideByUser = true
        hideInternal()
    }

    private fun hideInternal() {
        if (!inited || !isShow) {
            return
        }
        isShow = false
        mFloatView.visibility = View.INVISIBLE
        builder.mStateListener?.onHide()
    }

    override fun destroy() {
        destroyInternal()
    }

    private fun destroyInternal() {
        if (inited){
            mFloatView.destroy()
            mFloatLifecycle.destroy()
        }
        inited = false
        isShow = false
        cancelAnimator()
        builder.mStateListener?.onDestroy()
    }

    override fun isShowing(): Boolean {
        return isShow
    }

    override fun onConfigurationChanged() {
        if (!inited) {
            return
        }
        when (builder.mType) {
            FloatWindowType.SLIDE -> {
                if (mFloatView.getPositionX() != 0) {
                    mFloatView.updateX(ScreenUtil.getScreenWidth() - builder.mSlideRightMargin - mFloatView.width)
                }
            }
            else -> {
            }
        }
    }

    override fun refreshMargin(
        slideLeftMargin: Int,
        slideTopMargin: Int,
        slideRightMargin: Int,
        slideBottomMargin: Int
    ) {
        builder.setMargin(slideLeftMargin, slideTopMargin, slideRightMargin, slideBottomMargin)
    }

    /*IFloatWindow*/

    class Builder(internal val context: Context) {

        internal var mView: View? = null
        internal var mLayoutId = 0
        internal var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT
        internal var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        internal var xOffset = 0
        internal var yOffset = 0
        internal var mGravity = Gravity.TOP or Gravity.START
        internal var mType = FloatWindowType.SLIDE
        internal var mSlideLeftMargin = 0
        internal var mSlideRightMargin = 0
        internal var mSlideBottomMargin = 0
        internal var mSlideTopMargin = 0
        internal var mOutScreen = false

        //位移动画
        internal var mDuration: Long = 300
        internal var mInterpolator: TimeInterpolator? = null
        internal var mTag = mDefaultTag
        internal var mDesktopShow = false
        internal var mFilterFlag = true
        internal var mFilterActivities: Array<out Class<*>>? = null

        internal var mRequestPermission = false
        internal var mPermissionListener: FloatPermissionListener? = null
        internal var mStateListener: FloatWindowStateListener? = null

        fun setView(view: View): Builder {
            mView = view
            return this
        }

        fun setView(@LayoutRes layoutId: Int): Builder {
            mLayoutId = layoutId
            return this
        }

        fun setWidth(width: Int): Builder {
            mWidth = width
            return this
        }

        fun setHeight(height: Int): Builder {
            mHeight = height
            return this
        }

        fun setSize(width: Int, height: Int): Builder {
            mWidth = width
            mHeight = height
            return this
        }

        fun setX(x: Int): Builder {
            xOffset = x
            return this
        }

        fun setY(y: Int): Builder {
            yOffset = y
            return this
        }

        fun setPosition(x: Int, y: Int): Builder {
            xOffset = x
            yOffset = y
            return this
        }

        fun setPosition(gravity: Int, x: Int, y: Int): Builder {
            mGravity = gravity
            xOffset = x
            yOffset = y
            return this
        }

        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param filterFlag 过滤类型
         * @param activities 　过滤界面
         */
        fun setFilter(
            filterFlag: Boolean,
            vararg activities: Class<*>
        ): Builder {
            mFilterFlag = filterFlag
            mFilterActivities = activities
            return this
        }

        /**
         * 设置悬浮窗类型
         */
        fun setType(type: FloatWindowType): Builder {
            mType = type
            return this
        }

        /**
         * 设置边距
         */
        fun setMargin(
            slideLeftMargin: Int,
            slideTopMargin: Int,
            slideRightMargin: Int,
            slideBottomMargin: Int
        ): Builder {
            mOutScreen = false
            mSlideLeftMargin = slideLeftMargin
            mSlideTopMargin = slideTopMargin
            mSlideRightMargin = slideRightMargin
            mSlideBottomMargin = slideBottomMargin
            return this
        }

        /**
         * 是否可以移动到范围之外
         */
        fun setCanOutScreen(outScreen: Boolean): Builder {
            mOutScreen = outScreen
            return this
        }

        /**
         * 归位动画设置
         */
        fun setAnimatorStyle(
            duration: Long,
            interpolator: TimeInterpolator?
        ): Builder {
            mDuration = duration
            mInterpolator = interpolator
            return this
        }

        /**
         * 当前悬浮窗的标识
         */
        fun setTag(tag: String): Builder {
            mTag = tag
            return this
        }

        fun setDesktopShow(show: Boolean): Builder {
            mDesktopShow = show
            return this
        }

        fun setStateListener(stateListener: FloatWindowStateListener): Builder {
            mStateListener = stateListener
            return this
        }

        fun setRequestPermission(
            reqPermission: Boolean,
            listener: FloatPermissionListener?
        ): Builder {
            mPermissionListener = listener
            mRequestPermission = reqPermission
            return this
        }

        fun build(): IFloatWindow {
            require(!mFloatWindowMap.containsKey(mTag)) { "FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow" }
            require(!(mView == null && mLayoutId == 0)) { "View has not been set!" }
            if (mView == null) {
                val inflate =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                mView = inflate.inflate(mLayoutId, null)
            }
            val floatWindowImpl = FloatWindow(this)
            mFloatWindowMap[mTag] = floatWindowImpl
            return floatWindowImpl
        }
    }

    companion object {

        private const val mDefaultTag = "default_float_window_tag"

        private var mFloatWindowMap: MutableMap<String, IFloatWindow> = mutableMapOf()

        /**
         * 全局悬浮窗，请传入ApplicationContext
         *
         * 传入activity时，使用TYPE_APPLICATION_PANEL方式
         *
         * 传入activity时，因为和activity绑定，内部没有做弱引用，注意destroy
         */
        @MainThread
        fun with(context: Context): Builder {
            return Builder(context)
        }

        @JvmOverloads
        fun get(tag: String = mDefaultTag): IFloatWindow? {
            return mFloatWindowMap[tag]
        }

        @JvmOverloads
        fun destroy(tag: String = mDefaultTag) {
            mFloatWindowMap.remove(tag)?.destroy()
        }
    }


}