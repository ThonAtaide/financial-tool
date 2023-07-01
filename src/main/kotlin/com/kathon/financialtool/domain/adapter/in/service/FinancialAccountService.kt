package com.kathon.financialtool.domain.adapter.`in`.service

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.port.`in`.service.FinancialAccountServiceI
import com.kathon.financialtool.domain.usecase.financialAccount.CreateOrUpdateFinancialAccountUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.DeleteFinancialAccountUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindFinancialAccountByIdUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FinancialAccountService(
    private val createOrUpdateFinancialAccountUseCase: CreateOrUpdateFinancialAccountUseCase,
    private val findFinancialAccountByIdUseCase: FindFinancialAccountByIdUseCase,
    private val findAllFinancialAccountsUseCase: FindAllFinancialAccountsUseCase,
    private val deleteFinancialAccountUseCase: DeleteFinancialAccountUseCase
) : FinancialAccountServiceI {

    override fun createFinancialAccount(expenseGroupId: Long): FinancialAccountDto =
        createOrUpdateFinancialAccountUseCase.createDefaultFinancialAccount(expenseGroupId)

    override fun createFinancialAccount(
        expenseGroupId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto =
        createOrUpdateFinancialAccountUseCase
            .createFinancialAccount(expenseGroupId, financialAccountDto)

    override fun updateFinancialAccount(
        personId: Long,
        financialAccountId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto =
        createOrUpdateFinancialAccountUseCase
            .updateFinancialAccount(personId, financialAccountId, financialAccountDto)

    override fun getFinancialAccountsById(personId: Long, financialAccountId: Long): FinancialAccountDto =
        findFinancialAccountByIdUseCase.findFinancialAccountsById(personId, financialAccountId)

    override fun searchExpenseGroupFinancialAccounts(
        createdBy: Long?,
        expenseGroupId: Long,
        pageable: Pageable
    ): Page<FinancialAccountDto> =
        findAllFinancialAccountsUseCase
            .findFinancialAccountsBy(createdBy, expenseGroupId, pageable)

    override fun deleteFinancialAccount(personId: Long, financialAccountId: Long) =
        deleteFinancialAccountUseCase.deleteFinancialAccount(personId, financialAccountId)

}