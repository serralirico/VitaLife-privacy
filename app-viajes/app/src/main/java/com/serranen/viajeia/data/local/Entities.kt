package com.serranen.viajeia.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Un viaje: contenedor de itinerario, diario y gastos. */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val destination: String,
    val startDate: Long? = null,
    val endDate: Long? = null,
    /** Itinerario generado por la IA, guardado como texto Markdown. */
    val itinerary: String = "",
    val coverImageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

/** Una entrada de diario asociada a un viaje. */
@Entity(
    tableName = "diary_entries",
    indices = [Index("tripId")],
)
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val text: String,
    val photoUri: String? = null,
    val placeName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

/** Un gasto asociado a un viaje. */
@Entity(
    tableName = "expenses",
    indices = [Index("tripId")],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val title: String,
    val amount: Double,
    val currency: String = "EUR",
    val category: String = "General",
    val date: Long = System.currentTimeMillis(),
)
