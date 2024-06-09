package com.ratik.uttam.domain.model

/** Created by Ratik on 02/02/18. */
@Deprecated("Redundant, just directly use [Photo] instead")
enum class PhotoType(val value: Int) {
  FULL(0),
  REGULAR(1),
  THUMB(2),
}
