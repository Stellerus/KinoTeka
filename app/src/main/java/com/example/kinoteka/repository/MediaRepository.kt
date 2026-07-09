package com.example.kinoteka.repository

import android.content.Context
import com.example.kinoteka.model.MediaItem
import com.example.kinoteka.model.Movie
import com.example.kinoteka.model.Series
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

object MediaRepository {
    private const val FILE_NAME = "kinoteka.json"
    private val gson = Gson()
    private var items: MutableList<MediaItem> = mutableListOf()

    fun load(context: Context): List<MediaItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            items = mutableListOf()
            return items
        }
        return try {
            val json = file.readText()
            val type = object : TypeToken<List<Any>>() {}.type
            val rawList: List<Any> = gson.fromJson(json, type)
            items = rawList.mapNotNull { raw ->
                val map = raw as? Map<*, *> ?: return@mapNotNull null
                when (map["type"]) {
                    "movie" -> gson.fromJson(gson.toJsonTree(raw), Movie::class.java)
                    "series" -> gson.fromJson(gson.toJsonTree(raw), Series::class.java)
                    else -> null
                }
            }.toMutableList()
            items.toList()
        } catch (e: Exception) {
            items = mutableListOf()
            items.toList()
        }
    }

    fun getAll(): List<MediaItem> = items.toList()

    fun getById(id: String): MediaItem? = items.find { it.id == id }

    fun add(item: MediaItem) {
        items.add(item)
    }

    fun update(updatedItem: MediaItem) {
        val index = items.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            items[index] = updatedItem
        }
    }

    fun delete(id: String) {
        items.removeAll { it.id == id }
    }

    fun save(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        val enrichedList = items.map { item ->
            when (item) {
                is Movie -> mapOf("type" to "movie") + gson.toJsonTree(item).asJsonObject.entrySet().associate { it.key to it.value }
                is Series -> mapOf("type" to "series") + gson.toJsonTree(item).asJsonObject.entrySet().associate { it.key to it.value }
                else -> emptyMap()
            }
        }
        val json = gson.toJson(enrichedList)
        file.writeText(json)
    }

    fun generateId(): String = UUID.randomUUID().toString()
}
