package com.chrc.kotlindemo.modle

import java.io.Serializable

/**
 *    @author : chrc
 *    date   : 2021/1/21  3:28 PM
 *    desc   :
 */
class PreImageInfoResult: Serializable {

    var position: Int = 0
    var preImageInfos: MutableList<PreImageInfo> = ArrayList()

    constructor(position: Int, preImageInfos: List<PreImageInfo>) {
        this.position = position
        this.preImageInfos.addAll(preImageInfos)
    }
}