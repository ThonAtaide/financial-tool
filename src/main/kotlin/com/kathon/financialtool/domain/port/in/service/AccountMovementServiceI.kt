package com.kathon.financialtool.domain.port.`in`.service

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.dto.ExpenseMovementDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

interface AccountMovementServiceI {

    fun createAccountMovement(
        personId: Long,
        accountId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementDto

    fun updateAccountMovement(
        personId: Long,
        accountMovementId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementDto

    fun findAccountMovementById(
        personId: Long,
        movementId: Long
    ): AccountMovementDto

    fun findAllAccountMovementsBy(
        expenseGroup: Long,
        movementName: String? = null,
        financialAccount: Long? = null,
        category: Long? = null,
        paymentType: Long? = null,
        createdBy: Long? = null,
        rangeStart: LocalDateTime? = LocalDateTime.now(),
        rangeEnd: LocalDateTime? = LocalDateTime.now().minusDays(15),
        pageRequest: PageRequest = PageRequest.of(
            0, Int.MAX_VALUE, Sort.Direction.ASC, "created_at"
        )
    ): Page<AccountMovementDto>
}
