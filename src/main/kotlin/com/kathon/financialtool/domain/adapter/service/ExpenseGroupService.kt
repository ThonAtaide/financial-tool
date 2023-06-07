package com.kathon.financialtool.domain.adapter.service

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.PersonDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.PersonNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.mapper.toExpenseGroupDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.repository.PersonRepository
import com.kathon.financialtool.domain.port.service.ExpenseGroupServiceI
import com.kathon.financialtool.domain.port.service.FinancialAccountServiceI
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ExpenseGroupService(
    private val personRepository: PersonRepository,
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val financialAccountService: FinancialAccountServiceI
) : ExpenseGroupServiceI {

    override fun createExpenseGroup(personId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto =
        personRepository.findById(personId)
            .map {
                val newExpenseGroup = ExpenseGroupEntity(name = expenseGroupDto.name, members = mutableSetOf(it))
                val createdExpenseGroup = expenseGroupRepository.save(newExpenseGroup).toExpenseGroupDto()
                financialAccountService.createFinancialAccount(createdExpenseGroup.id!!)
                return@map createdExpenseGroup
            }.orElseThrow { PersonNotFoundException(personId) }

    override fun updateExpenseGroup(
        personId: Long,
        expenseGroupId: Long,
        expenseGroupDto: ExpenseGroupDto
    ): ExpenseGroupDto =
        expenseGroupRepository.findById(expenseGroupId)
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }
            .let {
                if (it.createdBy?.id != personId) throw ResourceUnauthorizedException()
                it.name = expenseGroupDto.name
                validateExpenseGroupMembersAndUpdateIfNecessary(expenseGroupDto.members, it)
                return@let expenseGroupRepository.save(it)
            }.toExpenseGroupDto()

    override fun getUserExpenseGroups(personId: Long, pageSize: Int, pageNumber: Int): Page<ExpenseGroupDto> =
        expenseGroupRepository
            .findExpenseGroupEntitiesByMembersContaining(personId, PageRequest.of(pageNumber, pageSize))
            .map { it.toExpenseGroupDto() }


    override fun getUserExpenseGroupById(personId: Long, expenseGroupId: Long): ExpenseGroupDto =
        expenseGroupRepository.findById(expenseGroupId)
            .map { expenseGroupEntity ->
                val membersIdList = expenseGroupEntity.members.map { it.id }
                if (!membersIdList.contains(personId)) throw ResourceUnauthorizedException()
                val expenseGroupFinAccountList = financialAccountService
                    .getFinancialAccountsByExpenseGroupAndFilter(
                        expenseGroupId = expenseGroupId
                    ).content
                return@map expenseGroupEntity.toExpenseGroupDto(expenseGroupFinAccountList)
            }
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }

    override fun deleteUserExpenseGroup(personId: Long, expenseGroupId: Long) {
        expenseGroupRepository.findById(expenseGroupId)
            .map {
                if (it.createdBy?.id != personId) throw ResourceUnauthorizedException()
                it.isActive = false
                expenseGroupRepository.save(it)
            }
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }
    }

    private fun validateExpenseGroupMembersAndUpdateIfNecessary(
        membersDtoList: MutableList<PersonDto>?, expenseGroupEntity: ExpenseGroupEntity
    ) {
        val currentMembersIdList = expenseGroupEntity.members.map { it.id }
        val inputMembersIdList = membersDtoList?.map { it.id }.orEmpty()

        if (currentMembersIdList.containsAll(inputMembersIdList)
            && inputMembersIdList.containsAll(currentMembersIdList)
        ) return

        val updatedMemberSet = mutableSetOf(expenseGroupEntity.createdBy!!)
        personRepository.findAllById(inputMembersIdList)
            .map { updatedMemberSet.add(it) }
        expenseGroupEntity.members = updatedMemberSet
    }
}
