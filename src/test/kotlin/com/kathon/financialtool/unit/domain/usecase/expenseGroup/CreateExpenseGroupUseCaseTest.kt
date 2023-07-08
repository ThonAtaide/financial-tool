package com.kathon.financialtool.unit.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.exceptions.InvalidDataException
import com.kathon.financialtool.domain.exceptions.PersonNotFoundException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.out.repository.PersonRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.CreateExpenseGroupUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.CreateOrUpdateFinancialAccountUseCase
import com.kathon.financialtool.factories.ExpenseGroupFactory
import com.kathon.financialtool.factories.FinancialAccountFactory
import com.kathon.financialtool.factories.PersonFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class CreateExpenseGroupUseCaseTest: AbstractUnitTest() {

    @MockK
    private lateinit var personRepository: PersonRepository

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @MockK
    private lateinit var financialAccountUseCase: CreateOrUpdateFinancialAccountUseCase

    @InjectMockKs
    private lateinit var createExpenseGroupUseCase: CreateExpenseGroupUseCase

    @Test
    @DisplayName(
        "Given a valid person id and a expenseGroup with valid data" +
                " When createExpenseGroup method is called" +
                " Then it should create a new expense group indicating person as created by and member."
    )
    fun `test createExpenseGroup method when person id and expense group data is valid`() {
        //given
        val personId = System.currentTimeMillis()
        val newExpenseGroupId = System.currentTimeMillis()
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto(id = null)

        val personEntity = mockPersonFindByIdReturningFilledOptional(personId)
        val expenseGroupEntityAfterPersist = mockExpenseGroupSave(newExpenseGroupId, personEntity, expenseGroupDto.name)
        mockFinancialAccountCreation(newExpenseGroupId)

        //when
        val createdExpenseGroup = createExpenseGroupUseCase.createExpenseGroup(personId, expenseGroupDto)

        //then
        Assertions.assertThat(createdExpenseGroup).usingRecursiveComparison()
            .isEqualTo(expenseGroupEntityAfterPersist)
        verify { financialAccountUseCase.createDefaultFinancialAccount(newExpenseGroupId) }
    }

    @Test
    @DisplayName(
        "Given a none existent person id and a expenseGroup with valid data" +
                " When createExpenseGroup method is called" +
                " Then it should throws PersonNotFoundException."
    )
    fun `test createExpenseGroup method when person id does not exist and expense group data is valid`() {
        //given
        val personId = System.currentTimeMillis()
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto(id = null)
        mockPersonFindByIdReturningEmptyOptional(personId)

        //when and then
        assertThrows<InvalidDataException> { createExpenseGroupUseCase.createExpenseGroup(personId, expenseGroupDto) }
        verify(exactly = 0) { financialAccountUseCase.createDefaultFinancialAccount(any()) }
    }

    private fun mockPersonFindByIdReturningFilledOptional(personId: Long): PersonEntity {
        val currentUser = PersonFactory.buildPersonEntity(id = personId)
        every { personRepository.findById(eq(personId)) } returns Optional.of(currentUser)
        return currentUser
    }

    private fun mockExpenseGroupSave(
        newExpenseGroupId: Long,
        personEntity: PersonEntity,
        expenseGroupName: String
    ): ExpenseGroupEntity {
        val expenseGroupEntityBeforePersist = ExpenseGroupEntity(
            name = expenseGroupName,
            members = mutableSetOf(personEntity)
        )
        val expenseGroupEntityAfterPersist = expenseGroupEntityBeforePersist.copy(
            id = newExpenseGroupId,
            updatedAt = Instant.now(),
        )
        every {
            expenseGroupRepository
                .save(eq(expenseGroupEntityBeforePersist))
        } returns expenseGroupEntityAfterPersist
        return expenseGroupEntityAfterPersist
    }

    private fun mockFinancialAccountCreation(expenseGroupId: Long) {
        every { financialAccountUseCase.createDefaultFinancialAccount(expenseGroupId) } returns FinancialAccountFactory.buildFinancialAccountEntity()
    }

    private fun mockPersonFindByIdReturningEmptyOptional(personId: Long) {
        every { personRepository.findById(eq(personId)) } returns Optional.empty()
    }
}