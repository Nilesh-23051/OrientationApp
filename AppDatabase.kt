package com.hello.orientationapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrientationData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orientationDao(): OrientationDao
}