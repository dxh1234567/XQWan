package com.jj.base.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.jj.base.common.helper.AreaParserHelper
import com.jj.base.utils.Constant.BIRTH_DATE_SEPARATOR
import com.jj.base.utils.Constant.DEFAULT_BIRTH_DATE
import com.jj.base.utils.timeToString
import com.jj.basiclib.R

object CommonPickerDialog {

    fun createYearMonthPicker(
        context: Context,
        yearInt: String,
        monthInt: String,
        onDateSetListener: OnDateSetListener?
    ): CustomAlertDialog {
        val container = LayoutInflater.from(context)
            .inflate(R.layout.birth_date_picker_layout, null)
        val year =
            container.findViewById<WheelView>(R.id.first_wheel)
        val month =
            container.findViewById<WheelView>(R.id.second_wheel)
        container.findViewById<WheelView>(R.id.third_wheel).visibility = View.GONE

        //设置year数据
        year.setWheelItemList(
            WheelStyle.getItemList(
                WheelStyle.STYLE_YEAR
            )
        )
        //设置month数据
        month.setWheelItemList(
            WheelStyle.getItemList(
                WheelStyle.STYLE_MONTH,
                Integer.parseInt(monthInt)
            )
        )

        //设置选中item
        year.setSelectedItem(yearInt)
        month.setSelectedItem(monthInt)

        val listener =
            WheelView.OnValueChangeListener { wheelView, old, new ->
                //@note 正向改动更新WheelView数据，反向改动调整WheelView选中item
                when (wheelView) {
                    year -> {
                        // 修改year改变month
                        month.setWheelItemList(
                            WheelStyle.getItemList(
                                WheelStyle.STYLE_MONTH,
                                Integer.parseInt(year.selectedItemText)
                            )
                        )
                    }
                    month -> {
                        // 修改month改变year
                        updateSelectedItem(year, wheelView, old, new)
                    }
                }
            }

        year.setOnValueChangedListener(listener)
        month.setOnValueChangedListener(listener)
        return CustomAlertDialog.Builder(context)
            .setTitle(R.string.basic_year_month_picker_dialog_title)
            .setMessageView(container)
            .setNegativeButton(R.string.basic_cancel, null)
            .setPositiveButton(R.string.basic_sure) { _, _ ->
                onDateSetListener?.onDateSet(
                    container,
                    Integer.parseInt(year.selectedItemText),
                    Integer.parseInt(month.selectedItemText),
                    0
                )
            }
            .create()
    }

    fun createBirthDatePicker(
        context: Context,
        birthDate: Long,
        onDateSetListener: OnDateSetListener?
    ): CustomAlertDialog {
        val container = LayoutInflater.from(context)
            .inflate(R.layout.birth_date_picker_layout, null)
        val year =
            container.findViewById<WheelView>(R.id.first_wheel)
        val month =
            container.findViewById<WheelView>(R.id.second_wheel)
        val day =
            container.findViewById<WheelView>(R.id.third_wheel)

        var date =
            timeToString(birthDate, "yyyy-M-d")
                .split("-")
                .run {
                    if (size == 3) {
                        this
                    } else {
                        timeToString(DEFAULT_BIRTH_DATE, "yyyy-M-d")
                            .split("-")
                    }
                }
        //设置year数据
        year.setWheelItemList(
            WheelStyle.getItemList(
                WheelStyle.STYLE_YEAR
            )
        )
        //设置month数据
        month.setWheelItemList(
            WheelStyle.getItemList(
                WheelStyle.STYLE_MONTH,
                Integer.parseInt(date[0])
            )
        )
        //设置day数据
        day.setWheelItemList(
            WheelStyle.getItemList(
                WheelStyle.STYLE_DAY,
                Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[0])
            )
        )
        //设置选中item
        year.setSelectedItem(date[0])
        month.setSelectedItem(date[1])
        day.setSelectedItem(date[2])

        fun updateDayList() =
            day.setWheelItemList(
                WheelStyle.getItemList(
                    WheelStyle.STYLE_DAY,
                    month.selectedItem,
                    Integer.parseInt(year.selectedItemText)
                )
            )

        val listener =
            WheelView.OnValueChangeListener { wheelView, old, new ->
                //@note 正向改动更新WheelView数据，反向改动调整WheelView选中item
                when (wheelView) {
                    year -> {
                        // 修改year改变month
                        month.setWheelItemList(
                            WheelStyle.getItemList(
                                WheelStyle.STYLE_MONTH,
                                Integer.parseInt(year.selectedItemText)
                            )
                        )
                        // 修改year改变day
                        updateDayList()
                    }
                    month -> {
                        // 修改month改变day
                        updateDayList()
                        // 修改month改变year
                        updateSelectedItem(year, wheelView, old, new)
                    }
                    day -> updateSelectedItem(month, wheelView, old, new)  // 修改day改变month
                }
            }

