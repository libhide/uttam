package com.ratik.uttam.domain.model

data class Photo(
    val id: String,
    var localUri: String,
    val photographer: Photographer,
    val shareUrl: String,
)
