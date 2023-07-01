package com.kathon.financialtool.domain.port.`in`.service

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface FinancialAccountServiceI {

    fun createFinancialAccount(expenseGroupId: Long): FinancialAccountDto

    fun createFinancialAccount(expenseGroupId: Long, financialAccountDto: FinancialAccountDto): FinancialAccountDto

    fun updateFinancialAccount(
        personId: Long,
        financialAccountId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto

    fun getFinancialAccountsById(personId: Long, financialAccountId: Long): FinancialAccountDto

    fun searchExpenseGroupFinancialAccounts(
        createdBy: Long? = null,
        expenseGroupId: Long,
        pageable: Pageable = PageRequest.of(0, 10, Sort.Direction.ASC)
    ): Page<FinancialAccountDto>

    fun deleteFinancialAccount(personId: Long, financialAccountId: Long)
}