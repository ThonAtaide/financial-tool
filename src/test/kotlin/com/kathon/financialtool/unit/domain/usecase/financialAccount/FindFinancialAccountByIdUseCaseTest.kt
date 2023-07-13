package com.kathon.financialtool.unit.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.usecase.financialAccount.FindFinancialAccountByIdUseCase
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
import java.util.*

class FindFinancialAccountByIdUseCaseTest : AbstractUnitTest() {

    @MockK
    private lateinit var financialAccountRepository: FinancialAccountRepository

    @InjectMockKs
    private lateinit var findFinancialAccountByIdUseCase: FindFinancialAccountByIdUseCase

    @Test
    @DisplayName(
        "Given a existent expenseGroupId and a personId from an expenseGroup member " +
                "When getFinancialAccountsById method is called " +
                "Then it should returns the financial Account from id"
    )
    fun `test get financial account by Id when person is member from financial account expense group`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        val financialAccountEntity = mockFinancialAccountFindByIdSuccessfully(accountId)
        val personId = financialAccountEntity.expenseGroupEntity.members.elementAt(0).id

        //when
        val actual = findFinancialAccountByIdUseCase.findFinancialAccountById(personId!!, accountId)
            .get()

        //then
        assertThat(actual)
            .usingRecursiveComparison().isEqualTo(financialAccountEntity)
    }

    @Test
    @DisplayName(
        "Given non existent expenseGroupId and a personId from an expenseGroup member " +
                "When getFinancialAccountsById method is called " +
                "Then it should throws FinancialAccountNotFoundException"
    )
    fun `test get financial account by Id when id does not exists`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        mockFinancialAccountFindByIdOptionalEmpty(accountId)
        val personId = TestUtils.randomLongBiggerThanZero()

        assertThat(
            findFinancialAccountByIdUseCase.findFinancialAccountById(
                personId,
                accountId
            ).isEmpty
        ).isTrue
    }

    @Test
    @DisplayName(
        "Given an existent expenseGroupId and a personId different from an expenseGroup member " +
                "When getFinancialAccountsById method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test get financial account by Id when person is not member from financial account expense group`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        mockFinancialAccountFindByIdSuccessfully(accountId)
        val personId = TestUtils.randomLongBiggerThanZero()

        //when then
        assertThrows<ResourceUnauthorizedException> {
            findFinancialAccountByIdUseCase.findFinancialAccountById(
                personId,
                accountId
            )
        }
    }

    private fun mockFinancialAccountFindByIdOptionalEmpty(accountId: Long) {
        every { financialAccountRepository.findById(accountId) } returns Optional.empty()
    }

    private fun mockFinancialAccountFindByIdSuccessfully(accountId: Long): FinancialAccountEntity {
        val financialAccountEntity = FinancialAccountFactory.buildFinancialAccountEntity(accountId)
        every { financialAccountRepository.findById(accountId) } returns Optional.of(financialAccountEntity)
        return financialAccountEntity
    }
}