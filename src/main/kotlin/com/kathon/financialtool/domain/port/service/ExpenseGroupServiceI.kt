package com.kathon.financialtool.domain.port.service

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import org.springframework.data.domain.Page

interface ExpenseGroupServiceI {

    fun createExpenseGroup(personId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto

    fun updateExpenseGroup(personId: Long, expenseGroupId: Long, expenseGroupDto: ExpenseGroupDto): ExpenseGroupDto

    fun getUserExpenseGroups(personId: Long, pageSize: Int, pageNumber: Int): Page<ExpenseGroupDto>

    fun getUserExpenseGroupById(personId: Long, expenseGroupId: Long): ExpenseGroupDto

    fun deleteUserExpenseGroup(personId: Long, expenseGroupId: Long)
}
