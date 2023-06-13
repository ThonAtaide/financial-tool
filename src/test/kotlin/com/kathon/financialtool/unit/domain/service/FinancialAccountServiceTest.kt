package com.kathon.financialtool.unit.domain.service

import com.kathon.financialtool.domain.adapter.service.FinancialAccountService
import com.kathon.financialtool.domain.adapter.service.FinancialAccountService.Companion.EXPENSE_GROUP_FINANCIAL_ACCNT_DEFAULT_NAME
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toFinancialAccountDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.domain.port.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.repository.FinancialAccountRepository
import com.kathon.financialtool.factories.ExpenseGroupFactory.Companion.buildExpenseGroupEntity
import com.kathon.financialtool.factories.FinancialAccountFactory.Companion.buildFinancialAccountDto
import com.kathon.financialtool.factories.FinancialAccountFactory.Companion.buildFinancialAccountEntity
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils.Companion.randomLongBiggerThanZero
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.util.*

class FinancialAccountServiceTest : AbstractUnitTest() {

    @MockK
    private lateinit var financialAccountRepository: FinancialAccountRepository

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @InjectMockKs
    private lateinit var financialAccountService: FinancialAccountService

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
        val accountId: Long = randomLongBiggerThanZero()
        val expenseGroupId: Long = randomLongBiggerThanZero()
        val expenseGroupEntity = mockExpenseGroupFindByIdSuccessfully(expenseGroupId)
        val expected = mockFinancialAccountSave(accountId, expenseGroupEntity).toFinancialAccountDto()

        //when
        val createdFinancialAccount = financialAccountService.createFinancialAccount(expenseGroupId)

