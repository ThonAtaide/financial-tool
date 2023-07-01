package com.kathon.financialtool.domain.usecase.financialAccount

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import org.springframework.stereotype.Service

@Service
class FindFinancialAccountByIdUseCase(
    private val financialAccountRepository: FinancialAccountRepository
) {

    fun findFinancialAccountsById(personId: Long, financialAccountId: Long): FinancialAccountDto =
        financialAccountRepository
            .findById(financialAccountId)
            .orElseThrow { FinancialAccountNotFoundException(financialAccountId) }
            .let {
                val groupMembersIdList = it.expenseGroupEntity.members.map { member -> member.id }
                if (!groupMembersIdList.contains(personId)) throw ResourceUnauthorizedException()
                return@let it.toFinancialAccountDto()
            }
}