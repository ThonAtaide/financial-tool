package com.kathon.financialtool.domain.dto

import java.math.BigDecimal
import java.time.Instant

data class AccountMovementDto(

    val id: Long? = null,

    val name: String,

    val description: String? = null,

    val amount: BigDecimal,

    val movementDate: Instant,

    var accountId: Long? = null,

    var account: FinancialAccountDto? = null,

    var expenseMovementDto: ExpenseMovementDto? = null,

    var movementType: String? = null,

    var createdBy: PersonDto? = null,

    var createdAt: Instant? = null,

    var updatedAt: Instant? = null,
) {
}