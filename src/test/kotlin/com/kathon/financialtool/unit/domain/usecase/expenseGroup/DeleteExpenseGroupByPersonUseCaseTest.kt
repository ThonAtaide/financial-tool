package com.kathon.financialtool.unit.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.mapper.toPersonEntity
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.DeleteExpenseGroupByPersonUseCase
import com.kathon.financialtool.factories.ExpenseGroupFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class DeleteExpenseGroupByPersonUseCaseTest: AbstractUnitTest() {

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @InjectMockKs
    private lateinit var deleteExpenseGroupByPersonUseCase: DeleteExpenseGroupByPersonUseCase

    @Test
    @DisplayName(
        "Given a valid person id and expenseGroupId" +
                " When deleteUserExpenseGroup is called" +
                " Then it should set group to inactive"
    )
    fun `test delete expense group by id when person is owner`() {
        //given
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto()
        val expenseGroupId = expenseGroupDto.id
        val personId = expenseGroupDto.createdBy?.id

        val existentExpenseGroup =
            mockExpenseGroupFindById(expenseGroupId = expenseGroupId!!, expenseGroupDto = expenseGroupDto)
        mockExpenseGroupUpdate(existentExpenseGroup.copy(isActive = false))
            .toExpenseGroupDto()

        //when then
        deleteExpenseGroupByPersonUseCase.deletePersonExpenseGroup(personId!!, expenseGroupId)
    }

    @Test
    @DisplayName(
        "Given expenseGroupId " +
                " and a person id different of expense group created by" +
                " When deleteUserExpenseGroup is called" +
                " Then it should throws ResourceUnauthorizedException"
    )
    fun `test delete expense group by id when person is not owner`() {
        //given
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto()
        val expenseGroupId = expenseGroupDto.id
        val invalidPersonId = TestUtils.randomLongBiggerThanZero()

        mockExpenseGroupFindById(expenseGroupId!!, expenseGroupDto)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            deleteExpenseGroupByPersonUseCase.deletePersonExpenseGroup(
                invalidPersonId,
                expenseGroupId
            )
        }
    }

    private fun mockExpenseGroupUpdate(
        expenseGroupEntityToSave: ExpenseGroupEntity
    ): ExpenseGroupEntity {
        val expenseGroupReturnedBySave = expenseGroupEntityToSave.copy(
            updatedAt = Instant.now()
        )
        every { expenseGroupRepository.save(eq(expenseGroupEntityToSave)) } returns expenseGroupReturnedBySave
        return expenseGroupReturnedBySave
    }

    private fun mockExpenseGroupFindById(
        expenseGroupId: Long,
        expenseGroupDto: ExpenseGroupDto
    ): ExpenseGroupEntity {
        val createdBy = expenseGroupDto.createdBy?.toPersonEntity()
        val expenseGroupEntityFound = ExpenseGroupEntity(
            id = expenseGroupDto.id,
            name = UUID.randomUUID().toString(),
            createdBy = createdBy,
            members = mutableSetOf(createdBy!!),
            createdAt = expenseGroupDto.createdAt,
            updatedAt = expenseGroupDto.updatedAt
        )
        every { expenseGroupRepository.findById(eq(expenseGroupId)) } returns Optional.of(expenseGroupEntityFound)
        return expenseGroupEntityFound
    }
}