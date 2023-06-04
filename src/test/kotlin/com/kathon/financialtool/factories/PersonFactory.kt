package com.kathon.financialtool.factories

import com.kathon.financialtool.domain.dto.PersonDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.PersonEntity
import java.time.Instant
import java.util.*

class PersonFactory {

    companion object {
        fun buildPersonEntity(
            id: Long? = System.currentTimeMillis(),
            name: String = UUID.randomUUID().toString(),
            expenseGroups: MutableList<ExpenseGroupEntity> = mutableListOf(),
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now()
        ): PersonEntity = PersonEntity(
            id, name, expenseGroups, createdAt, updatedAt
        )

        fun buildPersonDto(
            id: Long? = System.currentTimeMillis(),
            name: String = UUID.randomUUID().toString(),
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now()
        ): PersonDto = PersonDto(
            id, name, createdAt, updatedAt
        )
    }
}
