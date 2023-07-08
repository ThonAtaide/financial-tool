package com.kathon.financialtool.domain.usecase.expenseGroup

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.PersonDto
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.exceptions.ResourceUnauthorizedException
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import com.kathon.financialtool.domain.port.out.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.out.repository.PersonRepository
import org.springframework.stereotype.Service

@Service
class UpdateExpenseGroupUseCase(
    private val personRepository: PersonRepository,
    private val expenseGroupRepository: ExpenseGroupRepository
) {

    fun updateExpenseGroup(
        personId: Long,
        expenseGroupId: Long,
        expenseGroupDto: ExpenseGroupDto
    ): ExpenseGroupEntity =
        expenseGroupRepository.findById(expenseGroupId)
            .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }
            .let {
                if (it.createdBy?.id != personId) throw ResourceUnauthorizedException()
                it.name = expenseGroupDto.name
                validateExpenseGroupMembersAndUpdateIfNecessary(expenseGroupDto.members, it)
                return@let expenseGroupRepository.save(it)
            }

    private fun validateExpenseGroupMembersAndUpdateIfNecessary(
        membersDtoList: List<PersonDto>?, expenseGroupEntity: ExpenseGroupEntity
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