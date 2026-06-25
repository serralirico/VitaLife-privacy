package com.serranen.viajeia.di

import android.content.Context
import com.serranen.viajeia.data.local.AppDatabase
import com.serranen.viajeia.data.remote.GeminiService
import com.serranen.viajeia.data.repository.AiRepository
import com.serranen.viajeia.data.repository.TripRepository
import com.serranen.viajeia.data.settings.SettingsRepository

/**
 * Inyección de dependencias manual y sencilla. Se crea una vez en la
 * Application y se reutiliza en toda la app (sin librerías extra).
 */
class AppContainer(context: Context) {
    private val db = AppDatabase.get(context)

    val settingsRepository = SettingsRepository(context)
    val tripRepository = TripRepository(db.tripDao(), db.diaryDao(), db.expenseDao())
    val aiRepository = AiRepository(GeminiService(), settingsRepository)
}
