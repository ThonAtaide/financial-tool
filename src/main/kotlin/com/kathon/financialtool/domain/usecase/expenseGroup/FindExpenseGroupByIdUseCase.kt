package com.kathon.financialtool.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class FindExpenseGroupByIdUseCase(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val findAllFinancialAccountsUseCase: FindAllFinancialAccountsUseCase
) {

    fun findUserExpenseGroupsById(personId: Long, expenseGroupId: Long): Optional<ExpenseGroupEntity> =
        expenseGroupRepository.findById(expenseGroupId)
            .map { expenseGroupEntity ->
                val membersIdList = expenseGroupEntity.members.map { it.id }
                if (!membersIdList.contains(personId)) throw ResourceUnauthorizedException()
                val expenseGroupFinAccountList = findAllFinancialAccountsUseCase
                    .findFinancialAccountsBy(
                        personId = personId,
                        expenseGroupId = expenseGroupId,
                        pageable = PageRequest.of(0, Int.MAX_VALUE, Sort.Direction.ASC, "createdAt")
                    ).content
                expenseGroupEntity.finAccountList = expenseGroupFinAccountList
                return@map expenseGroupEntity
            }
}