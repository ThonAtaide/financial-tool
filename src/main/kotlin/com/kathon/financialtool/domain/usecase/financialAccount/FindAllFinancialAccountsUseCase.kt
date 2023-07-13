package com.kathon.financialtool.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class FindAllFinancialAccountsUseCase(
    private val financialAccountRepository: FinancialAccountRepository,
    private val findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase,
) {

    fun findFinancialAccountsBy(
        personId: Long,
        createdBy: Long? = null,
        expenseGroupId: Long,
        pageable: Pageable
    ): Page<FinancialAccountEntity> {

        if (expenseGroupDoNotBelongsToUser(personId, expenseGroupId)) {
            throw ResourceUnauthorizedException()
        }

        val specification = Specification
            .where(FinancialAccountRepository.expenseGroupEquals(expenseGroupId))
            .and(FinancialAccountRepository.createdByEquals(createdBy))
            .and(FinancialAccountRepository.isActiveEquals(true))
        return financialAccountRepository
            .findAll(specification, pageable)
    }

    private fun expenseGroupDoNotBelongsToUser(personId: Long, expenseGroupId: Long): Boolean {
        val userExpenseGroupList = findExpenseGroupsByPersonUseCase
            .findExpenseGroupsByUser(personId, PageRequest.of(0, Int.MAX_VALUE))
        val expenseGroupIdList = userExpenseGroupList.map { it.id }
        return !expenseGroupIdList.contains(expenseGroupId)
    }
}