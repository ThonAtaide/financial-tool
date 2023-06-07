package com.kathon.financialtool.unit.domain.service

import com.kathon.financialtool.domain.adapter.service.ExpenseGroupService
import com.kathon.financialtool.domain.adapter.service.FinancialAccountService
import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.PersonNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.mapper.toPersonEntity
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.model.PersonEntity
import com.kathon.financialtool.domain.port.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.repository.PersonRepository
import com.kathon.financialtool.factories.ExpenseGroupFactory.Companion.buildExpenseGroupDto
import com.kathon.financialtool.factories.ExpenseGroupFactory.Companion.buildExpenseGroupEntity
import com.kathon.financialtool.factories.FinancialAccountFactory.Companion.buildFinancialAccountDto
import com.kathon.financialtool.factories.PersonFactory.Companion.buildPersonDto
import com.kathon.financialtool.factories.PersonFactory.Companion.buildPersonEntity
import com.kathon.financialtool.unit.AbstractUnitTest
import com.kathon.financialtool.util.TestUtils.Companion.randomIntBiggerThanZero
import com.kathon.financialtool.util.TestUtils.Companion.randomLongBiggerThanZero
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.util.*

class ExpenseGroupServiceTest : AbstractUnitTest() {

    @MockK
    private lateinit var personRepository: PersonRepository

    @MockK
    private lateinit var expenseGroupRepository: ExpenseGroupRepository

    @MockK
    private lateinit var financialAccountServiceI: FinancialAccountService

    @InjectMockKs
    private lateinit var expenseGroupService: ExpenseGroupService

    @Test
    @DisplayName(
        "Given a valid person id and a expenseGroup with valid data" +
                " When createExpenseGroup method is called" +
                " Then it should create a new expense group indicating person as created by and member."
    )
    fun `test createExpenseGroup method when person id and expense group data is valid`() {
        //given
        val personId = System.currentTimeMillis()
        val newExpenseGroupId = System.currentTimeMillis()
        val expenseGroupDto = buildExpenseGroupDto(id = null)

        val personEntity = mockPersonFindByIdReturningFilledOptional(personId)
        val expenseGroupEntityAfterPersist = mockExpenseGroupSave(newExpenseGroupId, personEntity, expenseGroupDto.name)
        mockFinancialAccountCreation(newExpenseGroupId)

        //when
        val createdExpenseGroup = expenseGroupService.createExpenseGroup(personId, expenseGroupDto)

        //then
        assertThat(createdExpenseGroup).usingRecursiveComparison()
            .isEqualTo(expenseGroupEntityAfterPersist.toExpenseGroupDto())
    }

    @Test
    @DisplayName(
        "Given a none existent person id and a expenseGroup with valid data" +
                " When createExpenseGroup method is called" +
                " Then it should throws PersonNotFoundException."
    )
    fun `test createExpenseGroup method when person id does not exist and expense group data is valid`() {
        //given
        val personId = System.currentTimeMillis()
        val expenseGroupDto = buildExpenseGroupDto(id = null)
        mockPersonFindByIdReturningEmptyOptional(personId)

        //when and then
        assertThrows<PersonNotFoundException> { expenseGroupService.createExpenseGroup(personId, expenseGroupDto) }
    }

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
        val expenseGroupDto = buildExpenseGroupDto(id = expenseGroupId, createdBy = buildPersonDto(personId))
        val expenseGroupEntityExistent = mockExpenseGroupFindById(expenseGroupId, expenseGroupDto)
        val expected = mockExpenseGroupUpdate(
            expenseGroupEntityExistent.copy(
                name = expenseGroupDto.name,
                members = expenseGroupDto.members?.map { it.toPersonEntity() }.orEmpty().toMutableSet(),
            )
        ).toExpenseGroupDto()

        //when
        val updatedExpenseGroup = expenseGroupService.updateExpenseGroup(personId, expenseGroupId, expenseGroupDto)

        //then
        assertThat(updatedExpenseGroup)
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
        val expenseGroupDto = buildExpenseGroupDto(id = expenseGroupId, createdBy = buildPersonDto(personId))
        mockExpenseGroupFindByIdReturningEmptyOptional(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> {
            expenseGroupService.updateExpenseGroup(
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
        val invalidPersonId = randomLongBiggerThanZero()
        val expenseGroupId = System.currentTimeMillis()
        val expenseGroupDto = buildExpenseGroupDto(id = expenseGroupId, createdBy = buildPersonDto(personId))
        mockExpenseGroupFindById(expenseGroupId, expenseGroupDto)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            expenseGroupService.updateExpenseGroup(
                invalidPersonId,
                expenseGroupId,
                expenseGroupDto
            )
        }
    }

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
        val currentPage = expenseGroupService.getUserExpenseGroups(personId, pageSize, pageNumber)

        //then
        assertThat(currentPage).usingRecursiveComparison().isEqualTo(expectedPage)
    }

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
            mockGetFinancialAccountsByExpenseGroupFromService(expenseGroupId!!)
        val expectedExpenseGroup =
            mockExpenseGroupFindById(expenseGroupId = expenseGroupId, expenseGroupDto = expenseGroupDto)
                .toExpenseGroupDto(expenseGroupAccountList)

