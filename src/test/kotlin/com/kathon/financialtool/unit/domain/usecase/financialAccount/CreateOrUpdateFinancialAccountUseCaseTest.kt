package com.kathon.financialtool.unit.domain.usecase.financialAccount

import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.out.repository.FinancialAccountRepository
import com.kathon.financialtool.domain.usecase.financialAccount.CreateOrUpdateFinancialAccountUseCase
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
import java.time.Instant
import java.util.*

class CreateOrUpdateFinancialAccountUseCaseTest: AbstractUnitTest() {

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @MockK
    private lateinit var financialAccountRepository: FinancialAccountRepository

    @InjectMockKs
    private lateinit var createOrUpdateFinancialAccountUseCase: CreateOrUpdateFinancialAccountUseCase

    @Test
    @DisplayName(
        "Given a existent expenseGroupId from expense group recently generated " +
                "When createFinancialAccount method is called " +
                "Then it should create a first financial account into expense group with default name, " +
                "allowing user to start insert his/her expenses " +
                "And return some info about new financialAccount"
    )
    fun `test financial account creation based only in expense group id when group is found`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupEntity = mockExpenseGroupFindByIdSuccessfully(expenseGroupId)
        val expected = mockFinancialAccountSave(accountId, expenseGroupEntity)

        //when
        val createdFinancialAccount = createOrUpdateFinancialAccountUseCase.createDefaultFinancialAccount(expenseGroupId)

