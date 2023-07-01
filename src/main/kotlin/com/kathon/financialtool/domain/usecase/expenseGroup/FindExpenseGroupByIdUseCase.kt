package com.kathon.financialtool.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class FindExpenseGroupByIdUseCase(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val findAllFinancialAccountsUseCase: FindAllFinancialAccountsUseCase
) {

    fun findUserExpenseGroupsById(personId: Long, expenseGroupId: Long): ExpenseGroupDto =
        expenseGroupRepository.findById(expenseGroupId)
            .map { expenseGroupEntity ->
                val membersIdList = expenseGroupEntity.members.map { it.id }
                if (!membersIdList.contains(personId)) throw ResourceUnauthorizedException()
                val expenseGroupFinAccountList = findAllFinancialAccountsUseCase
                    .findFinancialAccountsBy(
                        expenseGroupId = expenseGroupId,
                        pageable = PageRequest.of(0, Int.MAX_VALUE, Sort.Direction.ASC, "created_at")
                    ).content
                return@map expenseGroupEntity.toExpenseGroupDto(expenseGroupFinAccountList)
            }
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }
}