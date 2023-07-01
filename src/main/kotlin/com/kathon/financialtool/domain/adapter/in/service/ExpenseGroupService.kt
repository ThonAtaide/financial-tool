package com.kathon.financialtool.domain.adapter.`in`.service

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.port.`in`.service.ExpenseGroupServiceI
import com.kathon.financialtool.domain.usecase.expenseGroup.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ExpenseGroupService(
    private val createExpenseGroupUseCase: CreateExpenseGroupUseCase,
    private val updateExpenseGroupUseCase: UpdateExpenseGroupUseCase,
    private val findExpenseGroupsByPersonUseCase: FindExpenseGroupsByPersonUseCase,
    private val findExpenseGroupByIdUseCase: FindExpenseGroupByIdUseCase,
    private val deleteExpenseGroupUseCase: DeleteExpenseGroupByPersonUseCase,
) : ExpenseGroupServiceI {

    override fun createExpenseGroup(personId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto =
        createExpenseGroupUseCase.createExpenseGroup(personId, expenseGroupDto)

    override fun updateExpenseGroup(
        personId: Long,
        expenseGroupId: Long,
        expenseGroupDto: ExpenseGroupDto
    ): ExpenseGroupDto =
        updateExpenseGroupUseCase.updateExpenseGroup(personId, expenseGroupId, expenseGroupDto)

    override fun findAllExpenseGroupsByPerson(personId: Long, pageable: Pageable): Page<ExpenseGroupDto> =
        findExpenseGroupsByPersonUseCase.findExpenseGroupsByUser(personId, pageable)

    override fun findUserExpenseGroupById(personId: Long, expenseGroupId: Long): ExpenseGroupDto =
        findExpenseGroupByIdUseCase.findUserExpenseGroupsById(personId, expenseGroupId)

    override fun deleteUserExpenseGroup(personId: Long, expenseGroupId: Long) {
        deleteExpenseGroupUseCase.deletePersonExpenseGroup(personId, expenseGroupId)
    }
}
