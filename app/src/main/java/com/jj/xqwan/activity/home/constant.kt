package com.jj.xqwan.activity.home


import com.jj.xqwan.R


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */


const val TAG_USER_PAGE_TYPE = "pageType"
const val TAG_PAGE_BUNDLE = "pageBundle"   //传入到某个页面的参数


fun getDestId(pageType: SettingFragmentType) =

    when(pageType){
        SettingFragmentType.USER_PAGE_TYPE_MINE -> R.id.home_fragment
        else -> throw IllegalArgumentException("不存在页面类型:$pageType")

    }