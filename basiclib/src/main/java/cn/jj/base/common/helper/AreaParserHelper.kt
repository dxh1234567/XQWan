package cn.jj.base.common.helper

import cn.jj.base.utils.Constant.AREA_DATA_FILE_NAME
import cn.jj.base.utils.Utility
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wangym on 2018/12/17
 */
internal object AreaParserHelper {
    private val areaData = LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>()
    fun getAreaData(): Map<String, Map<String, List<String>>> = Collections.unmodifiableMap(areaData)

    data class Area(
            @SerializedName("name")
            val name: String
    )

    data class City(
            @SerializedName("name")
            val name: String,
            @SerializedName("children")
            val area: ArrayList<Area> = ArrayList())

    data class Province(
            @SerializedName("name")
            val name: String,
            @SerializedName("children")
            val city: ArrayList<City> = ArrayList())

    data class AreaData(
            @SerializedName("data")
            val data: ArrayList<Province> = ArrayList())

    fun initData() {
        if (areaData.isEmpty()) {
            val data = Gson().fromJson(
                    InputStreamReader(Utility.getApplication().assets.open(AREA_DATA_FILE_NAME)),
                    AreaData::class.java)
            data?.data?.apply {
                forEach { province ->
                    province.city.apply {
                        val cityMap = LinkedHashMap<String, ArrayList<String>>()
                        forEach { city ->
                            city.area.apply {
                                val areaList = ArrayList<String>()
                                forEach { area ->
                                    area.apply {
                                        areaList.add(area.name)
                                    }
                                }
                                cityMap[city.name] = areaList
                            }
                        }
                        areaData[province.name] = cityMap
                    }
                }
            }
        }
    }
}