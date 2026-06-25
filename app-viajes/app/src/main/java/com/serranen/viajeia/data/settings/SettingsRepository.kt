package com.serranen.viajeia.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "viajeia_settings")

/** Configuración de la IA: clave propia (BYOK) o proxy, como en VitaLife. */
data class AiConfig(
    val apiKey: String = "",
    val baseUrl: String = DEFAULT_BASE_URL,
    val model: String = DEFAULT_MODEL,
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://generativelanguage.googleapis.com"
        const val DEFAULT_MODEL = "gemini-2.0-flash"
    }
}

class SettingsRepository(private val context: Context) {

    private object Keys {
        val API_KEY = stringPreferencesKey("api_key")
        val BASE_URL = stringPreferencesKey("base_url")
        val MODEL = stringPreferencesKey("model")
    }

    val aiConfig: Flow<AiConfig> = context.dataStore.data.map { prefs ->
        AiConfig(
            apiKey = prefs[Keys.API_KEY].orEmpty(),
            baseUrl = prefs[Keys.BASE_URL] ?: AiConfig.DEFAULT_BASE_URL,
            model = prefs[Keys.MODEL] ?: AiConfig.DEFAULT_MODEL,
        )
    }

    suspend fun save(config: AiConfig) {
        context.dataStore.edit { prefs ->
            prefs[Keys.API_KEY] = config.apiKey.trim()
            prefs[Keys.BASE_URL] = config.baseUrl.trim().ifBlank { AiConfig.DEFAULT_BASE_URL }
            prefs[Keys.MODEL] = config.model.trim().ifBlank { AiConfig.DEFAULT_MODEL }
        }
    }
}
