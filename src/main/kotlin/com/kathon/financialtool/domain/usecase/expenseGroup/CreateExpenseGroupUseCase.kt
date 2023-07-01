package com.kathon.financialtool.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.exceptions.PersonNotFoundException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.out.repository.PersonRepository
import com.kathon.financialtool.domain.usecase.financialAccount.CreateOrUpdateFinancialAccountUseCase
import org.springframework.stereotype.Service

@Service
class CreateExpenseGroupUseCase(
    private val personRepository: PersonRepository,
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val financialAccountUseCase: CreateOrUpdateFinancialAccountUseCase
) {

    fun createExpenseGroup(personId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto =
        personRepository.findById(personId)
            .map {
                val newExpenseGroup = ExpenseGroupEntity(name = expenseGroupDto.name, members = mutableSetOf(it))
                val createdExpenseGroup = expenseGroupRepository.save(newExpenseGroup).toExpenseGroupDto()
                financialAccountUseCase.createDefaultFinancialAccount(createdExpenseGroup.id!!)
                return@map createdExpenseGroup
            }.orElseThrow { PersonNotFoundException(personId) }
}