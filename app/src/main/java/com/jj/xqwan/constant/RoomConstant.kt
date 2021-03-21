package com.jj.xqwan.constant


/**
 *  Created By duXiaHui
 *  on 2021/3/21
 */
object RoomConstant {

    enum class GuessIngGageType(value:Int){
        STONE(1),
        SHEAR(2),
        CLOTH(3)
    }

    enum class GuessingGameResultType{

        NONE(),
        TIE(),
        STREAMER_SUCCESS()

    }
}