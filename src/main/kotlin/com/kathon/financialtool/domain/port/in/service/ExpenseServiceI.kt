package com.kathon.financialtool.domain.port.`in`.service

import com.kathon.financialtool.domain.dto.ExpenseMovementDto

interface ExpenseServiceI {

    fun createExpenseMovement(
        personId: Long,
        expense: ExpenseMovementDto
    ): ExpenseMovementDto

    fun updateExpenseMovement(
        personId: Long,
        movementId: Long,
        expense: ExpenseMovementDto
    ): ExpenseMovementDto
}
