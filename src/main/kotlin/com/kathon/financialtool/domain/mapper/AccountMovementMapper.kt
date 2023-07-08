package com.kathon.financialtool.domain.mapper

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.dto.ExpenseCategoryDto
import com.kathon.financialtool.domain.dto.ExpenseMovementDto
import com.kathon.financialtool.domain.dto.PaymentTypeDto
import com.kathon.financialtool.domain.model.AccountMovementEntity
import com.kathon.financialtool.domain.model.ExpenseCategoryEntity
import com.kathon.financialtool.domain.model.ExpenseMovementEntity
import com.kathon.financialtool.domain.model.PaymentTypeEntity

fun AccountMovementEntity.toAccountMovementDto() = AccountMovementDto(
    id,
    name,
    description,
    amount,
    movementDate,
    account = account.toFinancialAccountDto(),
    movementType = movementType.name,
    expenseMovementDto = expenseMovementEntity?.toExpenseMovementDto(),
    createdBy = createdBy?.toPersonDto(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ExpenseMovementEntity.toExpenseMovementDto() = ExpenseMovementDto(
    id,
    category = category.toExpenseCategoryDto(),
    paymentType = paymentType.toPaymentTypeDto(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ExpenseCategoryEntity.toExpenseCategoryDto() = ExpenseCategoryDto(
    id, name
)

fun PaymentTypeEntity.toPaymentTypeDto() = PaymentTypeDto(
    id,
    name = paymentName
)