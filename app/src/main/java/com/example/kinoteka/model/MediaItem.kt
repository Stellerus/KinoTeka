package com.example.kinoteka.model

import com.google.gson.annotations.SerializedName

/**
 * Base sealed class for media items.
 * Two types are fixed: Movie and Series.
 */
sealed class MediaItem {
    abstract val id: String
    abstract val title: String
    abstract val genre: String
}

data class Movie(
    override val id: String,
    @SerializedName("title") val titleField: String = "",
    val director: String = "",
    val year: Int = 0,
    override val genre: String = "",
    val duration: Int = 0, // in minutes
    val rating: Float = 0f // 0..10
) : MediaItem() {
    override val title: String get() = titleField
}

data class Series(
    override val id: String,
    @SerializedName("title") val titleField: String = "",
    val creators: String = "",
    val years: String = "", // e.g. "2016-2023"
    override val genre: String = "",
    val seasons: Int = 1,
    val status: String = "" // "Watching", "Completed", "Dropped"
) : MediaItem() {
    override val title: String get() = titleField
}
