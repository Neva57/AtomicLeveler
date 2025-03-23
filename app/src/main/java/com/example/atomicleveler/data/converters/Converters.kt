package com.example.atomicleveler.data.converters

import androidx.room.TypeConverter

/**
 * Type converters for Room database
 */
class Converters {
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(",").mapNotNull { it.toLongOrNull() }
        }
    }
}