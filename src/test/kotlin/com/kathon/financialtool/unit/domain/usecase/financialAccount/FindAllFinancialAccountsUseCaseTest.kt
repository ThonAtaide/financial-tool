package com.kathon.financialtool.unit.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupsByPersonUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import com.kathon.financialtool.factories.ExpenseGroupFactory
import com.kathon.financialtool.factories.FinancialAccountFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllFinancialAccountsUseCaseTest : AbstractUnitTest() {

    @MockK
    private lateinit var findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase

    @MockK
    private lateinit var financialAccountRepository: FinancialAccountRepository

    @InjectMockKs
    private lateinit var findAllFinancialAccountsUseCase: FindAllFinancialAccountsUseCase

    @Test
    @DisplayName(
        "Given an existent expenseGroupId" +
                "When getFinancialAccountsByExpenseGroupAndFilter method is called " +
                "Then it should returns a list of active accounts of group"
    )
    fun `test get financial accounts by expense group`() {
        //given
        val personId = TestUtils.randomLongBiggerThanZero()
        val pageNumber = 1
        val pageSize = 1
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val pageRequest = PageRequest.of(pageNumber, pageSize)
        val expected =
            mockFinancialAccountRepositoryFindAccountsByExpenseGroups(pageRequest)
        mockFindExpenseGroupsByPersonUseCaseWhenPersonHasAccessToExpenseGroup(personId, expenseGroupId)

        //when
        val actual = findAllFinancialAccountsUseCase
            .findFinancialAccountsBy(
                personId,
                expenseGroupId = expenseGroupId,
                pageable = pageRequest,
            )
        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given an existent expenseGroupId, creatorId and a person id" +
                "When getFinancialAccountsByExpenseGroupAndFilter method is called " +
                "Then it should returns a list of active accounts of group that belongs to the creatorId"
    )
    fun `test get financial accounts by expense group and author when person has access to expense group`() {
        //given
        val personId: Long = TestUtils.randomLongBiggerThanZero()
        val creatorId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val pageRequest = PageRequest.of(0, 100, Sort.Direction.ASC, "name")
        val expected =
            mockFinancialAccountRepositoryFindAccountsByExpenseGroupsAndPersonId(pageRequest)
        mockFindExpenseGroupsByPersonUseCaseWhenPersonHasAccessToExpenseGroup(personId, expenseGroupId)
        //when
        val actual = findAllFinancialAccountsUseCase
            .findFinancialAccountsBy(
                personId,
                createdBy = creatorId,
                expenseGroupId = expenseGroupId,
                pageable = pageRequest
            )
        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given an existent expenseGroupId and a person id that do not belongs to expense group" +
                "When getFinancialAccountsByExpenseGroupAndFilter method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test get financial accounts by expense group, author and person when person has not access to resource`() {
        //given
        val personId: Long = TestUtils.randomLongBiggerThanZero()
        val creatorId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val pageRequest = PageRequest.of(0, 100, Sort.Direction.ASC, "name")
        mockFindExpenseGroupsByPersonUseCaseWhenPersonHasNotAccessToExpenseGroup(personId)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            findAllFinancialAccountsUseCase
                .findFinancialAccountsBy(
                    personId,
                    createdBy = creatorId,
                    expenseGroupId = expenseGroupId,
                    pageable = pageRequest
                )
        }

    }

    private fun mockFindExpenseGroupsByPersonUseCaseWhenPersonHasAccessToExpenseGroup(
        personId: Long, firstGroupId: Long
    ) {
        val page = PageImpl(
            mutableListOf(
                ExpenseGroupFactory.buildExpenseGroupEntity(id = firstGroupId),
                ExpenseGroupFactory.buildExpenseGroupEntity(),
                ExpenseGroupFactory.buildExpenseGroupEntity(),
            )
        )
        every { findExpenseGroupsByPersonUseCase.findExpenseGroupsByUser(personId, any()) } returns page
    }

    private fun mockFindExpenseGroupsByPersonUseCaseWhenPersonHasNotAccessToExpenseGroup(
        personId: Long
    ) {
        val page = PageImpl(
            mutableListOf(
                ExpenseGroupFactory.buildExpenseGroupEntity(),
                ExpenseGroupFactory.buildExpenseGroupEntity(),
                ExpenseGroupFactory.buildExpenseGroupEntity(),
            )
        )
        every { findExpenseGroupsByPersonUseCase.findExpenseGroupsByUser(personId, any()) } returns page
    }

    private fun mockFinancialAccountRepositoryFindAccountsByExpenseGroupsAndPersonId(
        pageRequest: PageRequest
    ): PageImpl<FinancialAccountEntity> {
        val financialAccountEntities = listOf(
            FinancialAccountFactory.buildFinancialAccountEntity(TestUtils.randomLongBiggerThanZero()),
            FinancialAccountFactory.buildFinancialAccountEntity(TestUtils.randomLongBiggerThanZero()),
        )

        val page = PageImpl(financialAccountEntities, pageRequest, financialAccountEntities.size.toLong())
        every {
            financialAccountRepository
                .findAll(any(), pageRequest)
        } returns page
        return page
    }

    private fun mockFinancialAccountRepositoryFindAccountsByExpenseGroups(
        pageRequest: PageRequest
    ): PageImpl<FinancialAccountEntity> {
        val financialAccountEntities = listOf(
            FinancialAccountFactory.buildFinancialAccountEntity(TestUtils.randomLongBiggerThanZero()),
        )

        val page = PageImpl(financialAccountEntities, pageRequest, financialAccountEntities.size.toLong())
        every {
            financialAccountRepository.findAll(any(), pageRequest)
        } returns page
        return page
    }
}