        assertThat(createdFinancialAccount).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a non existent expenseGroupId " +
                "When createFinancialAccount method is called " +
                "Then it should throws ExpenseGroupNotFoundException"
    )
    fun `test financial account creation based only in expense group id when group is not found`() {
        //given
        val expenseGroupId: Long = randomLongBiggerThanZero()
        mockExpenseGroupFindByIdOptionalEmpty(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> { financialAccountService.createFinancialAccount(expenseGroupId) }
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
        val accountId: Long = randomLongBiggerThanZero()
        val expenseGroupId: Long = randomLongBiggerThanZero()
        val expenseGroupEntity = mockExpenseGroupFindByIdSuccessfully(expenseGroupId)
        val financialAccountDto = buildFinancialAccountDto(expenseGroupId)
        financialAccountDto.createdBy = null
        val expected = mockFinancialAccountSave(
            accountId,
            expenseGroupEntity,
            accountName = financialAccountDto.name,
        ).toFinancialAccountDto()

        //when
        val createdFinancialAccount =
            financialAccountService.createFinancialAccount(expenseGroupId, financialAccountDto)

        assertThat(createdFinancialAccount).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a non existent expenseGroupId and a financialAccountDto with valid data" +
                "When createFinancialAccount method is called " +
                "Then it should throws ExpenseGroupNotFoundException"
    )
    fun `test financial account creation based expense group id and financialAccount dto when group is not found`() {
        //given
        val expenseGroupId: Long = randomLongBiggerThanZero()
        mockExpenseGroupFindByIdOptionalEmpty(expenseGroupId)
        val financialAccountDto = buildFinancialAccountDto(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> {
            financialAccountService.createFinancialAccount(
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
        val accountId: Long = randomLongBiggerThanZero()
        val existentFinancialAccount = mockFinancialAccountFindByIdSuccessfully(accountId)
        existentFinancialAccount.name = UUID.randomUUID().toString()
        val financialAccountDto = existentFinancialAccount.toFinancialAccountDto()
        val expected = mockFinancialAccountSave(existentFinancialAccount).toFinancialAccountDto()
        val personId: Long? = existentFinancialAccount.createdBy?.id

        //when
        val actual =
            financialAccountService.updateFinancialAccount(personId!!, accountId, financialAccountDto)

        //then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a financialAccountDto with valid data and a person id different of fin account creator" +
                "When updateFinancialAccount method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test financial account update when financial account exists but person is not the creator`() {
        //given
        val accountId: Long = randomLongBiggerThanZero()
        val existentFinancialAccount = mockFinancialAccountFindByIdSuccessfully(accountId)
        existentFinancialAccount.name = UUID.randomUUID().toString()
        val financialAccountDto = existentFinancialAccount.toFinancialAccountDto()
        val personId = randomLongBiggerThanZero()

        //when then
        assertThrows<ResourceUnauthorizedException> {
            financialAccountService.updateFinancialAccount(
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
        val accountId: Long = randomLongBiggerThanZero()
        mockFinancialAccountFindByIdOptionalEmpty(accountId)
        val financialAccountDto = buildFinancialAccountDto()
        val personId = randomLongBiggerThanZero()

        //when then
        assertThrows<FinancialAccountNotFoundException> {
            financialAccountService.updateFinancialAccount(
                personId,
                accountId,
                financialAccountDto
            )
        }
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId and a personId from an expenseGroup member " +
                "When getFinancialAccountsById method is called " +
                "Then it should returns the financial Account from id"
    )
    fun `test get financial account by Id when person is member from financial account expense group`() {
        //given
        val accountId: Long = randomLongBiggerThanZero()
        val financialAccountEntity = mockFinancialAccountFindByIdSuccessfully(accountId)
        val personId = financialAccountEntity.expenseGroupEntity.members.elementAt(0).id

        //when
        val actual = financialAccountService.getFinancialAccountsById(personId!!, accountId)

        //then
        assertThat(actual).usingRecursiveComparison().isEqualTo(financialAccountEntity.toFinancialAccountDto())
    }

    @Test
    @DisplayName(
        "Given non existent expenseGroupId and a personId from an expenseGroup member " +
                "When getFinancialAccountsById method is called " +
                "Then it should throws FinancialAccountNotFoundException"
    )
    fun `test get financial account by Id when id does not exists`() {
        //given
        val accountId: Long = randomLongBiggerThanZero()
        mockFinancialAccountFindByIdOptionalEmpty(accountId)
        val personId = randomLongBiggerThanZero()

        //when then
        assertThrows<FinancialAccountNotFoundException> {
            financialAccountService.getFinancialAccountsById(
                personId,
                accountId
            )
        }
    }

    @Test
    @DisplayName(
        "Given an existent expenseGroupId and a personId different from an expenseGroup member " +
                "When getFinancialAccountsById method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test get financial account by Id when person is not member from financial account expense group`() {
        //given
        val accountId: Long = randomLongBiggerThanZero()
        mockFinancialAccountFindByIdSuccessfully(accountId)
        val personId = randomLongBiggerThanZero()

        //when then
        assertThrows<ResourceUnauthorizedException> {
            financialAccountService.getFinancialAccountsById(
                personId,
                accountId
            )
        }
    }

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
        val expenseGroupId: Long = randomLongBiggerThanZero()
        val expected =
            mockFinancialAccountRepositoryFindAccountsByExpenseGroups(expenseGroupId)
                .map { it.toFinancialAccountDto() }

        //when
        val actual = financialAccountService
            .getFinancialAccountsByExpenseGroupAndFilter(
                expenseGroupId = expenseGroupId,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
        //then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given an existent expenseGroupId and a person id" +
                "When getFinancialAccountsByExpenseGroupAndFilter method is called " +
                "Then it should returns a list of active accounts of group that person is the creator"
    )
    fun `test get financial accounts by expense group and person`() {
        //given
        val personId: Long = randomLongBiggerThanZero()
        val expenseGroupId: Long = randomLongBiggerThanZero()
        val expected =
            mockFinancialAccountRepositoryFindAccountsByExpenseGroupsAndPersonId(expenseGroupId, personId)
                .map { it.toFinancialAccountDto() }
        //when
        val actual = financialAccountService
            .getFinancialAccountsByExpenseGroupAndFilter(
                createdBy = personId,
                expenseGroupId = expenseGroupId
            )
        //then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a personId from fin account creator and a financialAccountDto with valid data" +
                "When deleteFinancialAccount method is called " +
                "Then the financial account active column should be updated to false"
    )
    fun `test financial account delete when financial account exists`() {
        //given
        val accountId: Long = randomLongBiggerThanZero()
        val existentFinancialAccount = mockFinancialAccountFindByIdSuccessfully(accountId)
        mockFinancialAccountSave(existentFinancialAccount.copy(isActive = false))
        val personId: Long? = existentFinancialAccount.createdBy?.id

        //when Then
        financialAccountService.deleteFinancialAccount(personId!!, accountId)
    }

    @Test
    @DisplayName(
        "Given a existent expenseGroupId, a financialAccountDto with valid data and a person id different of fin account creator" +
                "When deleteFinancialAccount method is called " +
                "Then it should throws ResourceUnauthorizedException"
    )
    fun `test financial account delete when financial account exists but person is not the creator`() {
        //given
        val personId = randomLongBiggerThanZero()
        val accountId: Long = randomLongBiggerThanZero()
        mockFinancialAccountFindByIdSuccessfully(accountId)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            financialAccountService.deleteFinancialAccount(
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
        val personId = randomLongBiggerThanZero()
        val accountId: Long = randomLongBiggerThanZero()
        mockFinancialAccountFindByIdOptionalEmpty(accountId)

        //when then
        assertThrows<FinancialAccountNotFoundException> {
            financialAccountService.deleteFinancialAccount(
                personId,
                accountId
            )
        }
    }

    private fun mockFinancialAccountRepositoryFindAccountsByExpenseGroupsAndPersonId(
        expenseGroupId: Long,
        personId: Long,
    ): PageImpl<FinancialAccountEntity> {
        val financialAccountEntities = listOf(
            buildFinancialAccountEntity(randomLongBiggerThanZero()),
            buildFinancialAccountEntity(randomLongBiggerThanZero()),
        )
        val pageRequest = PageRequest.of(0, 100)
        val page = PageImpl(financialAccountEntities, pageRequest, financialAccountEntities.size.toLong())
        every {
            financialAccountRepository
                .findFinancialAccountEntitiesByExpenseGroupEntityAndCreatedByPerson(
                    eq(expenseGroupId),
                    eq(personId),
                    pageRequest
                )
        } returns page
        return page
    }

    private fun mockFinancialAccountRepositoryFindAccountsByExpenseGroups(
        expenseGroupId: Long,
    ): PageImpl<FinancialAccountEntity> {
        val financialAccountEntities = listOf(
            buildFinancialAccountEntity(randomLongBiggerThanZero()),
        )
        val pageRequest = PageRequest.of(1, 1)
        val page = PageImpl(financialAccountEntities, pageRequest, financialAccountEntities.size.toLong())
        every {
            financialAccountRepository
                .findFinancialAccountEntitiesByExpenseGroupEntity(eq(expenseGroupId), pageRequest)
        } returns page
        return page
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
        accountName: String = EXPENSE_GROUP_FINANCIAL_ACCNT_DEFAULT_NAME,
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
        val expenseGroup = buildExpenseGroupEntity(expenseGroupId)
        every { expenseGroupRepository.findById(expenseGroupId) } returns Optional.of(expenseGroup)
        return expenseGroup
    }

    private fun mockExpenseGroupFindByIdOptionalEmpty(expenseGroupId: Long) {
        every { expenseGroupRepository.findById(expenseGroupId) } returns Optional.empty()
    }

    private fun mockFinancialAccountFindByIdSuccessfully(accountId: Long): FinancialAccountEntity {
        val financialAccountEntity = buildFinancialAccountEntity(accountId)
        every { financialAccountRepository.findById(accountId) } returns Optional.of(financialAccountEntity)
        return financialAccountEntity
    }

    private fun mockFinancialAccountFindByIdOptionalEmpty(accountId: Long) {
        every { financialAccountRepository.findById(accountId) } returns Optional.empty()
    }
}