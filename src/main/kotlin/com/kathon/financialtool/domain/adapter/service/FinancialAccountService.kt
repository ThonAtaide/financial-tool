package com.kathon.financialtool.domain.adapter.service

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.port.service.FinancialAccountServiceI
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class FinancialAccountService(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val financialAccountRepository: FinancialAccountRepository
) : FinancialAccountServiceI {

    companion object {
        const val EXPENSE_GROUP_FINANCIAL_ACCNT_DEFAULT_NAME = "Carteira"
    }

    override fun createFinancialAccount(expenseGroupId: Long): FinancialAccountDto =
        startFinancialAccountBuild(expenseGroupId)
            .let { financialAccountRepository.save(it) }
            .toFinancialAccountDto()

    override fun createFinancialAccount(
        expenseGroupId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto =
        startFinancialAccountBuild(expenseGroupId, financialAccountDto.name)
            .let { financialAccountRepository.save(it) }
            .toFinancialAccountDto()

    override fun updateFinancialAccount(
        personId: Long,
        financialAccountId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto =
        financialAccountRepository
            .findById(financialAccountId).orElseThrow { FinancialAccountNotFoundException(financialAccountId) }
            .let {
                if (personId != it.createdBy?.id) throw ResourceUnauthorizedException()
                it.name = financialAccountDto.name
                return@let financialAccountRepository.save(it)
            }.toFinancialAccountDto()

    override fun getFinancialAccountsById(personId: Long, financialAccountId: Long): FinancialAccountDto =
        financialAccountRepository
            .findById(financialAccountId)
            .orElseThrow { FinancialAccountNotFoundException(financialAccountId) }
            .let {
                val groupMembersIdList = it.expenseGroupEntity.members.map { member -> member.id }
                if (!groupMembersIdList.contains(personId)) throw ResourceUnauthorizedException()
                return@let it.toFinancialAccountDto()
            }

    override fun getFinancialAccountsByExpenseGroupAndFilter(
        createdBy: Long?,
        expenseGroupId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Page<FinancialAccountDto> {
        val pageRequest = PageRequest.of(pageNumber, pageSize)
        if (createdBy == null) {
            return financialAccountRepository.findFinancialAccountEntitiesByExpenseGroupEntity(
                expenseGroupId,
                pageRequest
            )
                .map { it.toFinancialAccountDto() }
        }
        return financialAccountRepository.findFinancialAccountEntitiesByExpenseGroupEntityAndCreatedByPerson(
            expenseGroupId,
            createdBy,
            pageRequest
        ).map { it.toFinancialAccountDto() }
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