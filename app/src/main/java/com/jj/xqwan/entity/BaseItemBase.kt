package com.jj.xqwan.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.sql.Timestamp


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
interface BaseItemBase :Serializable{

    var uid :Long ?

    var createdTime : Timestamp?

    var updatedTime : Timestamp?


}