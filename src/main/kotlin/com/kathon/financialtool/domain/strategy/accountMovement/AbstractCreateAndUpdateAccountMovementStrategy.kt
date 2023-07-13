package com.kathon.financialtool.domain.strategy.accountMovement

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.enums.AccountMovementType
import com.kathon.financialtool.domain.exceptions.*
import com.kathon.financialtool.domain.model.AccountMovementEntity
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.AccountMovementRepository
import com.kathon.financialtool.domain.port.out.repository.PersonRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindFinancialAccountByIdUseCase
import org.springframework.data.domain.Pageable

abstract class AbstractCreateAndUpdateAccountMovementStrategy(
    private val personRepository: PersonRepository,
    private val accountMovementRepository: AccountMovementRepository,
    private val accountMovementType: AccountMovementType,
    private val findFinancialAccountByIdUseCase: FindFinancialAccountByIdUseCase,
    private val findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase
) {

    open fun createAccountMovement(
        personId: Long,
        accountId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementEntity {
        val personEntity = personRepository
            .findById(personId)
            .orElseThrow { PersonNotFoundException(personId) }

        val accountEntity = findFinancialAccountByIdUseCase
            .findFinancialAccountById(personId, accountId)
            .orElseThrow { MissingRequiredDataException("É preciso informar uma conta válida.") }

        return AccountMovementEntity(
            name = accountMovementDto.name,
            description = accountMovementDto.description,
            amount = accountMovementDto.amount,
            movementDate = accountMovementDto.movementDate,
            movementType = this.accountMovementType,
            account = accountEntity,
            createdBy = personEntity
        )
    }

    open fun updateAccountMovement(
        personId: Long,
        accountMovementId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementEntity {

        val accountMovementEntity = accountMovementRepository.findById(accountMovementId)
            .orElseThrow { AccountMovementNotFoundException(accountMovementId) }

        if (personDoNotBelongsToAccountMovementExpenseGroup(personId, accountMovementEntity.account)) {
            throw ResourceUnauthorizedException()
        }

        if (isFinancialAccountBeingUpdated(accountMovementEntity.account.id!!, accountMovementDto.accountId)) {
            val accountFound = findFinancialAccountByIdUseCase
                .findFinancialAccountById(personId, accountMovementDto.accountId!!)
                .orElseThrow { MissingRequiredDataException("É preciso informar uma conta válida.") }

            if (newAccountIsNotFromSameExpenseGroup(accountFound, accountMovementEntity)) {
                throw InvalidDataException("Não é possível alterar a despesa para uma conta de outro grupo de despesas.")
            }
            accountMovementEntity.account = accountFound
        }
        accountMovementEntity.name = accountMovementDto.name
        accountMovementEntity.description = accountMovementDto.description
        accountMovementEntity.amount = accountMovementDto.amount
        accountMovementEntity.movementDate = accountMovementDto.movementDate
        return accountMovementEntity
    }

    private fun newAccountIsNotFromSameExpenseGroup(
        newAccount: FinancialAccountEntity,
        accountMovementEntity: AccountMovementEntity
    ) = newAccount.expenseGroupEntity != accountMovementEntity.account.expenseGroupEntity

    abstract fun supports(accountMovementDto: AccountMovementDto): Boolean

    private fun isFinancialAccountBeingUpdated(
        currentStoredAccountId: Long,
        currentDtoAccountId: Long?
    ): Boolean = (currentStoredAccountId != currentDtoAccountId) && (currentDtoAccountId != null)

    private fun personDoNotBelongsToAccountMovementExpenseGroup(
        personId: Long,
        financialAccountEntity: FinancialAccountEntity
    ): Boolean {
        val personIdExpenseGroupsId =
            findExpenseGroupsByPersonUseCase
                .findExpenseGroupsByUser(personId, Pageable.unpaged())
                .map { it.id }
        return !personIdExpenseGroupsId.contains(financialAccountEntity.expenseGroupEntity.id)
    }


}