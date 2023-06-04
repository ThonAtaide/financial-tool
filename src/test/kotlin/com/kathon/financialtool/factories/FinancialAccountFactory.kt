package com.kathon.financialtool.factories

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.dto.PersonDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.factories.ExpenseGroupFactory.Companion.buildExpenseGroupEntity
import com.kathon.financialtool.factories.PersonFactory.Companion.buildPersonDto
import com.kathon.financialtool.factories.PersonFactory.Companion.buildPersonEntity
import java.time.Instant
import java.util.*

class FinancialAccountFactory {

    companion object {
        fun buildFinancialAccountEntity(
            id: Long? = System.currentTimeMillis(),
            name: String = UUID.randomUUID().toString(),
            createdBy: PersonEntity = buildPersonEntity(),
            expenseGroup: ExpenseGroupEntity = buildExpenseGroupEntity(),
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now()
        ): FinancialAccountEntity = FinancialAccountEntity(
            id, name, createdBy = createdBy, expenseGroupEntity = expenseGroup, createdAt = createdAt, updatedAt = updatedAt
        )

        fun buildFinancialAccountDto(
            id: Long? = System.currentTimeMillis(),
            name: String = UUID.randomUUID().toString(),
            createdBy: PersonDto = buildPersonDto(),
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now()
        ): FinancialAccountDto = FinancialAccountDto(
            id, name, createdBy, createdAt, updatedAt
        )
    }
}
