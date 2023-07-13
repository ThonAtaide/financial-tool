package com.kathon.financialtool.adapter.vo

import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnCreate
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnUpdate
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class FinancialAccountVo(

    @field:NotNull(
        groups = [ValidationGroupOnUpdate::class],
        message = "É preciso informar o id da conta."
    )
    var id: Long?,

    @field:NotBlank(
        groups = [ValidationGroupOnCreate::class, ValidationGroupOnUpdate::class],
        message = "É preciso informar o nome da conta."
    )
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