        year.setOnValueChangedListener(listener)
        month.setOnValueChangedListener(listener)
        day.setOnValueChangedListener(listener)
        return CustomAlertDialog.Builder(context)
            .setTitle(R.string.basic_birth_date_picker_dialog_title)
            .setMessageView(container)
            .setNegativeButton(R.string.basic_cancel, null)
            .setPositiveButton(R.string.basic_sure) { _, _ ->
                onDateSetListener?.onDateSet(
                    container,
                    Integer.parseInt(year.selectedItemText),
                    Integer.parseInt(month.selectedItemText),
                    Integer.parseInt(day.selectedItemText)
                )
            }
            .create()
    }

    private fun updateSelectedItem(desView: WheelView, srcView: WheelView, old: Int, new: Int) {
        var desMax = desView.itemCount - 1
        var srcMax = srcView.itemCount - 1
        var origin = desView.selectedItem
        var temp = origin
        when {
            old == srcMax && new == 0 ->
                temp = run {
                    temp++
                    if (temp >= desMax + 1) {
                        temp = 0
                    }
                    temp
                }
            old == 0 && new == srcMax ->
                temp = run {
                    temp--
                    if (temp < 0) {
                        temp = desMax
                    }
                    temp
                }
        }
        if (temp != origin) {
            //反向改动需回调数据改变
            desView.setSelectedItem(temp, true)
        }
    }

    fun createAreaPicker(
        context: Context,
        areaData: String?,
        onAreaSetListener: OnAreaSetListener?
    ): CustomAlertDialog {
        AreaParserHelper.initData()
        val container = LayoutInflater.from(context)
            .inflate(R.layout.area_picker_layout, null)
        val province =
            container.findViewById<WheelView>(R.id.first_wheel)
                .apply {
                    setNotifyChangeWhenFling(false)
                }
        val city =
            container.findViewById<WheelView>(R.id.second_wheel)
                .apply {
                    setNotifyChangeWhenFling(false)
                }
        val area =
            container.findViewById<WheelView>(R.id.third_wheel)
                .apply {
                    setNotifyChangeWhenFling(false)
                }
        val provinceList = ArrayList<String>(AreaParserHelper.getAreaData().keys)
        fun getCityList(provinceIndex: Int) =
            ArrayList<String>(
                AreaParserHelper.getAreaData()[provinceList[provinceIndex]]
                    ?.keys!!
            )

        fun getAreaList(provinceIndex: Int, cityIndex: Int) =
            AreaParserHelper.getAreaData()[provinceList[provinceIndex]]
                ?.get(getCityList(provinceIndex)[cityIndex])!!

        val data = areaData
            ?.split(BIRTH_DATE_SEPARATOR)
            ?.run {
                if (size == 3) {
                    this
                } else {
                    List(3) { "" }
                }
            }
            ?: List(3) { "" }
        val provinceIndex = Math.max(0, provinceList.indexOf(data[0]))
        val cityList = getCityList(provinceIndex)
        val areaList = getAreaList(
            provinceIndex,
            Math.max(0, cityList.indexOf(data[1]))
        )
        //设置数据
        province.setWheelItemList(provinceList)
        city.setWheelItemList(cityList)
        area.setWheelItemList(areaList)
        //设置选中item
        province.setSelectedItem(data[0])
        city.setSelectedItem(data[1])
        area.setSelectedItem(data[2])

        val listener =
            WheelView.OnValueChangeListener { wheelView, _, new ->
                when (wheelView) {
                    province -> {
                        //修改province改变city
                        city.setWheelItemList(getCityList(new))
                        city.setSelectedItem(0) //默认选择第一项
                        //修改province改变area
                        area.setWheelItemList(getAreaList(new, 0))
                        area.setSelectedItem(0) //默认选择第一项
                    }
                    city -> {
                        //@note city数据依赖province数据,所以此处需要进行边界值约束处理
                        var cityIndex = Math.max(
                            0,
                            Math.min(getCityList(province.selectedItem).size - 1, new)
                        )
                        //修改city改变area
                        area.setWheelItemList(getAreaList(province.selectedItem, cityIndex))
                        area.setSelectedItem(0) //默认选择第一项
                    }
                }
            }
        province.setOnValueChangedListener(listener)
        city.setOnValueChangedListener(listener)

        return CustomAlertDialog.Builder(context)
            .setTitle(R.string.basic_area_picker_dialog_title)
            .setMessageView(container)
            .setNegativeButton(R.string.basic_cancel, null)
            .setPositiveButton(R.string.basic_sure) { _, _ ->
                onAreaSetListener?.onAreaSet(
                    container,
                    province.selectedItemText,
                    city.selectedItemText,
                    area.selectedItemText
                )
            }
            .create()
    }
}

interface OnDateSetListener {
    fun onDateSet(view: View, year: Int, month: Int, dayOfMonth: Int)
}

interface OnAreaSetListener {
    fun onAreaSet(view: View, province: String, city: String, area: String)
}