        //when
        val currentExpenseGroup = expenseGroupService.getUserExpenseGroupById(personId!!, expenseGroupId)

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
            expenseGroupService.getUserExpenseGroupById(
                randomLongBiggerThanZero(),
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
        val expenseGroupId = randomLongBiggerThanZero()
        mockExpenseGroupFindByIdReturningEmptyOptional(expenseGroupId)

        //when then
        assertThrows<ExpenseGroupNotFoundException> {
            expenseGroupService.getUserExpenseGroupById(
                System.currentTimeMillis(),
                expenseGroupId
            )
        }
    }

    @Test
    @DisplayName(
        "Given a valid person id and expenseGroupId" +
                " When deleteUserExpenseGroup is called" +
                " Then it should set group to inactive"
    )
    fun `test delete expense group by id when person is owner`() {
        //given
        val expenseGroupDto = buildExpenseGroupDto()
        val expenseGroupId = expenseGroupDto.id
        val personId = expenseGroupDto.createdBy?.id

        val existentExpenseGroup =
            mockExpenseGroupFindById(expenseGroupId = expenseGroupId!!, expenseGroupDto = expenseGroupDto)
        mockExpenseGroupUpdate(existentExpenseGroup.copy(isActive = false))
            .toExpenseGroupDto()

        //when then
        expenseGroupService.deleteUserExpenseGroup(personId!!, expenseGroupId)
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
        val expenseGroupDto = buildExpenseGroupDto()
        val expenseGroupId = expenseGroupDto.id
        val invalidPersonId = randomLongBiggerThanZero()

        mockExpenseGroupFindById(expenseGroupId!!, expenseGroupDto)

        //when then
        assertThrows<ResourceUnauthorizedException> {
            expenseGroupService.deleteUserExpenseGroup(
                invalidPersonId,
                expenseGroupId
            )
        }
    }

    private fun mockGetFinancialAccountsByExpenseGroupFromService(
        expenseGroupId: Long
    ): MutableList<FinancialAccountDto> {
        val accountList = mutableListOf(
            buildFinancialAccountDto(),
            buildFinancialAccountDto()
        )
        val page = PageImpl(accountList, PageRequest.of(0, 100), 2)
        every {
            financialAccountServiceI
                .getFinancialAccountsByExpenseGroupAndFilter(expenseGroupId = expenseGroupId)
        } returns page
        return accountList
    }

    private fun mockFinancialAccountCreation(expenseGroupId: Long) {
        every { financialAccountServiceI.createFinancialAccount(expenseGroupId) } returns buildFinancialAccountDto()
    }

    private fun mockPersonFindByIdReturningEmptyOptional(personId: Long) {
        every { personRepository.findById(eq(personId)) } returns Optional.empty()
    }

    private fun mockPersonFindByIdReturningFilledOptional(personId: Long): PersonEntity {
        val currentUser = buildPersonEntity(id = personId)
        every { personRepository.findById(eq(personId)) } returns Optional.of(currentUser)
        return currentUser
    }

    private fun mockExpenseGroupSave(
        newExpenseGroupId: Long,
        personEntity: PersonEntity,
        expenseGroupName: String
    ): ExpenseGroupEntity {
        val expenseGroupEntityBeforePersist = ExpenseGroupEntity(
            name = expenseGroupName,
            members = mutableSetOf(personEntity)
        )
        val expenseGroupEntityAfterPersist = expenseGroupEntityBeforePersist.copy(
            id = newExpenseGroupId,
            updatedAt = Instant.now(),
        )
        every {
            expenseGroupRepository
                .save(eq(expenseGroupEntityBeforePersist))
        } returns expenseGroupEntityAfterPersist
        return expenseGroupEntityAfterPersist
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

    private fun mockExpenseGroupFindAllThatPersonIsMember(
        personId: Long,
        pageSize: Int,
        pageNumber: Int
    ): Page<ExpenseGroupEntity> {
        val createdBy = buildPersonEntity(personId)
        val pageRequest = PageRequest.of(pageNumber, pageSize)
        val pageReturned = PageImpl(
            mutableListOf(
                buildExpenseGroupEntity(createdBy = createdBy),
                buildExpenseGroupEntity(createdBy = createdBy),
                buildExpenseGroupEntity(createdBy = createdBy),
                buildExpenseGroupEntity(createdBy = createdBy),
                buildExpenseGroupEntity(createdBy = createdBy),
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