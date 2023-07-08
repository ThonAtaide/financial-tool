package com.kathon.financialtool.domain.adapter.`in`.service

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.mapper.toAccountMovementDto
import com.kathon.financialtool.domain.port.`in`.service.AccountMovementServiceI
import com.kathon.financialtool.domain.usecase.accountMovement.CreateAccountMovementUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountMovementService(
    private val createAccountMovementUseCase: CreateAccountMovementUseCase
) : AccountMovementServiceI {

    override fun createAccountMovement(
        personId: Long,
        accountId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementDto =
        createAccountMovementUseCase
            .createAccountMovement(personId, accountId, accountMovementDto)
            .toAccountMovementDto()

    override fun updateAccountMovement(
        personId: Long,
        accountMovementId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementDto {
        TODO("Not yet implemented")
    }

    override fun findAccountMovementById(personId: Long, movementId: Long): AccountMovementDto {
        TODO("Not yet implemented")
    }

    override fun findAllAccountMovementsBy(
        expenseGroup: Long,
        movementName: String?,
        financialAccount: Long?,
        category: Long?,
        paymentType: Long?,
        createdBy: Long?,
        rangeStart: LocalDateTime?,
        rangeEnd: LocalDateTime?,
        pageRequest: PageRequest
    ): Page<AccountMovementDto> {
        TODO("Not yet implemented")
    }


}