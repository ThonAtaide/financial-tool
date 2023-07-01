package com.kathon.financialtool.domain.usecase.financialAccount

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class FindAllFinancialAccountsUseCase(
    private val financialAccountRepository: FinancialAccountRepository
) {

    fun findFinancialAccountsBy(
        createdBy: Long? = null,
        expenseGroupId: Long,
        pageable: Pageable
    ): Page<FinancialAccountDto> {
        val specification = Specification
            .where(FinancialAccountRepository.expenseGroupEquals(expenseGroupId))
            .and(FinancialAccountRepository.createdByEquals(createdBy))
        return financialAccountRepository
            .findAll(specification, pageable)
            .map { it.toFinancialAccountDto() }
    }
}