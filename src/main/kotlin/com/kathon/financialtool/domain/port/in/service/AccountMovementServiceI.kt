package com.kathon.financialtool.domain.port.`in`.service

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.dto.ExpenseMovementDto
import com.kathon.financialtool.domain.enums.AccountMovementType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

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
    ): Optional<AccountMovementDto>

    fun findAllAccountMovementsBy(
        personId: Long,
        expenseGroup: Long,
        accountMovementTypeList: List<AccountMovementType> = emptyList(),
        movementName: String? = null,
        financialAccountIdList: List<Long> = emptyList(),
        expenseCategoryList: List<Long> = emptyList(),
        paymentTypeList: List<Long> = emptyList(),
        createdBy: Long? = null,
        oldestLimit: Instant = Instant.now().minus(15, ChronoUnit.DAYS),
        newestLimit: Instant = Instant.now(),
        pageRequest: PageRequest = PageRequest.of(
            0, Int.MAX_VALUE, Sort.Direction.ASC, "created_at"
        )
    ): Page<AccountMovementDto>
}
