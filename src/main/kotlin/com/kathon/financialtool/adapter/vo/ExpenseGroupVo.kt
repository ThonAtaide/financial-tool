package com.kathon.financialtool.adapter.vo

import com.fasterxml.jackson.annotation.JsonInclude
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnCreate
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnUpdate
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class ExpenseGroupVo(

    @field:NotNull(
        groups = [ValidationGroupOnUpdate::class],
        message = "É preciso informar o id do grupo."
    )
    val id: Long? = null,

    @field:NotBlank(
        groups = [ValidationGroupOnCreate::class, ValidationGroupOnUpdate::class],
        message = "É preciso informar o nome do grupo."
    )
    val name: String,

    val createdBy: PersonVo? = null,

    @field:NotNull(
        groups = [ValidationGroupOnUpdate::class],
        message = "É preciso informar os membros do grupo."
    )
    val members: List<PersonVo>? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val accounts: List<FinancialAccountVo>? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val createdAt: Instant? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val updatedAt: Instant? = null,
)