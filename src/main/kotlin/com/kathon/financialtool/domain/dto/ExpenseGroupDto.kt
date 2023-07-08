package com.kathon.financialtool.domain.dto

import java.time.Instant

data class ExpenseGroupDto(

    var id: Long? = null,

    var name: String,

    val createdBy: PersonDto? = null,

    val members: List<PersonDto>? = null,

    val accounts: List<FinancialAccountDto>? = null,

    val createdAt: Instant? = null,

    val updatedAt: Instant? = null,
) {
}