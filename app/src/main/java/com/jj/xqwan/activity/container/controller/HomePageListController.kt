package com.jj.xqwan.activity.container.controller

import androidx.constraintlayout.widget.Placeholder
import cn.jj.base.common.ThreadManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.jj.xqwan.LayoutPlachoderBindingModel_
import com.jj.xqwan.activity.home.epmodels.ShowRoomItemEpoxyHolder
import com.jj.xqwan.activity.home.epmodels.ShowRoomItemEpoxyHolder_
import com.jj.xqwan.databinding.EpoxyLayoutPlachoderBinding
import com.jj.xqwan.databinding.ShowRoomHolderItemDatabinding2Binding
import com.jj.xqwan.entity.HomeItemInfo
import com.jj.xqwan.entity.HomeItemType


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */

class HomePageListController():
    PagedListEpoxyController<HomeItemInfo>(
        ThreadManager.getHandler(ThreadManager.THREAD_UI),
        ThreadManager.getHandler(ThreadManager.THREAD_WORKER)
    ) {
    override fun buildItemModel(currentPosition: Int, item: HomeItemInfo?): EpoxyModel<*> {

        return when (item!!.type) {
            HomeItemType.HOME_ITEM_TYPE_LIVE -> {
                ShowRoomItemEpoxyHolder_()
                    .id("room ${item.uid}")
                    .imgUrl(item.minPic)
                    .title(item.name)


            }
            else -> {

                LayoutPlachoderBindingModel_()
                    .id("room ${item.uid}")
                    .msg("")
            }
        }


    }


}