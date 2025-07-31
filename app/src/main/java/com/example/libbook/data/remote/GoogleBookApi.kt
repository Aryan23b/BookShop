package com.example.libbook.data.remote


data class GoogleBooksResponse(
    val items: List<Volume>?
)

data class Volume(
    val volumeInfo: VolumeInfo?
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)