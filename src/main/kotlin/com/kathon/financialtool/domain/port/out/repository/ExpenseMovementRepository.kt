package com.kathon.financialtool.domain.port.out.repository

import com.kathon.financialtool.domain.model.ExpenseMovementEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpenseMovementRepository : JpaRepository<ExpenseMovementEntity, Long>
