package com.jj.xqwan.entity

import java.sql.Timestamp


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
data class GoodsItemInfo(
    override var uid: Long?,
    override var createdTime: Timestamp?,
    override var updatedTime: Timestamp?
) :BaseItemBase {



}