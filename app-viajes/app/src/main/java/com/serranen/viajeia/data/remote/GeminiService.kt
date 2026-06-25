package com.serranen.viajeia.data.remote

import com.serranen.viajeia.data.settings.AiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/** Un turno de la conversación con la IA. */
data class AiTurn(val fromUser: Boolean, val text: String)

/**
 * Cliente ligero de la API REST de Gemini.
 *
 * Funciona en dos modos, igual que VitaLife:
 *  - BYOK: el usuario pone su clave -> se envía como ?key=...
 *  - Proxy: el usuario apunta [AiConfig.baseUrl] a su servidor proxy
 *    (que añade la clave por su cuenta) y deja la clave vacía.
 */
class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    suspend fun generate(
        config: AiConfig,
        systemPrompt: String,
        history: List<AiTurn>,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val url = buildString {
                append(config.baseUrl.trimEnd('/'))
                append("/v1beta/models/")
                append(config.model)
                append(":generateContent")
                if (config.apiKey.isNotBlank()) append("?key=").append(config.apiKey)
            }

            val payload = GeminiRequest(
                systemInstruction = Content(parts = listOf(Part(systemPrompt))),
                contents = history.map { turn ->
                    Content(
                        role = if (turn.fromUser) "user" else "model",
                        parts = listOf(Part(turn.text)),
                    )
                },
            )

            val body = json.encodeToString(GeminiRequest.serializer(), payload)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder().url(url).post(body).build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    error("Gemini ${response.code}: ${raw.take(300)}")
                }
                val parsed = json.decodeFromString(GeminiResponse.serializer(), raw)
                parsed.candidates.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text
                    ?.trim()
                    ?: error("Respuesta vacía de Gemini")
            }
        }
    }
}

// --- DTOs de la API ---

@Serializable
private data class GeminiRequest(
    @SerialName("system_instruction") val systemInstruction: Content? = null,
    val contents: List<Content>,
)

@Serializable
private data class Content(
    val role: String? = null,
    val parts: List<Part>,
)

@Serializable
private data class Part(val text: String)

@Serializable
private data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
)

@Serializable
private data class Candidate(val content: Content? = null)
