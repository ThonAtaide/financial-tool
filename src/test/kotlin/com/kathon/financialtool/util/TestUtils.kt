package com.kathon.financialtool.util

class TestUtils {

    companion object {
        fun randomIntBiggerThanZero() = (0 until Int.MAX_VALUE).random()

        fun randomLongBiggerThanZero() = (0 until Long.MAX_VALUE).random()
    }
}