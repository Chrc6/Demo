package com.chrc.kotlindemo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chrc.kotlindemo.modle.UserRoomModel

/**
 *    @author : chrc
 *    date   : 2021/2/25  4:11 PM
 *    desc   :
 */
@Database(
        version = 1, exportSchema = false,
        entities = [UserRoomModel::class]
)
abstract class AppDataBase: RoomDatabase() {

    abstract fun getUserRoomModelDao(): UserRoomModelDao
}