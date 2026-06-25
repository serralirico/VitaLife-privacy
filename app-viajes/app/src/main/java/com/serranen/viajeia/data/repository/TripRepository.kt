package com.serranen.viajeia.data.repository

import com.serranen.viajeia.data.local.DiaryDao
import com.serranen.viajeia.data.local.DiaryEntryEntity
import com.serranen.viajeia.data.local.ExpenseDao
import com.serranen.viajeia.data.local.ExpenseEntity
import com.serranen.viajeia.data.local.TripDao
import com.serranen.viajeia.data.local.TripEntity
import kotlinx.coroutines.flow.Flow

class TripRepository(
    private val tripDao: TripDao,
    private val diaryDao: DiaryDao,
    private val expenseDao: ExpenseDao,
) {
    fun trips(): Flow<List<TripEntity>> = tripDao.observeAll()
    fun trip(id: Long): Flow<TripEntity?> = tripDao.observeById(id)
    suspend fun saveTrip(trip: TripEntity): Long = tripDao.insert(trip)
    suspend fun updateTrip(trip: TripEntity) = tripDao.update(trip)
    suspend fun deleteTrip(trip: TripEntity) = tripDao.delete(trip)

    fun diary(tripId: Long): Flow<List<DiaryEntryEntity>> = diaryDao.observeByTrip(tripId)
    suspend fun addDiary(entry: DiaryEntryEntity) = diaryDao.insert(entry)
    suspend fun deleteDiary(entry: DiaryEntryEntity) = diaryDao.delete(entry)

    fun expenses(tripId: Long): Flow<List<ExpenseEntity>> = expenseDao.observeByTrip(tripId)
    fun expenseTotal(tripId: Long): Flow<Double> = expenseDao.observeTotal(tripId)
    suspend fun addExpense(expense: ExpenseEntity) = expenseDao.insert(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.delete(expense)
}
