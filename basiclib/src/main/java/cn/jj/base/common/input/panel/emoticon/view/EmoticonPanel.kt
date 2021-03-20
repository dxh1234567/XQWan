package cn.jj.base.common.input.panel.emoticon.view

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import cn.jj.base.common.input.panel.emoticon.data.Emoticon
import cn.jj.base.common.input.panel.emoticon.data.EmoticonSet
import cn.jj.base.utils.BitmapUtil
import cn.jj.basiclib.R
import com.airbnb.paris.utils.setPaddingHorizontal
import com.airbnb.paris.utils.setPaddingVertical
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.dip

/**
 * 包括三个部分
 *
 * 1 viewpager
 * 2 PagerIndicator
 * 3 tab
 *
 */
class EmoticonPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mViewPager: EmoticonViewPager
    private var indicator: EmoticonPagerIndicator
    private var tabLayout: EmoticonTabLayout
    private var deleteView: ImageView
    private var listener: OnEmoticonClickListener? = null

    init {
        gravity = Gravity.CENTER

        mViewPager = EmoticonViewPager(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }
        indicator = EmoticonPagerIndicator(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                context.dip(10)
            )
        }
        tabLayout = EmoticonTabLayout(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                context.dip(40)
            )
        }
        deleteView = ImageView(context).apply {
            isEnabled = false
            layoutParams = LayoutParams(
                context.dip(50),
                context.dip(40)
            ).apply {
                bottomMargin = context.dip(10)
                rightMargin = context.dip(10)
                alignParentBottom()
                alignParentRight()
            }
            setPaddingHorizontal(context.dip(15))
            setPaddingVertical(context.dip(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#F3F3F3"))
                cornerRadius = context.dip(2).toFloat()
            }
            setImageDrawable(
                StateListDrawable()
                    .apply {
                        addState(
                            intArrayOf(android.R.attr.state_enabled),
                            ContextCompat.getDrawable(context, R.drawable.ic_delete_emoticon)
                        )
                        addState(
                            intArrayOf(),
                            BitmapUtil.getTintDrawable(
                                ContextCompat.getDrawable(context, R.drawable.ic_delete_emoticon),
                                Color.parseColor("#C7C7C7"),
                                null
                            )
                        )
                    })
            setOnClickListener {
                listener?.onClick(Emoticon(tag = "delete"))
            }
        }
        addView(mViewPager)
//        addView(indicator)
        addView(tabLayout)
        addView(deleteView)
        indicator.setupWithViewPager(mViewPager)
        tabLayout.setUpWith(mViewPager)
    }

    fun addEmoticonSet(emoticonSets: List<EmoticonSet>) {
        if (emoticonSets.size == 1) {
            tabLayout.visibility = View.GONE
            indicator.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                context.dip(10)
            ).apply {
                bottomMargin = context.dip(10)
            }
        } else {
            tabLayout.visibility = View.VISIBLE
            indicator.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                context.dip(10)
            )
        }
        mViewPager.addEmoticonSet(emoticonSets)
        emoticonSets.forEach {
            tabLayout.addTab(it)
        }
        indicator.updateWithPosition(mViewPager, mViewPager.currentItem)
        tabLayout.setSelectTab(mViewPager.getEmoticonSetIndex(mViewPager.currentItem))
    }

    fun setOnEmoticonClickListener(listener: OnEmoticonClickListener?) {
        this.listener = listener
        mViewPager.setOnEmoticonClickListener(listener)
    }

    fun showPanel() {
        if (EmoticonConfigureModel.hasRecentEmoticon()) {
            mViewPager.notifyDataSetChanged()
        }
        updateDeleteViewLoc()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (isInEditMode) {
            return
        }
        if (EmoticonConfigureModel.hasRecentEmoticon()) {
            mViewPager.notifyDataSetChanged()
        }
        updateDeleteViewLoc()
    }

    fun setDeleteEnable(enable: Boolean) {
        deleteView.isEnabled = enable
    }

    private fun updateDeleteViewLoc() {
        (deleteView.layoutParams as MarginLayoutParams).apply {
            rightMargin =
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    context.dip(20)
                } else {
                    context.dip(10)
                }
        }
    }
}