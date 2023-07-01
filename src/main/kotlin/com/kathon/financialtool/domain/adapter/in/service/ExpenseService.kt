package com.kathon.financialtool.domain.adapter.`in`.service

import com.kathon.financialtool.domain.dto.ExpenseMovementDto
import com.kathon.financialtool.domain.port.`in`.service.ExpenseServiceI
import com.kathon.financialtool.domain.port.out.repository.AccountMovementRepository
import com.kathon.financialtool.domain.port.out.repository.ExpenseMovementRepository
import org.springframework.stereotype.Service

@Service
class ExpenseService(
    private val accountMovementRepository: AccountMovementRepository,
    private val expenseMovementRepository: ExpenseMovementRepository
): ExpenseServiceI {

    override fun createExpenseMovement(personId: Long, expense: ExpenseMovementDto): ExpenseMovementDto {

        TODO("Not yet implemented")
    }

    override fun updateExpenseMovement(
        personId: Long,
        movementId: Long,
        expense: ExpenseMovementDto
    ): ExpenseMovementDto {
        TODO("Not yet implemented")
    }



}