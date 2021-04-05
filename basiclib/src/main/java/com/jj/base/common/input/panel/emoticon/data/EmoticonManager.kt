package com.jj.base.common.input.panel.emoticon.data

import android.text.*
import android.widget.EditText
import android.widget.TextView
import com.jj.base.common.span.CenterAlignImageSpan
import com.jj.base.utils.Utility
import com.jj.basiclib.R
import java.util.regex.Pattern

object EmoticonManager {

    val sEmoticonSetList = mutableListOf<EmoticonSet>()

    val sDefault = mutableListOf<Emoticon>()
    val sDolphin = mutableListOf<Emoticon>()
    val sFarmer = mutableListOf<Emoticon>()
    val sLandlord = mutableListOf<Emoticon>()

    init {
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e004,
                iconStr = "e004",
                tag = "/偷笑/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e001,
                iconStr = "e001",
                tag = "/龇牙/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e005,
                iconStr = "e005",
                tag = "/憨笑/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e010,
                iconStr = "e010",
                tag = "/擦汗/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e016,
                iconStr = "e016",
                tag = "/玫瑰/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e011,
                iconStr = "e011",
                tag = "/流泪/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e007,
                iconStr = "e007",
                tag = "/流汗/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e048,
                iconStr = "e048",
                tag = "/强/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e003,
                iconStr = "e003",
                tag = "/阴险/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e009,
                iconStr = "e009",
                tag = "/敲打/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e000,
                iconStr = "e000",
                tag = "/微笑/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e002,
                iconStr = "e002",
                tag = "/调皮/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e008,
                iconStr = "e008",
                tag = "/再见/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e012,
                iconStr = "e012",
                tag = "/疯了/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e006,
                iconStr = "e006",
                tag = "/酷/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e052,
                iconStr = "e052",
                tag = "/抱拳/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e066,
                iconStr = "e066",
                tag = "/鼓掌/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e030,
                iconStr = "e030",
                tag = "/白眼/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e020,
                iconStr = "e020",
                tag = "/色/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e013,
                iconStr = "e013",
                tag = "/嘘/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e029,
                iconStr = "e029",
                tag = "/嘴唇/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e027,
                iconStr = "e027",
                tag = "/冷汗/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e026,
                iconStr = "e026",
                tag = "/惊恐/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e034,
                iconStr = "e034",
                tag = "/疑问/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e014,
                iconStr = "e014",
                tag = "/委屈/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e025,
                iconStr = "e025",
                tag = "/尴尬/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e042,
                iconStr = "e042",
                tag = "/抱抱/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e028,
                iconStr = "e028",
                tag = "/爱心/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e019,
                iconStr = "e019",
                tag = "/愉快/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e050,
                iconStr = "e050",
                tag = "/握手/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e017,
                iconStr = "e017",
                tag = "/大哭/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e043,
                iconStr = "e043",
                tag = "/坏笑/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e024,
                iconStr = "e024",
                tag = "/发怒/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e015,
                iconStr = "e015",
                tag = "/猪头/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e021,
                iconStr = "e021",
                tag = "/害羞/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e031,
                iconStr = "e031",
                tag = "/傲慢/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e057,
                iconStr = "e057",
                tag = "/咖啡/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e022,
                iconStr = "e022",
                tag = "/得意/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e051,
                iconStr = "e051",
                tag = "/胜利/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e056,
                iconStr = "e056",
                tag = "/好的/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e033,
                iconStr = "e033",
                tag = "/惊讶/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e023,
                iconStr = "e023",
                tag = "/吐/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e065,
                iconStr = "e065",
                tag = "/抠鼻/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e018,
                iconStr = "e018",
                tag = "/便便/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e036,
                iconStr = "e036",
                tag = "/亲亲/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e040,
                iconStr = "e040",
                tag = "/发呆/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e041,
                iconStr = "e041",
                tag = "/右哼哼/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e046,
                iconStr = "e046",
                tag = "/悠闲/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e059,
                iconStr = "e059",
                tag = "/拳头/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e045,
                iconStr = "e045",
                tag = "/晕了/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e053,
                iconStr = "e053",
                tag = "/凋谢/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e037,
                iconStr = "e037",
                tag = "/衰/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e071,
                iconStr = "e071",
                tag = "/闭嘴/"
            )
        )
        sDefault.add(
            Emoticon(
                isEmoji = true,
                icon = R.drawable.e068,
                iconStr = "e068",
                tag = "/左哼哼/"
            )
        )
        //        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e044,
