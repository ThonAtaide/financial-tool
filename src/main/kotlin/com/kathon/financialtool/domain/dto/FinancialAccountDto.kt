package com.kathon.financialtool.domain.dto

import java.time.Instant

data class FinancialAccountDto(

    var id: Long?,

    val name: String,

    var createdBy: PersonDto? = null,

    val createdAt: Instant? = null,

    val updatedAt: Instant? = null,
) {
}