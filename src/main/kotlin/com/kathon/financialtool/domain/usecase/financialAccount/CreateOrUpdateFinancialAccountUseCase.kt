package com.kathon.financialtool.domain.usecase.financialAccount

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import org.springframework.stereotype.Service

@Service
class CreateOrUpdateFinancialAccountUseCase(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val financialAccountRepository: FinancialAccountRepository
) {

    companion object {
        const val EXPENSE_GROUP_FINANCIAL_ACCNT_DEFAULT_NAME = "Carteira"
    }

    fun createDefaultFinancialAccount(expenseGroupId: Long): FinancialAccountEntity =
        startFinancialAccountBuild(expenseGroupId)
            .let { financialAccountRepository.save(it) }

    fun createFinancialAccount(
        expenseGroupId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountEntity =
        startFinancialAccountBuild(expenseGroupId, financialAccountDto.name)
            .let { financialAccountRepository.save(it) }

    fun updateFinancialAccount(
        personId: Long,
        financialAccountId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountEntity =
        financialAccountRepository
            .findById(financialAccountId)
            .orElseThrow { FinancialAccountNotFoundException(financialAccountId) }
            .let {
                if (personId != it.createdBy?.id) throw ResourceUnauthorizedException()
                it.name = financialAccountDto.name
                return@let financialAccountRepository.save(it)
            }

    private fun startFinancialAccountBuild(
        expenseGroupId: Long,
        accountName: String = EXPENSE_GROUP_FINANCIAL_ACCNT_DEFAULT_NAME
    ): FinancialAccountEntity {
        val expenseGroupEntity = expenseGroupRepository.findById(expenseGroupId)
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }
        return FinancialAccountEntity(
            name = accountName,
            expenseGroupEntity = expenseGroupEntity
        )
    }
}