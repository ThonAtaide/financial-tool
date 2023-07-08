package com.kathon.financialtool.domain.adapter.`in`.service

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.port.`in`.service.FinancialAccountServiceI
import com.kathon.financialtool.domain.usecase.financialAccount.CreateOrUpdateFinancialAccountUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.DeleteFinancialAccountUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindFinancialAccountByIdUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class FinancialAccountService(
    private val createOrUpdateFinancialAccountUseCase: CreateOrUpdateFinancialAccountUseCase,
    private val findFinancialAccountByIdUseCase: FindFinancialAccountByIdUseCase,
    private val findAllFinancialAccountsUseCase: FindAllFinancialAccountsUseCase,
    private val deleteFinancialAccountUseCase: DeleteFinancialAccountUseCase
) : FinancialAccountServiceI {

    override fun createFinancialAccount(expenseGroupId: Long): FinancialAccountDto =
        createOrUpdateFinancialAccountUseCase
            .createDefaultFinancialAccount(expenseGroupId)
            .toFinancialAccountDto()

    override fun createFinancialAccount(
        expenseGroupId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto =
        createOrUpdateFinancialAccountUseCase
            .createFinancialAccount(expenseGroupId, financialAccountDto)
            .toFinancialAccountDto()

    override fun updateFinancialAccount(
        personId: Long,
        financialAccountId: Long,
        financialAccountDto: FinancialAccountDto
    ): FinancialAccountDto =
        createOrUpdateFinancialAccountUseCase
            .updateFinancialAccount(personId, financialAccountId, financialAccountDto)
            .toFinancialAccountDto()

    override fun findFinancialAccountsById(personId: Long, financialAccountId: Long): Optional<FinancialAccountDto> =
        findFinancialAccountByIdUseCase
            .findFinancialAccountsById(personId, financialAccountId)
            .map { it.toFinancialAccountDto() }

    override fun searchExpenseGroupFinancialAccounts(
        personId: Long,
        createdBy: Long?,
        expenseGroupId: Long,
        pageable: Pageable
    ): Page<FinancialAccountDto> =
        findAllFinancialAccountsUseCase
            .findFinancialAccountsBy(personId, createdBy, expenseGroupId, pageable)
            .map { it.toFinancialAccountDto() }

    override fun deleteFinancialAccount(personId: Long, financialAccountId: Long) =
        deleteFinancialAccountUseCase.deleteFinancialAccount(personId, financialAccountId)

}