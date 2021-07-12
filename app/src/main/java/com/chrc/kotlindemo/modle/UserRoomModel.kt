package com.chrc.kotlindemo.modle

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 *    @author : chrc
 *    date   : 2021/2/25  11:44 AM
 *    desc   :
 */

@Entity(tableName = "user_room_model",
    primaryKeys = ["id"])
class UserRoomModel: Serializable {

    var id: Long = 0
    var name: String? = null

    constructor(id: Long, name: String?) {
        this.id = id
        this.name = name
    }
}