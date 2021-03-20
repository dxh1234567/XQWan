package com.jj.xqwan.base.net


/**
 *  Created By duXiaHui
 *  on 2021/1/24
 */


import java.io.IOException

const val NET_CODE_SUCCESS = 0


val TOKEN_ERROR_CODE = listOf(
    ErrCode.ERR_TOKEN_INVALID,
    ErrCode.ERR_TOKEN_ERROR,
    ErrCode.ERR_TOKEN_OUTTIME,
    ErrCode.ERR_TOKEN_NOT_EXIST,
    ErrCode.ERR_TOKEN_CANNOT_USE
)

object ErrCode {
    const val ERR_OK = 200
    const val ERR_NETWORK_TIMEOUT = 1  // 网络异常
    const val ERR_TOAST = 10006  // 通用的、弹错误消息
    const val ERR_START_LIVE = 20001  // 主播开播逻辑异常
    const val ERR_STOP_LIVE = 20002  // 主播下播逻辑异常
    const val ERR_ROOMINFO_NOT_EXIST = 20003  // 房间信息不存在
    const val ERR_HOST_PERMISSION_ERROR = 20004  // 权限错误 用户不是主播
    const val ERR_HOT_RANK_NOT_EXIST = 20005  // 热度排行不存在
    const val ERR_LIVE_URL_NOT_EXIST = 20006  // 直播流信息不存在
    const val ERR_USER_NOT_EXIST = 20007  // 用户不存在
    const val ERR_IN_OTHER_BLACKLIST = 20008  // 由于对方设置，暂无法关注
    const val ERR_IN_MY_BLACKLIST = 20009  // 对方在您的黑名单中，关注的同时会将对方移出黑名单
    const val ERR_USER_IS_FORBIDEN = 20013  // 用户账户已被封禁
    const val ERR_EDIT_NAME_INVALID = 20015  // 用户昵称非法
    const val ERR_EDIT_NAME_NOT_ENABLE = 20016  // 每30天只能修改一次昵称
    const val ERR_EDIT_NAME_EXIST = 20017  // 用户昵称已存在
    const val ERR_SIGN_ERROR = 20018  // 用户签名内容非法，包含敏感词
    const val ERR_EDIT_NAME_ERROR = 20019  // 用户昵称非法，包含敏感词
    const val ERR_SEND_FANS_GIFT_UNLOCK = 20022  // 粉絲禮物未解鎖
    const val ERR_DEVICE_INFO_INVALID = 30001  // 设备信息错误
    const val ERR_TOKEN_INVALID = 30002  // token不可用
    const val ERR_TOKEN_ERROR = 30003  // token错误
    const val ERR_TOKEN_OUTTIME = 30004  // token过期
    const val ERR_TOKEN_NOT_EXIST = 30005  // token不存在
    const val ERR_TOKEN_CANNOT_USE = 30006  // token失效 请重新登录
    const val ERR_JJPAY_ERROR = 30007  // 发起JJ支付错误
    const val ERR_ORDER_LIMIT = 30008  // 创建订单数量已达当日限制
    const val ERR_CREATE_ORDER = 30009  // 创建订单错误
    const val ERR_ORDER_ID_INVALID = 30010  // 订单号错误
    const val ERR_CREATE_PREPAY = 30011  // 发起预支付错误
    const val ERR_RECHARGE = 30012  // 充值错误
    const val ERR_SEND_GIFT = 30013  // 发送礼物错误
    const val ERR_ADD_GIFT = 30014  // 礼物购买错误
    const val ERR_COMMODITY_INSTANCE = 30015  // 商品实例错误
    const val ERR_DELIVERY = 30016  // 商品发货失败
    const val ERR_ORDER_STATUS = 30017  // 设置订单状态错误
    const val ERR_MQTT = 30018  // 长连接报错
    const val ERR_GET_USERINFO = 30019  // 获取用户信息错误
    const val ERR_NOT_IN_ROOM = 30020  // 没有在此房间内
    const val ERR_CHAT_LIMIT = 30021  // 聊天次数限制
    const val ERR_CHAT_MUTE = 30022  // 聊天禁言中
    const val ERR_HOST_LOGIN = 30023  // 主播登录错误
    const val ERR_CONSUME_ERR = 30024  // 消耗错误，余额不足
    const val ERR_WORLD_MSG = 30025  // 世界消息错误
    const val ERR_SET_MANAGER = 30026  // 设置管理员错误
    const val ERR_KICKOUT = 30027  // 设置踢人错误
    const val ERR_FORBID_SPEAK = 30028  // 设置禁言错误
    const val ERR_ROOM_KICK = 30029  // 被踢出房间
    const val ERR_ACCOUNT_BAN = 30030  // 账号被封停
    const val ERR_ADMIN_ACTION = 30031  // 管理操作错误
    const val ERR_BARRAGE_MSG = 30032  // 弹幕消息错误
    const val ERR_JOIN_ROOM = 30034  // 加入房间错误
    const val ERR_CHAT_AUTH = 30035  // 发言权限限制
    const val ERR_CHAT_LENGTH = 30036  // 发言长度限制
    const val ERR_CHAT_LOW_LEVEL = 30045  // 当前房间禁止财富/用户等级N级以下发言
    const val ERR_FORBID_CHAT = 30046  // 房间当前禁止：发言
    const val ERR_FORBID_SEND_GIFT = 30047  // 当前房间禁止：送礼
    const val ERR_WEALTH_LEVEL_NOT_ALLOWED_SEND_GIFT = 20027  // 该礼物暂未解锁
    const val ERR_REAL_NAME = 30040  // 实名认证错误
    const val ERR_SYSTEM = 40001  // 系统错误
    const val ERR_INVALID_REQUEST = 40002  // 无效的请求
    const val ERR_INVALID_PARAM = 40003  // 无效的参数
    const val ERR_INVALID_TOKEN = 40004  // 无效的token
    const val ERR_VIDEO_NOT_EXIST = 40300  // 视频不存在
    const val ERR_INSUFFICIENT_POPCORN = 50003  // 爆米花数量不足
    const val ERV_INSUFFICIENT_GOLD_COINS = 50004  // 金币数量不足
}


data class NetworkException(
    val errCode: Int,
    val errMsg: String
) : IOException(errMsg)