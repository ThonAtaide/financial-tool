package com.kathon.financialtool.domain.dto

import java.time.Instant

data class PersonDto(
    var id: Long?,

    val name: String,

    val createdAt: Instant? = null,

    val updatedAt: Instant? = null,
) {
}