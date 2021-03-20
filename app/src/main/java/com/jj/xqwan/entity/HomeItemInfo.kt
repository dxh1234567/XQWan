package com.jj.xqwan.entity

import com.google.gson.annotations.SerializedName


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */

enum class HomeItemType(val value: Int) {
    @SerializedName("3")
    HOME_ITEM_TYPE_LIVE(3),

    @SerializedName("1")
    HOME_ITEM_TYPE_BANNER(1),

    @SerializedName("2")
    HOME_ITEM_TYPE_VIDEO(2),
    HOME_ITEM_TYPE_BANNER_GROUP(-1),
    HOME_ITEM_TYPE_SPACE(-2),  //占空位使用，便于隔离游戏和秀场房间
    HOME_ITEM_TYPE_MORE(-3),  //更多
    HOME_ITEM_TYPE_CENTER(-4),  //金刚位
}

data class HomeItemInfo(

    @SerializedName(value = "id", alternate = ["roomId"])
    var type : HomeItemType = HomeItemType.HOME_ITEM_TYPE_LIVE,
    var uid :Int = 0,
    var name :String = "",
    var minPic :String =""
) {
}