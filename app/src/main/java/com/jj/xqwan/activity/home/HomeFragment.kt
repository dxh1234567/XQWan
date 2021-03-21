package com.jj.xqwan.activity.home

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.jj.xqwan.R
import com.jj.xqwan.activity.container.controller.HomePageListController
import com.jj.xqwan.activity.container.viewmodels.DataViewModelFactory
import com.jj.xqwan.activity.home.viewmodels.HomeViewModel
import com.jj.xqwan.base.BaseFragment
import com.jj.xqwan.base.net.app.DataRepository
import com.jj.xqwan.common.Constant
import com.jj.xqwan.view.TurnTableView
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */

class HomeFragment : BaseFragment(), TurnTableView.OnTurnTableResultListener,
    TurnTableView.OnTurnTableAnimEndListener {
    private lateinit var viewModel: HomeViewModel
    private lateinit var controller: HomePageListController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = DataViewModelFactory.getHomeViewModel(this)

    }

    override fun initLayout(): Int = R.layout.fragment_home

    override fun initView() {


        viewModel.apply {
            searchRefreshState.observe(viewLifecycleOwner, Observer {

                Log.e("---","dxh")
            })
            searchState.observe(this@HomeFragment, Observer { })
        }
        DataRepository.apiService.getHomeLabelGoodsDataList1(1,10,20)
        turn_table.canPlay = true
        turn_table.listener = this
        turn_table.onTurnTableAnimEndListener = this
        mParent.setContainerViewPage(mContainer)

        btn1.setOnClickListener {
            val result = ArrayList<Int>()
            val random = Random()
            for (item in 1..5) {
                result.add((random.nextInt(5) + 1))
            }
            my_dialect_open.resultArray = result

            turn_table.start(1,true)

//                .observe(viewLifecycleOwner,
//            Observer {
//                Log.e("---",it.toString())
//                val apiSuccessResponse = ApiSuccessResponse(it)
//
//
//                Log.e("---444",   apiSuccessResponse.data?.toString())
//
//                apiSuccessResponse.data.apply {
//
//                }
//                apiSuccessResponse.data
//                apiSuccessResponse.data.let {
//                }
//                Log.e("---",   apiSuccessResponse.data.toString())
//
//            })
//            val homeDataList = DataRepository.getHomeDataList(20)
        }

    }


    override fun lazyLoad() {
        showBaseContent()
        viewModel.setQuery(Constant.SEARCH_ALL)

    }

    override fun onTurnTableResult(position: Int) {

    }

    override fun onTurnTableAnimEnd(canPlay: Boolean) {

    }


}