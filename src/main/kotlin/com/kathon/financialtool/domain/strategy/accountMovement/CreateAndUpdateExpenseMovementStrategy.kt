package com.kathon.financialtool.domain.strategy.accountMovement

import com.kathon.financialtool.domain.dto.AccountMovementDto
import com.kathon.financialtool.domain.enums.AccountMovementType
import com.kathon.financialtool.domain.exceptions.InvalidDataException
import com.kathon.financialtool.domain.exceptions.MissingRequiredDataException
import com.kathon.financialtool.domain.model.AccountMovementEntity
import com.kathon.financialtool.domain.model.ExpenseCategoryEntity
import com.kathon.financialtool.domain.model.ExpenseMovementEntity
import com.kathon.financialtool.domain.model.PaymentTypeEntity
import com.kathon.financialtool.domain.port.out.repository.AccountMovementRepository
import com.kathon.financialtool.domain.port.out.repository.ExpenseCategoryRepository
import com.kathon.financialtool.domain.port.out.repository.PaymentTypeRepository
import com.kathon.financialtool.domain.port.out.repository.PersonRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindFinancialAccountByIdUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateAndUpdateExpenseMovementStrategy(
    personRepository: PersonRepository,
    private val accountMovementRepository: AccountMovementRepository,
    private val expenseCategoryRepository: ExpenseCategoryRepository,
    private val paymentTypeRepository: PaymentTypeRepository,
    findFinancialAccountByIdUseCase: FindFinancialAccountByIdUseCase,
    findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase
) : AbstractCreateAndUpdateAccountMovementStrategy(
    personRepository, accountMovementRepository, AccountMovementType.EXPENSE,
    findFinancialAccountByIdUseCase, findExpenseGroupsByPersonUseCase
) {

    override fun createAccountMovement(
        personId: Long,
        accountId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementEntity {
        val accountMovement =
            super.createAccountMovement(
                personId,
                accountId,
                accountMovementDto
            )
        val expenseMovementDto = Optional.ofNullable(accountMovementDto.expenseMovementDto)
            .orElseThrow { MissingRequiredDataException("Missing expense movement data.") }

        val expenseCategory = this
            .findExpenseCategory(expenseMovementDto.categoryId)

        val paymentType = this
            .findPaymentType(expenseMovementDto.paymentId)

        val expenseMovementEntity = ExpenseMovementEntity(
            accountMovement = accountMovement,
            category = expenseCategory,
            paymentType = paymentType
        )
        accountMovement.expenseMovementEntity = expenseMovementEntity
        return accountMovementRepository.save(accountMovement)
    }

    override fun updateAccountMovement(
        personId: Long,
        accountMovementId: Long,
        accountMovementDto: AccountMovementDto
    ): AccountMovementEntity {
        val accountMovementEntity =
            super.updateAccountMovement(personId, accountMovementId, accountMovementDto)

        val expenseMovementEntity = accountMovementEntity.expenseMovementEntity!!

        updateExpenseCategoryIfNecessary(expenseMovementEntity, accountMovementDto.expenseMovementDto!!.categoryId)
        updatePaymentTypeIfNecessary(expenseMovementEntity, accountMovementDto.expenseMovementDto!!.paymentId)

        return accountMovementRepository.save(accountMovementEntity)
    }

    override fun supports(accountMovementDto: AccountMovementDto): Boolean {
        return accountMovementDto.expenseMovementDto != null
    }

    private fun findExpenseCategory(expenseCategoryId: Long?): ExpenseCategoryEntity {
        return Optional.ofNullable(expenseCategoryId)
            .map { expenseCategoryRepository.findById(it) }
            .orElseThrow { MissingRequiredDataException("Categoria de pagamento não informada.") }
            .orElseThrow { InvalidDataException("Categoria de pagamento não encontrada.") }
    }

    private fun findPaymentType(paymentTypeId: Long?): PaymentTypeEntity {
        return Optional.ofNullable(paymentTypeId)
            .map { paymentTypeRepository.findById(it) }
            .orElseThrow { MissingRequiredDataException("Tipo de pagamento não informado.") }
            .orElseThrow { InvalidDataException("Tipo de pagamento não encontrado.") }
    }

    private fun updateExpenseCategoryIfNecessary(
        expenseMovementEntity: ExpenseMovementEntity,
        expenseCategoryId: Long?
        ) {
        if (expenseMovementEntity.category.id != expenseCategoryId) {
            expenseMovementEntity.category = findExpenseCategory(expenseCategoryId)
        }
    }

    private fun updatePaymentTypeIfNecessary(
        expenseMovementEntity: ExpenseMovementEntity,
        paymentTypeId: Long?
    ) {
        if (expenseMovementEntity.paymentType.id != paymentTypeId) {
            expenseMovementEntity.paymentType = findPaymentType(paymentTypeId)
        }
    }
}