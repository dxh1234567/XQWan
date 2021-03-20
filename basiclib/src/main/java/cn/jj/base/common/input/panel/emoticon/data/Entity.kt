package cn.jj.base.common.input.panel.emoticon.data

/**
 * 表情集合实体类
 */
data class EmoticonSet(
    val name: String = "",
    val icon: Int = 0,
    val numColumns: Int = 4,
    val numRows: Int = 2,
    val hasDelete: Boolean = false,
    val data: List<Emoticon> = mutableListOf()
) {

    //    private var countInOnePage: Int = numColumns * numRows - if (hasDelete) 1 else 0
    private var countInOnePage: Int = data.size

    private var pageCount: Int = Math.ceil(data.size.toDouble() / countInOnePage).toInt()

    fun getPageCount() = pageCount

    fun getEmoticons(pageIndex: Int): List<Emoticon> {
        val start = countInOnePage * pageIndex
        var end = countInOnePage * (pageIndex + 1)
        if (end > data.size) {
            end = data.size
        }
        return data.subList(start, end)
    }

}

/**
 * 表情
 */
data class Emoticon(
    val tag: String = "",
    val icon: Int = 0,
    val iconStr: String = "",
    val name: String = "",
    val showName: Boolean = false,
    val isEmoji: Boolean = false
)