        Assertions.assertThat(createdFinancialAccount).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a non existent expenseGroupId " +
                "When createFinancialAccount method is called " +
                "Then it should throws ExpenseGroupNotFoundException"
    )
    fun `test financial account creation based only in expense group id when group is not found`() {
        //given
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        mockExpenseGroupFindByIdOptionalEmpty(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> { createOrUpdateFinancialAccountUseCase.createDefaultFinancialAccount(expenseGroupId) }
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId from expense group and a financialAccountDto with valid data " +
                "When createFinancialAccount method is called " +
                "Then it should create a financial account into expense group with informed name " +
                "allowing user to start insert his/her expenses " +
                "And return some info about new financialAccount"
    )
    fun `test financial account creation based expense group id and financialAccount dto when group exists`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        val expenseGroupEntity = mockExpenseGroupFindByIdSuccessfully(expenseGroupId)
        val financialAccountDto = FinancialAccountFactory.buildFinancialAccountDto(expenseGroupId)
        financialAccountDto.createdBy = null
        val expected = mockFinancialAccountSave(
            accountId,
            expenseGroupEntity,
            accountName = financialAccountDto.name,
        )

        //when
        val createdFinancialAccount =
            createOrUpdateFinancialAccountUseCase.createFinancialAccount(expenseGroupId, financialAccountDto)

        Assertions.assertThat(createdFinancialAccount).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a non existent expenseGroupId and a financialAccountDto with valid data" +
                "When createFinancialAccount method is called " +
                "Then it should throws ExpenseGroupNotFoundException"
    )
    fun `test financial account creation based expense group id and financialAccount dto when group is not found`() {
        //given
        val expenseGroupId: Long = TestUtils.randomLongBiggerThanZero()
        mockExpenseGroupFindByIdOptionalEmpty(expenseGroupId)
        val financialAccountDto = FinancialAccountFactory.buildFinancialAccountDto(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> {
            createOrUpdateFinancialAccountUseCase.createFinancialAccount(
                expenseGroupId,
                financialAccountDto
            )
        }
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a personId from fin account creator and a financialAccountDto with valid data" +
                "When updateFinancialAccount method is called " +
                "Then the financial account should be updated " +
                "And returned with new data"
    )
    fun `test financial account update when financial account exists`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        val existentFinancialAccount = mockFinancialAccountFindByIdSuccessfully(accountId)
        existentFinancialAccount.name = UUID.randomUUID().toString()
        val financialAccountDto = existentFinancialAccount.toFinancialAccountDto()
        val expected = mockFinancialAccountSave(existentFinancialAccount)
        val personId: Long? = existentFinancialAccount.createdBy?.id

        //when
        val actual =
            createOrUpdateFinancialAccountUseCase.updateFinancialAccount(personId!!, accountId, financialAccountDto)

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a financialAccountDto with valid data and a person id different of fin account creator" +
                "When updateFinancialAccount method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test financial account update when financial account exists but person is not the creator`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        val existentFinancialAccount = mockFinancialAccountFindByIdSuccessfully(accountId)
        existentFinancialAccount.name = UUID.randomUUID().toString()
        val financialAccountDto = existentFinancialAccount.toFinancialAccountDto()
        val personId = TestUtils.randomLongBiggerThanZero()

        //when then
        assertThrows<ResourceUnauthorizedException> {
            createOrUpdateFinancialAccountUseCase.updateFinancialAccount(
                personId,
                accountId,
                financialAccountDto
            )
        }
    }

    @Test
    @DisplayName(
        "Given a non existent financialAccountId and a financialAccountDto with valid data" +
                "When updateFinancialAccount method is called " +
                "Then it should throws FinancialAccountNotFoundException"
    )
    fun `test financial account update when financial account is not found`() {
        //given
        val accountId: Long = TestUtils.randomLongBiggerThanZero()
        mockFinancialAccountFindByIdOptionalEmpty(accountId)
        val financialAccountDto = FinancialAccountFactory.buildFinancialAccountDto()
        val personId = TestUtils.randomLongBiggerThanZero()

        //when then
        assertThrows<FinancialAccountNotFoundException> {
            createOrUpdateFinancialAccountUseCase.updateFinancialAccount(
                personId,
                accountId,
                financialAccountDto
            )
        }
    }

    private fun mockFinancialAccountSave(financialAccountEntity: FinancialAccountEntity): FinancialAccountEntity {
        val finAccount = financialAccountEntity.copy(updatedAt = Instant.now())
        every { financialAccountRepository.save(financialAccountEntity) } returns finAccount
        return finAccount
    }

    private fun mockFinancialAccountSave(
        accountId: Long,
        expenseGroupEntity: ExpenseGroupEntity,
        createdBy: PersonEntity? = null,
        accountName: String = CreateOrUpdateFinancialAccountUseCase.EXPENSE_GROUP_FINANCIAL_ACCNT_DEFAULT_NAME,
        createdAt: Instant? = null,
        updatedAt: Instant? = null
    ): FinancialAccountEntity {
        val financialAccountBeforeSave = FinancialAccountEntity(
            name = accountName,
            createdBy = createdBy,
            expenseGroupEntity = expenseGroupEntity,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
        val financialAccountAfterSave =
            financialAccountBeforeSave.copy(id = accountId, createdAt = Instant.now(), updatedAt = Instant.now())

        every { financialAccountRepository.save(financialAccountBeforeSave) } returns financialAccountAfterSave

        return financialAccountAfterSave
    }

    private fun mockExpenseGroupFindByIdSuccessfully(expenseGroupId: Long): ExpenseGroupEntity {
        val expenseGroup = ExpenseGroupFactory.buildExpenseGroupEntity(expenseGroupId)
        every { expenseGroupRepository.findById(expenseGroupId) } returns Optional.of(expenseGroup)
        return expenseGroup
    }

    private fun mockExpenseGroupFindByIdOptionalEmpty(expenseGroupId: Long) {
        every { expenseGroupRepository.findById(expenseGroupId) } returns Optional.empty()
    }

    private fun mockFinancialAccountFindByIdSuccessfully(accountId: Long): FinancialAccountEntity {
        val financialAccountEntity = FinancialAccountFactory.buildFinancialAccountEntity(accountId)
        every { financialAccountRepository.findById(accountId) } returns Optional.of(financialAccountEntity)
        return financialAccountEntity
    }

    private fun mockFinancialAccountFindByIdOptionalEmpty(accountId: Long) {
        every { financialAccountRepository.findById(accountId) } returns Optional.empty()
    }

}