package com.kathon.financialtool.adapter.vo

import java.time.Instant

data class FinancialAccountVo(

    var id: Long?,

    var name: String,

    var createdBy: PersonVo? = null,

    var createdAt: Instant? = null,

    var updatedAt: Instant? = null,
) {
    enum class SortFields(val field: String) {
        CREATED_AT("createdAt"),
        NAME("name"),
        CREATED_BY_NAME("createdBy.name")
    }
}