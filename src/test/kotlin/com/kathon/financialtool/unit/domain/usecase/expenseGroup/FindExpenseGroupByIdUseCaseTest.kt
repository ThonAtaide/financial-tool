package com.kathon.financialtool.unit.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.mapper.toPersonEntity
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.usecase.expenseGroup.FindExpenseGroupByIdUseCase
import com.kathon.financialtool.domain.usecase.financialAccount.FindAllFinancialAccountsUseCase
import com.kathon.financialtool.factories.ExpenseGroupFactory.Companion.buildExpenseGroupDto
import com.kathon.financialtool.factories.FinancialAccountFactory
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

class FindExpenseGroupByIdUseCaseTest : AbstractUnitTest() {

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @MockK
    private lateinit var findAllFinancialAccountsUseCase: FindAllFinancialAccountsUseCase

    @InjectMockKs
    private lateinit var findExpenseGroupByIdUseCase: FindExpenseGroupByIdUseCase

    @Test
    @DisplayName(
        "Given a valid expenseGroupId and a personId that from a group member" +
                " When getPersonExpenseGroup is called" +
                " Then it should return the group"
    )
    fun `test find expense group by id when person is member`() {
        //given
        val expenseGroupDto = buildExpenseGroupDto()
        val expenseGroupId = expenseGroupDto.id
        val personId = expenseGroupDto.createdBy?.id
        val expenseGroupAccountList =
            mockGetFinancialAccountsByExpenseGroupFromService(personId!!, expenseGroupId!!)
        val expectedExpenseGroup =
            mockExpenseGroupFindById(expenseGroupId = expenseGroupId, expenseGroupDto = expenseGroupDto)
        expectedExpenseGroup.finAccountList = expenseGroupAccountList

        //when
        val currentExpenseGroup = findExpenseGroupByIdUseCase.findUserExpenseGroupsById(personId, expenseGroupId)
            .get()

        //then
        assertThat(currentExpenseGroup).usingRecursiveComparison().isEqualTo(expectedExpenseGroup)
    }

    @Test
    @DisplayName(
        "Given a valid expenseGroupId and a personId that is not from a group member" +
                " When getPersonExpenseGroup is called" +
                " Then it should throws ResourceUnauthorizedException"
    )
    fun `test find expense group by id when person is not member`() {
        //given
        val expenseGroupDto = buildExpenseGroupDto()
        val expenseGroupId = expenseGroupDto.id

        mockExpenseGroupFindById(expenseGroupId = expenseGroupId!!, expenseGroupDto = expenseGroupDto)
            .toExpenseGroupDto()

        //when then
        assertThrows<ResourceUnauthorizedException> {
            findExpenseGroupByIdUseCase.findUserExpenseGroupsById(
                TestUtils.randomLongBiggerThanZero(),
                expenseGroupId
            )
        }
    }

    @Test
    @DisplayName(
        "Given a none existent expenseGroupId" +
                " When getPersonExpenseGroup is called" +
                " Then it should throws ExpenseGroupNotFoundException"
    )
    fun `test find expense group by id when group does not exist`() {
        //given
        val expenseGroupId = TestUtils.randomLongBiggerThanZero()
        mockExpenseGroupFindByIdReturningEmptyOptional(expenseGroupId)

        assertThat(
            findExpenseGroupByIdUseCase.findUserExpenseGroupsById(
                System.currentTimeMillis(),
                expenseGroupId
            ).isEmpty
        ).isTrue
    }

    private fun mockGetFinancialAccountsByExpenseGroupFromService(
        personId: Long,
        expenseGroupId: Long
    ): MutableList<FinancialAccountEntity> {
        val accountList = mutableListOf(
            FinancialAccountFactory.buildFinancialAccountEntity(),
            FinancialAccountFactory.buildFinancialAccountEntity()
        )
        val page = PageImpl(accountList, PageRequest.of(0, 100), 2)
        every {
            findAllFinancialAccountsUseCase
                .findFinancialAccountsBy(personId, expenseGroupId = expenseGroupId, pageable = any())
        } returns page
        return accountList
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