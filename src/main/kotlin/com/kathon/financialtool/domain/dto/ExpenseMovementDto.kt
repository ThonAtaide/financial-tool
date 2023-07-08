package com.kathon.financialtool.domain.dto

import java.time.Instant

data class ExpenseMovementDto(

    var id: Long? = null,

    var categoryId: Long? = null,

    var category: ExpenseCategoryDto? = null,

    var paymentId: Long? = null,

    var paymentType: PaymentTypeDto? = null,

    var createdAt: Instant? = null,

    var updatedAt: Instant? = null,
) {
}