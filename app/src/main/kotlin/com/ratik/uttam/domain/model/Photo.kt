package com.ratik.uttam.domain.model

data class Photo(
    var localUri: String,
    val photographer: Photographer,
    val shareUrl: String,
)
