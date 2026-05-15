package com.example.beattreat.data.dto

data class FirestoreAlbumDto(
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val releaseYear: Int = 0,
    val coverImage: String = "",
    val description: String = ""
)