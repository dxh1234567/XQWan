package com.jj.xqwan.entity

import com.jj.xqwan.constant.RoomConstant


/**
 *  Created By duXiaHui
 *  on 2021/3/21
*/


data class GuessGamePromotionInfo(
  var   resultInfo: GuessGameResultInfo ? = null,
  var punish :String  ="唱一首歌曲"

)

data class GuessGameResultInfo(
    var hostGuess : RoomConstant.GuessIngGageType = RoomConstant.GuessIngGageType.SHEAR,
    var userGuess : RoomConstant.GuessIngGageType = RoomConstant.GuessIngGageType.CLOTH,
    var moraResult : RoomConstant.GuessingGameResultType = RoomConstant.GuessingGameResultType.STREAMER_SUCCESS,
    var hostAvatar :String  ="",
    var userAvatar :String  ="",
    var hostNickName :String  ="",
    var userNickName :String  =""

)