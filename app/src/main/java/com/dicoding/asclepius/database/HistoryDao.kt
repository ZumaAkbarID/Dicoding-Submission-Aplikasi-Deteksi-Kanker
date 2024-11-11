package com.dicoding.asclepius.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Query("SELECT * from history ORDER BY id DESC")
    fun getAllHistory(): List<History>

    @Query("SELECT * from history WHERE id = :id")
    fun getHistory(id: Int): History
}