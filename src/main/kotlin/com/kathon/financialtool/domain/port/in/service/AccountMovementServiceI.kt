package com.kathon.financialtool.domain.port.`in`.service

import com.kathon.financialtool.domain.dto.ExpenseMovementDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

interface AccountMovementServiceI {

    fun getAccountMovementById(
        personId: Long,
        movementId: Long
    ): ExpenseMovementDto

    fun searchAccountMovements(
        expenseGroup: Long,
        movementName: String? = null,
        financialAccount: Long? = null,
        category: Long? = null,
        paymentType: Long? = null,
        createdBy: Long? = null,
        rangeStart: LocalDateTime? = LocalDateTime.now(),
        rangeEnd: LocalDateTime? = LocalDateTime.now().minusDays(15),
        pageRequest: PageRequest
    ): Page<ExpenseMovementDto>
}
