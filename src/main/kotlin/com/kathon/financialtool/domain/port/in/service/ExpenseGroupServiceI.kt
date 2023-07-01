package com.kathon.financialtool.domain.port.`in`.service

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface ExpenseGroupServiceI {

    fun createExpenseGroup(personId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto

    fun updateExpenseGroup(personId: Long, expenseGroupId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto

    fun findAllExpenseGroupsByPerson(
        personId: Long,
        pageable: Pageable = PageRequest.of(0, 50, Sort.Direction.ASC, "name")
    ): Page<ExpenseGroupDto>

    fun findUserExpenseGroupById(personId: Long, expenseGroupId: Long): ExpenseGroupDto

    fun deleteUserExpenseGroup(personId: Long, expenseGroupId: Long)
}
