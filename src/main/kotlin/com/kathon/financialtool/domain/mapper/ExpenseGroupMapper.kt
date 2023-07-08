package com.kathon.financialtool.domain.mapper

import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.model.ExpenseGroupEntity
import java.util.*

fun ExpenseGroupEntity.toExpenseGroupDto(

) = ExpenseGroupDto(
    id,
    name,
    createdBy = createdBy?.toPersonDto(),
    members = members.map { it.toPersonDto() }.toMutableList(),
    accounts = Optional.ofNullable(finAccountList)
        .map { accountList -> accountList.map { it.toFinancialAccountDto() } }
        .orElse(emptyList()),
    createdAt,
    updatedAt
)