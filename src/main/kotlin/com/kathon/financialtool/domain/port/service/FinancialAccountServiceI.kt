package com.kathon.financialtool.domain.port.service

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import org.springframework.data.domain.Page

interface FinancialAccountServiceI {

    fun createFinancialAccount(expenseGroupId: Long): FinancialAccountDto

    fun createFinancialAccount(expenseGroupId: Long, financialAccountDto: FinancialAccountDto): FinancialAccountDto

    fun updateFinancialAccount(
        personId: Long,
        financialAccountId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto

    fun getFinancialAccountsById(personId: Long, financialAccountId: Long): FinancialAccountDto

    fun getFinancialAccountsByExpenseGroupAndFilter(
        createdBy: Long? = null,
        expenseGroupId: Long,
        pageNumber: Int = 0,
        pageSize: Int = 100
    ): Page<FinancialAccountDto>

    fun deleteFinancialAccount(personId: Long, financialAccountId: Long)
}