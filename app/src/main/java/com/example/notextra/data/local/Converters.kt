package com.example.notextra.data.local // Sesuaikan dengan nama package folder-mu

import androidx.room.TypeConverter
import com.example.notextra.domain.model.ColumnConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // --- Converter untuk List<ColumnConfig> ---
    @TypeConverter
    fun fromColumnConfigList(value: List<ColumnConfig>?): String {
        return gson.toJson(value ?: emptyList<ColumnConfig>())
    }

    @TypeConverter
    fun toColumnConfigList(value: String): List<ColumnConfig> {
        val listType = object : TypeToken<List<ColumnConfig>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Converter untuk Map<String, String> (Isi Data Custom) ---
    @TypeConverter
    fun fromDynamicDataMap(value: Map<String, String>?): String {
        return gson.toJson(value ?: emptyMap<String, String>())
    }

    @TypeConverter
    fun toDynamicDataMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return try {
            gson.fromJson(value, mapType) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}