package com.kathon.financialtool.factories

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.dto.PersonDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.factories.PersonFactory.Companion.buildPersonDto
import com.kathon.financialtool.factories.PersonFactory.Companion.buildPersonEntity
import java.time.Instant
import java.util.*

class ExpenseGroupFactory {

    companion object {

        fun buildExpenseGroupEntity(
            id: Long? = System.currentTimeMillis(),
            name: String = UUID.randomUUID().toString(),
            createdBy: PersonEntity = buildPersonEntity(),
            isActive: Boolean = true,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ) = ExpenseGroupEntity(
            id,
            name,
            createdBy,
            mutableSetOf(createdBy),
            finAccountList = null,
            isActive,
            createdAt,
            updatedAt
        )

        fun buildExpenseGroupDto(
            id: Long? = System.currentTimeMillis(),
            name: String = UUID.randomUUID().toString(),
            createdBy: PersonDto = buildPersonDto(),
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
            accountList: MutableList<FinancialAccountDto>? = null
        ) = ExpenseGroupDto(
            id,
            name,
            createdBy,
            mutableListOf(createdBy),
            accountList,
            createdAt,
            updatedAt
        )
    }
}