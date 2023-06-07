package com.kathon.financialtool.domain.mapper

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity

fun ExpenseGroupEntity.toExpenseGroupDto(
    finAccountList: MutableList<FinancialAccountDto>? = null
) = ExpenseGroupDto(
    id,
    name,
    createdBy = createdBy?.toPersonDto(),
    members = members.map { it.toPersonDto() }.toMutableList(),
    accounts = finAccountList,
    createdAt,
    updatedAt
)