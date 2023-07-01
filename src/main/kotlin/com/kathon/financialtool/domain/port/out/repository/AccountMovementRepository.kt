package com.kathon.financialtool.domain.port.out.repository

import com.kathon.financialtool.domain.model.AccountMovementEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface AccountMovementRepository : JpaRepository<AccountMovementEntity, Long>,
    JpaSpecificationExecutor<AccountMovementEntity>
