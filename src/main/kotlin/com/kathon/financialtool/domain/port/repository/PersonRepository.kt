package com.kathon.financialtool.domain.port.repository

import com.kathon.financialtool.domain.model.PersonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: JpaRepository<PersonEntity, Long>