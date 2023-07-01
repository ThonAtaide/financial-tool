package com.kathon.financialtool.unit.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.mapper.toPersonEntity
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.out.repository.PersonRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.UpdateExpenseGroupUseCase
import com.kathon.financialtool.factories.ExpenseGroupFactory
import com.kathon.financialtool.factories.PersonFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class UpdateExpenseGroupUseCaseTest : AbstractUnitTest() {

    @MockK
    private lateinit var personRepository: PersonRepository

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @InjectMockKs
    private lateinit var updateExpenseGroupUseCase: UpdateExpenseGroupUseCase

    @Test
    @DisplayName(
        "Given a expenseGroup with valid data, " +
                " the expenseGroup id and person id of expenseGroup creator" +
                " When updateExpenseGroup method is called" +
                " Then it should update the expense group without change created by" +
                " and update members if necessary."
    )
    fun `test updateExpenseGroup method when person id and expense group data is valid`() {
        //given
        val personId = System.currentTimeMillis()
        val expenseGroupId = System.currentTimeMillis()
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto(
            id = expenseGroupId,
            createdBy = PersonFactory.buildPersonDto(personId)
        )
        val expenseGroupEntityExistent = mockExpenseGroupFindById(expenseGroupId, expenseGroupDto)
        val expected = mockExpenseGroupUpdate(
            expenseGroupEntityExistent.copy(
                name = expenseGroupDto.name,
                members = expenseGroupDto.members?.map { it.toPersonEntity() }.orEmpty().toMutableSet(),
            )
        ).toExpenseGroupDto()

        //when
        val updatedExpenseGroup =
            updateExpenseGroupUseCase.updateExpenseGroup(personId, expenseGroupId, expenseGroupDto)

        //then
        Assertions.assertThat(updatedExpenseGroup)
            .usingRecursiveComparison().isEqualTo(expected)
    }


    @Test
    @DisplayName(
        "Given a expenseGroup with valid data, " +
                " and expenseGroup id that does not exist " +
                " and a person id " +
                " When updateExpenseGroup method is called" +
                " Then it should throws ExpenseGroupNotFoundException"
    )
    fun `test updateExpenseGroup method when expense group does not exist`() {
        //given
        val personId = System.currentTimeMillis()
        val expenseGroupId = System.currentTimeMillis()
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto(
            id = expenseGroupId,
            createdBy = PersonFactory.buildPersonDto(personId)
        )
        mockExpenseGroupFindByIdReturningEmptyOptional(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> {
            updateExpenseGroupUseCase.updateExpenseGroup(
                personId,
                expenseGroupId,
                expenseGroupDto
            )
        }
    }

    @Test
    @DisplayName(
        "Given a expenseGroup with valid data, " +
                " the expenseGroup id and person id of expenseGroup creator" +
                " When updateExpenseGroup method is called" +
                " Then it should throws ResourceUnauthorizedException"
    )
    fun `test updateExpenseGroup method when person id is not the author of group`() {
        //given
        val personId = System.currentTimeMillis()
        val invalidPersonId = TestUtils.randomLongBiggerThanZero()
        val expenseGroupId = System.currentTimeMillis()
        val expenseGroupDto = ExpenseGroupFactory.buildExpenseGroupDto(
            id = expenseGroupId,
            createdBy = PersonFactory.buildPersonDto(personId)
        )
        mockExpenseGroupFindById(expenseGroupId, expenseGroupDto)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            updateExpenseGroupUseCase.updateExpenseGroup(
                invalidPersonId,
                expenseGroupId,
                expenseGroupDto
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

    private fun mockExpenseGroupFindByIdReturningEmptyOptional(expenseGroupId: Long) {
        every { expenseGroupRepository.findById(expenseGroupId) } returns Optional.empty()
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