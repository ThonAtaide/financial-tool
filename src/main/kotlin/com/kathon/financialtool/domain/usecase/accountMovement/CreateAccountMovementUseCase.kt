package com.kathon.financialtool.domain.usecase.accountMovement

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.model.AccountMovementEntity
import com.kathon.financialtool.domain.strategy.accountMovement.AbstractCreateAndUpdateAccountMovementStrategy
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateAccountMovementUseCase(
    private val accountMovementCreatorAndUpdaterStrategies: List<AbstractCreateAndUpdateAccountMovementStrategy>
) {

    fun createAccountMovement(
        person: Long,
        accountId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementEntity =
        accountMovementCreatorAndUpdaterStrategies
            .firstOrNull { it.supports(accountMovementDto) }
            .let { strategy ->
                Optional.ofNullable(strategy)
                    .map { it.createAccountMovement(person, accountId, accountMovementDto) }
                    .orElseThrow { Exception("Tipo de movimentação não implementada ainda.") }
            }

    fun updateAccountMovement(
        person: Long,
        accountMovementId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementEntity =
        accountMovementCreatorAndUpdaterStrategies
            .firstOrNull { it.supports(accountMovementDto) }
            .let { strategy ->
                Optional.ofNullable(strategy)
                    .map { it.updateAccountMovement(person, accountMovementId, accountMovementDto) }
                    .orElseThrow { Exception("Tipo de movimentação não implementada ainda.") }
            }
}