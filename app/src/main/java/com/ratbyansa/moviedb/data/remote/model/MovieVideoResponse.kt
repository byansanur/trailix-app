package com.ratbyansa.moviedb.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieVideoResponse(
    val id: Int,
    val results: List<MovieVideo>
)

@Serializable
data class MovieVideo(
    val id: String,
    val key: String, // ID Video YouTube
    val name: String,
    val site: String,
    val type: String, // "Trailer", "Teaser", dll
    val official: Boolean,
    @SerialName("published_at")
    val publishedAt: String
)