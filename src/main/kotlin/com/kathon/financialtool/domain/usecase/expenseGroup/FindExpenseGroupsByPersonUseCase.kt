package com.kathon.financialtool.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FindExpenseGroupsByPersonUseCase(
    private val expenseGroupRepository: ExpenseGroupRepository
) {

    fun findExpenseGroupsByUser(personId: Long, pageable: Pageable): Page<ExpenseGroupDto> =
        expenseGroupRepository
            .findExpenseGroupEntitiesByMembersContaining(personId, pageable)
            .map { it.toExpenseGroupDto() }
}