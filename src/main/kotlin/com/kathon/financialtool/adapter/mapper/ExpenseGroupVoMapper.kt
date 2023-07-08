package com.kathon.financialtool.adapter.mapper

import com.kathon.financialtool.adapter.vo.ExpenseGroupVo
import com.kathon.financialtool.adapter.vo.PersonVo
import com.kathon.financialtool.domain.dto.ExpenseGroupDto
import com.kathon.financialtool.domain.dto.PersonDto
import java.util.*

fun ExpenseGroupVo.toExpenseGroupDto() = ExpenseGroupDto(
    id,
    name,
    members = Optional.ofNullable(members)
        .map { membersList -> membersList.map { it.toPersonDto() } }
        .orElse(emptyList())
)

fun PersonVo.toPersonDto() = PersonDto(
    id,
    name!!,
    createdAt,
    updatedAt
)

fun ExpenseGroupDto.toExpenseGroupVo() = ExpenseGroupVo(
    id,
    name,
    members = members!!.map { it.toPersonVo() },
    accounts = accounts!!.map { it.toFinancialAccountVo() },
    createdBy = createdBy!!.toPersonVo(),
    updatedAt = updatedAt,
    createdAt = createdAt
)

fun PersonDto.toPersonVo() = PersonVo(
    id,
    name
)
