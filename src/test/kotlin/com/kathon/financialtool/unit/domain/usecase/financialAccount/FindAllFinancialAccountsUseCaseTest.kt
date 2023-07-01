package com.kathon.financialtool.unit.domain.usecase.financialAccount

import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import com.kathon.financialtool.factories.FinancialAccountFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class FindAllFinancialAccountsUseCaseTest : AbstractUnitTest() {

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
        val pageNumber = 1
        val pageSize = 1
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val pageRequest = PageRequest.of(pageNumber, pageSize)
        val expected =
            mockFinancialAccountRepositoryFindAccountsByExpenseGroups(pageRequest)
                .map { it.toFinancialAccountDto() }

        //when
        val actual = findAllFinancialAccountsUseCase
            .findFinancialAccountsBy(
                expenseGroupId = expenseGroupId,
                pageable = pageRequest,
            )
        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given an existent expenseGroupId and a person id" +
                "When getFinancialAccountsByExpenseGroupAndFilter method is called " +
                "Then it should returns a list of active accounts of group that person is the creator"
    )
    fun `test get financial accounts by expense group and person`() {
        //given
        val personId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val pageRequest = PageRequest.of(0, 100, Sort.Direction.ASC, "name")
        val expected =
            mockFinancialAccountRepositoryFindAccountsByExpenseGroupsAndPersonId(pageRequest)
                .map { it.toFinancialAccountDto() }
        //when
        val actual = findAllFinancialAccountsUseCase
            .findFinancialAccountsBy(
                createdBy = personId,
                expenseGroupId = expenseGroupId,
                pageable = pageRequest
            )
        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
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