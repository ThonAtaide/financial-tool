package com.kathon.financialtool.domain.usecase.accountMovement

import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.AccountMovementEntity
import com.kathon.financialtool.domain.port.out.repository.AccountMovementRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class FindAccountMovementByIdUseCase(
    private val findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase,
    private val accountMovementRepository: AccountMovementRepository
) {

    fun findAccountMovementById(
        personId: Long,
        accountMovementId: Long
    ): Optional<AccountMovementEntity> {
        return accountMovementRepository
            .findById(accountMovementId)
            .map {
                val personExpenseGroupsList = findExpenseGroupsByPersonUseCase
                    .findExpenseGroupsByUser(personId, Pageable.unpaged())
                    .content
                if (!personExpenseGroupsList.contains(it.account.expenseGroupEntity)) {
                    throw ResourceUnauthorizedException()
                }
                return@map it
            }
    }
}