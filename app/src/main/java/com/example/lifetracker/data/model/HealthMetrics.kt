package com.example.lifetracker.data.model

import kotlin.math.pow

data class HealthMetrics(
    val weight: Float = 68.2f,
    val height: Float = 180f,
    val bodyFat: Float = 18.5f,
    val waist: Float = 81.0f,
    val bicep: Float = 50.0f,
    val chest: Float = 157.0f,
    val thigh: Float = 200.0f,
    val shoulder: Float = 582.0f,
    val date: Long = System.currentTimeMillis()
) {
    val bmi: Float
        get() {
            val heightInMeters = height / 100
            return weight / heightInMeters.pow(2)
        }
}

data class HistoryEntry(
    val value: Float,
    val unit: String,
    val date: Long,
    val metricName: String = "",
    val weight: Float? = null,
    val height: Float? = null
)
