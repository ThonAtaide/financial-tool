package com.kathon.financialtool.unit.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import com.kathon.financialtool.factories.ExpenseGroupFactory
import com.kathon.financialtool.factories.PersonFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils.Companion.randomIntBiggerThanZero
import com.kathon.financialtool.util.TestUtils.Companion.randomLongBiggerThanZero
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class FindExpenseGroupsByPersonUseCaseTest: AbstractUnitTest() {

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @InjectMockKs
    private lateinit var findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase

    @Test
    @DisplayName(
        "Given a person id and a pageable" +
                " When getPersonExpenseGroups is called" +
                " Then it should return a pageable with the groups that person is member."
    )
    fun `test find expense groups that person is member`() {
        //given
        val personId = randomLongBiggerThanZero()
        val pageSize = randomIntBiggerThanZero()
        val pageNumber = randomIntBiggerThanZero()
        val expectedPage = mockExpenseGroupFindAllThatPersonIsMember(personId, pageSize, pageNumber)
            .map { it.toExpenseGroupDto() }

        //when
        val currentPage =
            findExpenseGroupsByPersonUseCase.findExpenseGroupsByUser(personId, PageRequest.of(pageNumber, pageSize))

        //then
        assertThat(currentPage).usingRecursiveComparison().isEqualTo(expectedPage)
    }

    private fun mockExpenseGroupFindAllThatPersonIsMember(
        personId: Long,
        pageSize: Int,
        pageNumber: Int
    ): Page<ExpenseGroupEntity> {
        val createdBy = PersonFactory.buildPersonEntity(personId)
        val pageRequest = PageRequest.of(pageNumber, pageSize)
        val pageReturned = PageImpl(
            mutableListOf(
                ExpenseGroupFactory.buildExpenseGroupEntity(createdBy = createdBy),
                ExpenseGroupFactory.buildExpenseGroupEntity(createdBy = createdBy),
                ExpenseGroupFactory.buildExpenseGroupEntity(createdBy = createdBy),
                ExpenseGroupFactory.buildExpenseGroupEntity(createdBy = createdBy),
                ExpenseGroupFactory.buildExpenseGroupEntity(createdBy = createdBy),
            ),
            pageRequest,
            (pageSize * pageNumber).toLong()
        )
        every {
            expenseGroupRepository.findExpenseGroupEntitiesByMembersContaining(
                personId,
                pageRequest
            )
        } returns pageReturned
        return pageReturned
    }
}