package com.ratik.uttam.domain.model

data class Photo(
  val id: String,
  var rawPhotoUri: String,
  var regularPhotoUri: String,
  var thumbPhotoUri: String,
  val photographer: Photographer,
  val shareUrl: String,
)
