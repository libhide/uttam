package com.ratik.uttam.util

import junit.framework.Assert.assertEquals
import org.junit.Test

class StringExtensionsKtTest {

    @Test
    fun `should convert any string to title case`() {
        val strings = mutableListOf(
                "Hello World",
                "hello world",
                "hEllo WoRld",
                "HELLO WORLD"
        )
        val expected = "Hello World"

        strings.forEach {
            assertEquals(expected, it.toTitleCase())
        }
    }
}