package com.jj.base.common.input.panel.emoticon.view

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.jj.base.baseclass.BaseConfigureModel
import com.jj.base.common.input.panel.emoticon.data.Emoticon
import com.jj.base.common.input.panel.emoticon.data.EmoticonSet
import com.jj.base.utils.LogUtil
import com.jj.base.utils.ScreenUtil
import com.jj.base.utils.Utility
import com.jj.basiclib.R
import com.airbnb.epoxy.*
import com.airbnb.paris.utils.setPaddingHorizontal
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.*

/**
 * 展示多个表情集的ViewPager
 */
internal class EmoticonViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    private val emoticonPageAdapter: EmoticonPageAdapter
    var data: MutableList<EmoticonSet> = mutableListOf()

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        emoticonPageAdapter = EmoticonPageAdapter(data)
        adapter = emoticonPageAdapter
    }

    fun addEmoticonSet(emoticonSets: List<EmoticonSet>) {
        data.addAll(emoticonSets)
        emoticonPageAdapter.notifyDataSetChanged()
    }

    fun notifyDataSetChanged() {
        emoticonPageAdapter.notifyDataSetChanged()
    }

    fun setCurrentEmoticonSet(position: Int) {
        var index = 0
        data.forEachWithIndex { i, emoticonSet ->
            if (i < position) index += emoticonSet.getPageCount()
        }
        currentItem = index
    }

    fun getEmoticonSetIndex(position: Int) = emoticonPageAdapter.getEmoticonSetIndex(position)

    fun getPagerIndicatorCount(position: Int) =
        emoticonPageAdapter.getEmoticonSet(position).getPageCount()

    fun getIndexInEmoticonSet(position: Int) = emoticonPageAdapter.getIndexInEmoticonSet(position)

    fun setOnEmoticonClickListener(listener: OnEmoticonClickListener?) {
        emoticonPageAdapter.setOnEmoticonClickListener(listener)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onInterceptTouchEvent(ev)
    }
}

/**
 * ViewPager适配器
 */
