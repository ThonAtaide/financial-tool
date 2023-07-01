package com.kathon.financialtool.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import org.springframework.stereotype.Service

@Service
class DeleteFinancialAccountUseCase(
    private val financialAccountRepository: FinancialAccountRepository
) {

    fun deleteFinancialAccount(personId: Long, financialAccountId: Long) {
        financialAccountRepository
            .findById(financialAccountId)
            .orElseThrow { FinancialAccountNotFoundException(financialAccountId) }
            .let {
                if (personId != it.createdBy?.id) throw ResourceUnauthorizedException()
                it.isActive = false
                return@let financialAccountRepository.save(it)
            }
    }
}