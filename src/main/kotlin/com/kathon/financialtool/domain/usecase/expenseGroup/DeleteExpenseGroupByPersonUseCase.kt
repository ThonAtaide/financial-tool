package com.kathon.financialtool.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import org.springframework.stereotype.Service

@Service
class DeleteExpenseGroupByPersonUseCase(
    private val expenseGroupRepository: ExpenseGroupRepository
) {

    fun deletePersonExpenseGroup(personId: Long, expenseGroupId: Long) {
        expenseGroupRepository.findById(expenseGroupId)
            .map {
                if (it.createdBy?.id != personId) throw ResourceUnauthorizedException()
                it.isActive = false
                expenseGroupRepository.save(it)
            }
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }
    }
}