internal class EmoticonPageAdapter(
    val items: List<EmoticonSet>
) : PagerAdapter() {

    var listener: OnEmoticonClickListener? = null

    fun setOnEmoticonClickListener(listener: OnEmoticonClickListener?) {
        this.listener = listener
    }

    fun getEmoticonSetIndex(position: Int): Int {
        var index = position
        items.forEachWithIndex { i, emoticonSet ->
            if (emoticonSet.getPageCount() > index) {
                return i
            } else {
                index -= emoticonSet.getPageCount()
            }
        }
        throw RuntimeException("position=$position is too large, and now count= $count")
    }

    /**
     * 根据位置获取当前处于的EmoticonSet
     */
    fun getEmoticonSet(position: Int): EmoticonSet {
        var index = position
        items.forEach {
            if (it.getPageCount() > index) {
                return it
            } else {
                index -= it.getPageCount()
            }
        }
        throw RuntimeException("position=$position is too large, and now count= $count")
    }

    fun getIndexInEmoticonSet(position: Int): Int {
        var index = position
        items.forEach {
            if (it.getPageCount() > index) {
                return index
            } else {
                index -= it.getPageCount()
            }
        }
        throw RuntimeException("position=$position is too large, and now count= $count")
    }

    override fun getCount(): Int {
        return items.sumBy {
            it.getPageCount()
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //获取当前的表情集合
        val emoticonSet = getEmoticonSet(position)
        //获取当前需要展示的表情
        val index = getIndexInEmoticonSet(position)
        val emoticons = mutableListOf<Emoticon>()
        emoticons.addAll(emoticonSet.getEmoticons(index))
//        if (emoticonSet.hasDelete) {
//            //todo
//            emoticons.add(Emoticon(icon = R.drawable.closebtn, tag = "delete"))
//        }
//
//        val minHeight = Math.min(container.measuredHeight, container.context.dip(200))
//
//        val relativeLayout = RelativeLayout(container.context).apply {
//            layoutParams = ViewGroup.MarginLayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            gravity = Gravity.CENTER
//        }
//
//        val gridView = GridView(container.context).apply {
//            layoutParams = RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                minHeight
//            )
//        }
//        gridView.numColumns = emoticonSet.numColumns
//        gridView.adapter = EmoticonAdapter(emoticons, minHeight / emoticonSet.numRows)
//        gridView.setOnItemClickListener { _, _, pos, _ ->
//            listener?.onClick(emoticons[pos])
//        }
//        relativeLayout.addView(gridView)
//        container.addView(relativeLayout)
        val v = buildPageView(container.context, emoticonSet.numColumns, emoticons)
        container.addView(
            v,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun buildPageView(context: Context, spanCount: Int, data: List<Emoticon>) =
        EpoxyRecyclerView(context).apply {
            layoutManager = GridLayoutManager(context, spanCount)
            withModels {
                // 最近使用
                EmoticonConfigureModel.getRecentEmoticons().apply {
                    if (isNotEmpty()) {
                        emoticonItem {
                            id(-2)
                            image(0)
                            title("最近使用")
                        }
                        forEach { item: Emoticon ->
                            emoticonItem {
                                id(item.tag)
                                image(item.icon)
                                title(item.name)
                                clickListener(View.OnClickListener {
                                    listener?.apply {
                                        onClick(item)
                                        EmoticonConfigureModel.addEmoticon(item)
                                    }
                                })
                                spanSizeOverride { _, _, _ ->
                                    1
                                }
                            }
                        }
                    }
                }
                // 所有表情
                emoticonItem {
                    id(-1)
                    image(0)
                    title("所有表情")
                }
                // 表情列表
                data.forEach { item: Emoticon ->
                    emoticonItem {
                        id(item.tag)
                        image(item.icon)
                        title(item.name)
                        clickListener(View.OnClickListener {
                            listener?.apply {
                                onClick(item)
                                EmoticonConfigureModel.addEmoticon(item)
                            }
                        })
                        spanSizeOverride { _, _, _ ->
                            1
                        }
                    }
                }
            }
        }
}

internal class EmoticonAdapter(
    val data: List<Emoticon>,
    val itemHeight: Int
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val emoticon = data[position]
        convertView?.let {
            val tag = it.tag
            if (tag is ViewHolder) {
                tag.icon.setImageResource(emoticon.icon)
                tag.textView.text = emoticon.name
                tag.textView.visibility =
                    if (emoticon.showName) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
            return convertView
        }

        val imageView = ImageView(parent?.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setImageResource(emoticon.icon)
//            background = context.resources.getDrawable(R.drawable.selector_emoticon_background)
        }
        val textView = TextView(parent?.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = emoticon.name
            gravity = Gravity.CENTER
            visibility =
                if (emoticon.showName) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
        return LinearLayout(parent?.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                itemHeight
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(imageView)
            addView(textView)
            tag = ViewHolder(textView, imageView)
        }
    }

    override fun getItem(position: Int) = data[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = data.size

    inner class ViewHolder(
        val textView: TextView,
        val icon: ImageView
    )
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class EmoticonItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val padding = ScreenUtil.dp2px(10f)
    private val titleView: TextView
    private val imageView: ImageView

    init {
        setPadding(padding)
        gravity = Gravity.CENTER
        orientation = VERTICAL
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        titleView = TextView(context).apply {
            layoutParams = params
            gravity = Gravity.LEFT
            visibility = View.GONE
            setTextColor(ContextCompat.getColor(context, R.color.basic_black_text))
            textSize = 14f
        }
        imageView = ImageView(context).apply {
            layoutParams = params
            visibility = View.GONE
        }
        addView(titleView)
        addView(imageView)
    }

    @TextProp
    fun setTitle(title: CharSequence) {
        titleView.visibility = if (title.isBlank()) View.GONE else View.VISIBLE
        titleView.text = title
        if (title.isNotBlank() && context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setPaddingHorizontal(padding * 2)
        } else {
            setPaddingHorizontal(padding)
        }
    }

    @ModelProp
    fun setImage(@DrawableRes resId: Int) {
        imageView.visibility = if (resId == 0) View.GONE else View.VISIBLE
        if (resId != 0) {
            imageView.setImageResource(resId)
        }
    }

    @CallbackProp
    fun setClickListener(clickListener: OnClickListener?) {
        setOnClickListener(clickListener)
    }
}

object EmoticonConfigureModel : BaseConfigureModel() {
    private const val TAG = "EmoticonViewPager"
    private const val RECENT_USED_EMOTICON = "recent_used_emoticon"

    fun hasRecentEmoticon() = getString(RECENT_USED_EMOTICON, "").isNotBlank()

    fun addEmoticon(e: Emoticon) {
        (getRecentEmoticons() as? LinkedList)
            ?.apply {
                if (contains(e)) {
                    remove(e)
                }
                addFirst(e)
                joinToString(
                    separator = ";",
                    prefix = "{",
                    postfix = "}",
                    limit = 8,
                    truncated = ""
                ) { emoticon ->
                    StringBuilder(emoticon.tag)
                        .append("|")
                        .append(emoticon.iconStr)
                        .append("|")
                        .append(emoticon.name)
                }
                    .let {
                        putString(RECENT_USED_EMOTICON, it)
                    }
            }
    }

    fun getRecentEmoticons(): List<Emoticon> {
        val s = getString(RECENT_USED_EMOTICON, "")
//        LogUtil.d(TAG, "s: $s");
        if (s.isBlank()) {
            return LinkedList()
        }
        val list = LinkedList<Emoticon>()
        try {
            // tag|iconStr|name
            s.substring(1, s.length - 1).split(";").forEach {
                if (it.isNotBlank()) {
                    it.split("|").let {
                        list.add(
                            Emoticon(
                                tag = it[0],
                                iconStr = it[1],
                                icon = getDrawableResId(it[1]),
                                name = it[2],
                                isEmoji = true
                            )
                        )
                    }
                }
            }
//            LogUtil.d(TAG, "list: $list");
        } catch (e: Exception) {
            LogUtil.e(TAG, "e: ${e.message}");
        }
        return list
    }

    private fun getDrawableResId(resName: String) =
        Utility.getApplication().resources.getIdentifier(
            resName,
            "drawable",
            Utility.getApplication().packageName
        )
}


