package com.kathon.financialtool.domain.adapter.`in`.service

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.enums.AccountMovementType
import com.kathon.financialtool.domain.mapper.toAccountMovementDto
import com.kathon.financialtool.domain.port.`in`.service.AccountMovementServiceI
import com.kathon.financialtool.domain.usecase.accountMovement.CreateAccountMovementUseCase
import com.kathon.financialtool.domain.usecase.accountMovement.FindAccountMovementByIdUseCase
import com.kathon.financialtool.domain.usecase.accountMovement.FindAllAccountMovementsUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class AccountMovementService(
    private val createAccountMovementUseCase: CreateAccountMovementUseCase,
    private val findAccountMovementByIdUseCase: FindAccountMovementByIdUseCase,
    private val findAllAccountMovementsUseCase: FindAllAccountMovementsUseCase
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
    ): AccountMovementDto =
        createAccountMovementUseCase
            .updateAccountMovement(personId, accountMovementId, accountMovementDto)
            .toAccountMovementDto()

    override fun findAccountMovementById(personId: Long, movementId: Long): Optional<AccountMovementDto> =
        findAccountMovementByIdUseCase.findAccountMovementById(personId, movementId)
            .map { it.toAccountMovementDto() }

    override fun findAllAccountMovementsBy(
        personId: Long,
        expenseGroup: Long,
        accountMovementTypeList: List<AccountMovementType>,
        movementName: String?,
        financialAccountIdList: List<Long>,
        expenseCategoryList: List<Long>,
        paymentTypeList: List<Long>,
        createdBy: Long?,
        oldestLimit: Instant,
        newestLimit: Instant,
        pageRequest: PageRequest
    ): Page<AccountMovementDto> = findAllAccountMovementsUseCase
        .findAllAccountMovementsBy(
            personId, expenseGroup, accountMovementTypeList, movementName, financialAccountIdList, expenseCategoryList
        ).map { it.toAccountMovementDto() }


}