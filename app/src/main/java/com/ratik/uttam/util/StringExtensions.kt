package com.ratik.uttam.util

fun String.toTitleCase(): String {
    var space = true
    val builder = StringBuilder(this)
    val len = builder.length

    for (i in 0 until len) {
        val c = builder[i]
        if (space) {
            if (!Character.isWhitespace(c)) {
                builder.setCharAt(i, Character.toTitleCase(c))
                space = false
            }
        } else if (Character.isWhitespace(c)) {
            space = true
        } else {
            builder.setCharAt(i, Character.toLowerCase(c))
        }
    }
    return builder.toString()
}