package com.chrc.kotlindemo.modle

import java.io.Serializable

/**
 *    @author : chrc
 *    date   : 2021/1/18  11:06 AM
 *    desc   :
 */
class PreImageInfo: Serializable {

    var imageUrl: String? = null
    var x: Int = 0
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0
    var radius: Float = 0f;


    constructor(imageUrl: String?, x: Int, y: Int, width: Int, height: Int, radius: Float = 0f) {
        this.imageUrl = imageUrl
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.radius = radius
    }

}