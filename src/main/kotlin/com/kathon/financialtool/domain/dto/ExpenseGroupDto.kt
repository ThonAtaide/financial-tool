package com.kathon.financialtool.domain.dto

import java.time.Instant

data class ExpenseGroupDto(

    var id: Long? = null,

    var name: String,

    val createdBy: PersonDto? = null,

    val members: MutableList<PersonDto>? = null,

    val createdAt: Instant? = null,

    val updatedAt: Instant? = null,
) {
}