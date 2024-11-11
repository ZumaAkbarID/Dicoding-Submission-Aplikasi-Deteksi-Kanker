package com.dicoding.asclepius.repository

import android.app.Application
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.database.HistoryDao
import com.dicoding.asclepius.database.HistoryRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository(application: Application) {
    private val historyDao: HistoryDao

    init {
        val db = HistoryRoomDatabase.getDatabase(application)
        historyDao = db.historyDao()
    }

    suspend fun getAllHistory(): List<History> = historyDao.getAllHistory()
    suspend fun getHistory(id: Int): History = historyDao.getHistory(id)

    suspend fun insert(history: History) {
        withContext(Dispatchers.IO) {
            historyDao.insert(history)
        }
    }
}