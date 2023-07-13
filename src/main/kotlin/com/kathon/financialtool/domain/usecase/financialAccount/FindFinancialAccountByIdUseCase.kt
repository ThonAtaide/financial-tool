package com.kathon.financialtool.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FindFinancialAccountByIdUseCase(
    private val financialAccountRepository: FinancialAccountRepository
) {

    fun findFinancialAccountById(personId: Long, financialAccountId: Long): Optional<FinancialAccountEntity> =
        financialAccountRepository
            .findById(financialAccountId)
            .map {
                val groupMembersIdList = it.expenseGroupEntity.members.map { member -> member.id }
                if (!groupMembersIdList.contains(personId)) throw ResourceUnauthorizedException()
                return@map it
            }
}