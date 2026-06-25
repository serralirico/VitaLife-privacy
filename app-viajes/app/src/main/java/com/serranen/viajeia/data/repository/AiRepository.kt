package com.serranen.viajeia.data.repository

import com.serranen.viajeia.data.remote.AiTurn
import com.serranen.viajeia.data.remote.GeminiService
import com.serranen.viajeia.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first

/** Lógica de viajes sobre Gemini: planificador + guía conversacional. */
class AiRepository(
    private val gemini: GeminiService,
    private val settings: SettingsRepository,
) {
    /** Genera un itinerario en Markdown para un destino. */
    suspend fun planItinerary(
        destination: String,
        days: Int,
        budget: String,
        interests: String,
    ): Result<String> {
        val config = settings.aiConfig.first()
        val system = """
            Eres un experto planificador de viajes. Devuelve un itinerario claro
            en Markdown, día a día (## Día 1, ## Día 2...), con mañana/tarde/noche,
            lugares concretos, una estimación de coste y un consejo local por día.
            Responde en español. No incluyas texto introductorio innecesario.
        """.trimIndent()

        val prompt = buildString {
            append("Destino: ").append(destination).append('\n')
            append("Duración: ").append(days).append(" días\n")
            if (budget.isNotBlank()) append("Presupuesto: ").append(budget).append('\n')
            if (interests.isNotBlank()) append("Intereses: ").append(interests).append('\n')
        }

        return gemini.generate(config, system, listOf(AiTurn(fromUser = true, text = prompt)))
    }

    /** Continúa una conversación de guía de viajes. */
    suspend fun chat(history: List<AiTurn>): Result<String> {
        val config = settings.aiConfig.first()
        val system = """
            Eres "Globo", una guía de viajes amable y práctica. Das recomendaciones
            concretas (qué ver, dónde comer, cómo moverse, seguridad, frases útiles).
            Sé conciso y útil. Responde en el idioma del usuario.
        """.trimIndent()
        return gemini.generate(config, system, history)
    }
}
