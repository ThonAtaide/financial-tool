package com.kathon.financialtool.infra

import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.domain.port.repository.PersonRepository
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserAudit(
    private val personRepository: PersonRepository): AuditorAware<PersonEntity> {

    override fun getCurrentAuditor(): Optional<PersonEntity> {
        return personRepository.findById(1) //TODO replace after create login
    }
}