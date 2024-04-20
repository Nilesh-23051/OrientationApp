package com.hello.orientationapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrientationDao {
    @Insert
    suspend fun insert(orientationData: OrientationData)

    @Query("SELECT * FROM orientation_data ORDER BY timestamp DESC")
    fun getAll(): List<OrientationData>
}