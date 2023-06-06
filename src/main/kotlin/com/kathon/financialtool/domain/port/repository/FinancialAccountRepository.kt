package com.kathon.financialtool.domain.port.repository

import com.kathon.financialtool.domain.model.FinancialAccountEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FinancialAccountRepository : JpaRepository<FinancialAccountEntity, Long> {

    @Query(
        "SELECT FA FROM FinancialAccountEntity FA " +
                "INNER JOIN ExpenseGroupEntity EG ON EG.id=FA.expenseGroupEntity.id " +
                "WHERE EG.id = :expenseGroupId AND FA.isActive = true"
    )
    fun findFinancialAccountEntitiesByExpenseGroupEntity(
        expenseGroupId: Long,
        pageRequest: PageRequest
    ): Page<FinancialAccountEntity>

    @Query(
        "SELECT FA FROM FinancialAccountEntity FA " +
                "INNER JOIN ExpenseGroupEntity EG ON EG.id=FA.expenseGroupEntity.id " +
                "WHERE EG.id = :expenseGroupId AND FA.createdBy.id = :personId AND FA.isActive = true"
    )
    fun findFinancialAccountEntitiesByExpenseGroupEntityAndCreatedByPerson(
        expenseGroupId: Long,
        personId: Long,
        pageRequest: PageRequest
    ): Page<FinancialAccountEntity>
}