//                iconStr = "e044",
//                tag = "/鄙视/"
//            )
//        )
        //        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e038,
//                iconStr = "e038",
//                tag = "/憋嘴/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e039,
//                iconStr = "e039",
//                tag = "/奋斗/"
//            )
//        )
        //        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e047,
//                iconStr = "e047",
//                tag = "/可怜/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e049,
//                iconStr = "e049",
//                tag = "/弱/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e069,
//                iconStr = "e069",
//                tag = "/哈欠/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e070,
//                iconStr = "e070",
//                tag = "/快哭了/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e032,
//                iconStr = "e032",
//                tag = "/难过/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e035,
//                iconStr = "e035",
//                tag = "/睡/"
//            )
//        )
        //        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e060,
//                iconStr = "e060",
//                tag = "/心碎/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e061,
//                iconStr = "e061",
//                tag = "/礼物/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e062,
//                iconStr = "e062",
//                tag = "/饿了/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e063,
//                iconStr = "e063",
//                tag = "/困/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e064,
//                iconStr = "e064",
//                tag = "/咒骂/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e067,
//                iconStr = "e067",
//                tag = "/糗大了/"
//            )
//        )
        //        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e054,
//                iconStr = "e054",
//                tag = "/饭/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e055,
//                iconStr = "e055",
//                tag = "/蛋糕/"
//            )
//        )
//        sDefault.add(
//            Emoticon(
//                isEmoji = true,
//                icon = R.drawable.e058,
//                iconStr = "e058",
//                tag = "/刀/"
//            )
//        )

//        sDolphin.add(Emoticon(icon = R.drawable.ff000, tag = "/ff000/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff001, tag = "/ff001/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff002, tag = "/ff002/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff003, tag = "/ff003/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff004, tag = "/ff004/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff005, tag = "/ff005/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff006, tag = "/ff006/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff007, tag = "/ff007/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff008, tag = "/ff008/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff009, tag = "/ff009/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff010, tag = "/ff010/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff011, tag = "/ff011/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff012, tag = "/ff012/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff013, tag = "/ff013/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff014, tag = "/ff014/"))
//        sDolphin.add(Emoticon(icon = R.drawable.ff015, tag = "/ff015/"))
//
//        sFarmer.add(Emoticon(icon = R.drawable.nn000, tag = "/nn000/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn001, tag = "/nn001/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn002, tag = "/nn002/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn003, tag = "/nn003/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn004, tag = "/nn004/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn005, tag = "/nn005/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn006, tag = "/nn006/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn007, tag = "/nn007/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn008, tag = "/nn008/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn009, tag = "/nn009/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn010, tag = "/nn010/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn011, tag = "/nn011/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn012, tag = "/nn012/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn013, tag = "/nn013/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn014, tag = "/nn014/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn015, tag = "/nn015/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn016, tag = "/nn016/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn017, tag = "/nn017/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn018, tag = "/nn018/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn019, tag = "/nn019/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn020, tag = "/nn020/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn021, tag = "/nn021/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn022, tag = "/nn022/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn023, tag = "/nn023/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn024, tag = "/nn024/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn025, tag = "/nn025/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn026, tag = "/nn026/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn027, tag = "/nn027/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn028, tag = "/nn028/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn029, tag = "/nn029/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn030, tag = "/nn030/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn031, tag = "/nn031/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn032, tag = "/nn032/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn033, tag = "/nn033/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn034, tag = "/nn034/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn035, tag = "/nn035/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn036, tag = "/nn036/"))
//        sFarmer.add(Emoticon(icon = R.drawable.nn037, tag = "/nn037/"))
//
//        sLandlord.add(Emoticon(icon = R.drawable.gg000, tag = "/gg000/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg001, tag = "/gg001/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg002, tag = "/gg002/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg003, tag = "/gg003/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg004, tag = "/gg004/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg005, tag = "/gg005/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg006, tag = "/gg006/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg007, tag = "/gg007/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg008, tag = "/gg008/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg009, tag = "/gg009/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg010, tag = "/gg010/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg011, tag = "/gg011/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg012, tag = "/gg012/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg013, tag = "/gg013/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg014, tag = "/gg014/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg015, tag = "/gg015/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg016, tag = "/gg016/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg017, tag = "/gg017/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg018, tag = "/gg018/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg019, tag = "/gg019/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg020, tag = "/gg020/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg021, tag = "/gg021/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg022, tag = "/gg022/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg023, tag = "/gg023/"))
//        sLandlord.add(Emoticon(icon = R.drawable.gg024, tag = "/gg024/"))

        sEmoticonSetList.add(
            EmoticonSet(
                icon = R.drawable.e000, numColumns = 8,
                numRows = 3, hasDelete = false, data = sDefault
            )
        )
