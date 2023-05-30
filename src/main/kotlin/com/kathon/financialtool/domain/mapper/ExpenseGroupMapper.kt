package com.kathon.financialtool.domain.mapper

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity

fun ExpenseGroupEntity.toExpenseGroupDto() = ExpenseGroupDto(
    id,
    name,
    createdBy = createdBy?.toPersonDto(),
    members = members.map { it.toPersonDto() }.toMutableList(),
    createdAt,
    updatedAt
)