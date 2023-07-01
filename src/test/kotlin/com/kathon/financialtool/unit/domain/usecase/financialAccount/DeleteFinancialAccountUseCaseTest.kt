package com.kathon.financialtool.unit.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.usecase.financialAccount.DeleteFinancialAccountUseCase
import com.kathon.financialtool.factories.FinancialAccountFactory
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

class DeleteFinancialAccountUseCaseTest: AbstractUnitTest() {

    @MockK
    private lateinit var financialAccountRepository: FinancialAccountRepository

    @InjectMockKs
    private lateinit var deleteFinancialAccountUseCase: DeleteFinancialAccountUseCase

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a personId from fin account creator and a financialAccountDto with valid data" +
                "When deleteFinancialAccount method is called " +
                "Then the financial account active column should be updated to false"
    )
    fun `test financial account delete when financial account exists`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        val existentFinancialAccount = mockFinancialAccountFindByIdSuccessfully(accountId)
        mockFinancialAccountSave(existentFinancialAccount.copy(isActive = false))
        val personId: Long? = existentFinancialAccount.createdBy?.id

        //when Then
        deleteFinancialAccountUseCase.deleteFinancialAccount(personId!!, accountId)
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a financialAccountDto with valid data and a person id different of fin account creator" +
                "When deleteFinancialAccount method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test financial account delete when financial account exists but person is not the creator`() {
        //given
        val personId = TestUtils.randomLongBiggerThanZero()
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        mockFinancialAccountFindByIdSuccessfully(accountId)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            deleteFinancialAccountUseCase.deleteFinancialAccount(
                personId,
                accountId
            )
        }
    }

    @Test
    @DisplayName(
        "Given a non existent financialAccountId and a financialAccountDto with valid data" +
                "When deleteFinancialAccount method is called " +
                "Then it should throws FinancialAccountNotFoundException"
    )
    fun `test financial account delete when financial account is not found`() {
        //given
        val personId = TestUtils.randomLongBiggerThanZero()
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        mockFinancialAccountFindByIdOptionalEmpty(accountId)

        //when then
        assertThrows<FinancialAccountNotFoundException> {
            deleteFinancialAccountUseCase.deleteFinancialAccount(
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

    private fun mockFinancialAccountSave(financialAccountEntity: FinancialAccountEntity): FinancialAccountEntity {
        val finAccount = financialAccountEntity.copy(updatedAt = Instant.now())
        every { financialAccountRepository.save(financialAccountEntity) } returns finAccount
        return finAccount
    }
}