//        sEmoticonSetList.add(
//                EmoticonSet(
//                        icon = R.drawable.emotion_dolphin, data = sDolphin
//                )
//        )
//        sEmoticonSetList.add(
//                EmoticonSet(
//                        icon = R.drawable.emotion_landlord, data = sLandlord
//                )
//        )
//        sEmoticonSetList.add(
//                EmoticonSet(
//                        icon = R.drawable.emotion_farmer, data = sFarmer
//                )
//        )
    }

    val XHS_RANGE = Pattern.compile("/[a-zA-Z0-9\\u4e00-\\u9fa5]+/")

    fun filterEmoticon(text: String): Int {
        val matcher = XHS_RANGE.matcher(text)
        find@ while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val sub = text.substring(start, end)

            for ((tag, icon) in EmoticonManager.sDolphin) {
                if (tag == sub) {
                    return icon
                }
            }

            for ((tag, icon) in EmoticonManager.sFarmer) {
                if (tag == sub) {
                    return icon
                }
            }

            for ((tag, icon) in EmoticonManager.sLandlord) {
                if (tag == sub) {
                    return icon
                }
            }
        }

        return 0
    }

    fun filterEmoticon(textView: TextView, text: SpannableStringBuilder) {
        textView.text = filterEmoticon(text, textView.paint.fontMetricsInt.let {
            it.descent - it.ascent
        })
    }

    fun filterEmoticon(textView: TextView, text: SpannableString) {
        textView.text = filterEmoticon(text, textView.paint.fontMetricsInt.let {
            it.descent - it.ascent
        })
    }

    fun filterEmoticon(textView: TextView, text: String) {
        textView.text = filterEmoticon(text, textView.textSize.toInt())
    }

    fun filterEmoticon(text: String, size: Int): SpannableString {
        val t = TextPaint().apply {
            textSize = size.toFloat()
        }
        return filterEmoticon(SpannableString(text), t.fontMetricsInt.let {
            it.descent - it.ascent
        })
    }

    fun filterEmoticon(text: SpannableString, size: Int): SpannableString {
        val matcher = XHS_RANGE.matcher(text)
        find@ while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val sub = text.substring(start, end)

            for ((tag, icon) in EmoticonManager.sDefault) {
                if (tag == sub) {
                    val drawable =
                        Utility.getApplication().applicationContext.resources.getDrawable(icon)
                    if (drawable != null) {
                        drawable.setBounds(0, 0, size, size)
                        val span =
                            CenterAlignImageSpan(
                                drawable,
                                CenterAlignImageSpan.ALIGN_FONTCENTER
                            )
                        text.setSpan(
                            span,
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    continue@find
                }
            }

        }
        return text
    }

    fun filterEmoticon(text: SpannableStringBuilder, size: Int): SpannableStringBuilder {
        val matcher = XHS_RANGE.matcher(text)
        find@ while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val sub = text.substring(start, end)

            for ((tag, icon) in EmoticonManager.sDefault) {
                if (tag == sub) {
                    val drawable =
                        Utility.getApplication().applicationContext.resources.getDrawable(icon)
                    if (drawable != null) {
                        drawable.setBounds(0, 0, size, size)
                        val span =
                            CenterAlignImageSpan(
                                drawable,
                                CenterAlignImageSpan.ALIGN_FONTCENTER
                            )
                        text.setSpan(
                            span,
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    continue@find
                }
            }

        }
        return text
    }

    fun replaceEmoticons(editText: EditText, editable: Editable, start: Int, count: Int) {

        val text = editable.toString()
        val matcher = XHS_RANGE.matcher(text)
        find@ while (matcher.find()) {
            val from = matcher.start()
            val to = matcher.end()
            val sub = text.substring(matcher.start(), matcher.end())

            for ((tag, icon) in EmoticonManager.sDefault) {
                if (tag == sub) {
                    val drawable = editText.context.resources.getDrawable(icon)
                    if (drawable != null) {
                        drawable.setBounds(0, 0, editText.lineHeight, editText.lineHeight)
                        val span =
                            CenterAlignImageSpan(
                                drawable,
                                CenterAlignImageSpan.ALIGN_FONTCENTER
                            )
                        editable.setSpan(span, from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    continue@find
                }
            }

        }

    }
}
