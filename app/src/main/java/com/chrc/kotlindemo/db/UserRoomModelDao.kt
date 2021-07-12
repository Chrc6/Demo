package com.chrc.kotlindemo.db

import androidx.room.*
import com.chrc.kotlindemo.modle.UserRoomModel

/**
 *    @author : chrc
 *    date   : 2021/2/25  4:07 PM
 *    desc   :
 */
@Dao
interface UserRoomModelDao {

    @Query("SELECT * FROM user_room_model WHERE name = :name")
    fun getByName(name: String?): UserRoomModel?

    @Query("SELECT * FROM user_room_model")
    fun getAll(): List<UserRoomModel>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    @Insert()
    fun insert(userRoomModel: UserRoomModel)

    @Query("DELETE FROM user_room_model")
    fun deleteAll()

    @Delete
    fun delete(userRoomModel: UserRoomModel)
}