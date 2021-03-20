package com.jj.xqwan.activity.home.epmodels

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import cn.jj.base.common.glide.GlideApp
import cn.jj.base.common.helper.KotlinEpoxyHolder
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.jj.xqwan.R


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */
@EpoxyModelClass(layout = R.layout.show_room_holder_item_databinding_2)
 abstract  class  ShowRoomItemEpoxyHolder : EpoxyModelWithHolder<ShowRoomItemEpoxyHolder.Holder>() {

    @EpoxyAttribute
    var imgUrl: String? = ""

    @EpoxyAttribute
    var title: CharSequence = ""

    override fun bind(holder: Holder) {
        if (!TextUtils.isEmpty(imgUrl)) {
            holder.iconView.tag = null
            GlideApp.with(holder.iconView)
                .load(imgUrl)
//                .placeholder(R.drawable.common_room_placeholder_icon)
//                .error(R.drawable.common_room_placeholder_icon)
                .transforms(CenterCrop())
                .into(holder.iconView)
        }
        holder.titleView.text = title
    }

    class Holder : KotlinEpoxyHolder(){
        val iconView by bind<ImageView>(R.id.icon_ib)
        val titleView by bind<TextView>(R.id.title_tv)
    }
}