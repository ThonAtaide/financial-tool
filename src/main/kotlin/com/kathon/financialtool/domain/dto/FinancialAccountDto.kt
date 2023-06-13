package com.kathon.financialtool.domain.dto

import java.time.Instant

data class FinancialAccountDto(

    var id: Long?,

    var name: String,

    var createdBy: PersonDto? = null,

    var createdAt: Instant? = null,

    var updatedAt: Instant? = null,
) {
}