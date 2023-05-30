package com.kathon.financialtool.domain.port.repository

import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ExpenseGroupRepository : JpaRepository<ExpenseGroupEntity, Long> {

    @Query(
        "SELECT EG FROM ExpenseGroupEntity EG " +
                "INNER JOIN EG.members m where m.id= :member"
    )
    fun findExpenseGroupEntitiesByMembersContaining(member: Long, pageRequest: Pageable): Page<ExpenseGroupEntity>
}