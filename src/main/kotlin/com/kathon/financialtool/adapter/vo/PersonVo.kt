package com.kathon.financialtool.adapter.vo

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

data class PersonVo(
    val id: Long? = null,

    val name: String? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val createdAt: Instant? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val updatedAt: Instant? = null,
)