package com.kathon.financialtool.domain.usecase.accountMovement

import com.kathon.financialtool.domain.enums.AccountMovementType
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.AccountMovementEntity
import com.kathon.financialtool.domain.port.out.repository.AccountMovementRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class FindAllAccountMovementsUseCase(
    private val findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase,
    private val accountMovementRepository: AccountMovementRepository
) {

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
            0, Int.MAX_VALUE, Sort.Direction.DESC, "movementDate"
        )
    ): Page<AccountMovementEntity> {
        val personExpenseGroups = findExpenseGroupsByPersonUseCase
            .findExpenseGroupsByUser(personId, Pageable.unpaged())
        val personHasAccessToSearchGroup = personExpenseGroups
            .content.map { it.id }.contains(expenseGroup)

        if (!personHasAccessToSearchGroup) {
            throw ResourceUnauthorizedException()
        }

        val specifications = Specification.where(AccountMovementRepository.expenseGroupEquals(expenseGroup))
            .and(AccountMovementRepository.financialAccountMovementIn(financialAccountIdList))
            .and(AccountMovementRepository.accountMovementTypeIn(accountMovementTypeList))
            .and(AccountMovementRepository.accountMovementRangeBetween(oldestLimit, newestLimit))
            .and(AccountMovementRepository.accountMovementNameStartWith(movementName))
            .and(AccountMovementRepository.accountMovementCreatedByEquals(createdBy))
            .and(AccountMovementRepository.expenseCategoryIn(expenseCategoryList))
        return accountMovementRepository.findAll(specifications, pageRequest)
    }
}