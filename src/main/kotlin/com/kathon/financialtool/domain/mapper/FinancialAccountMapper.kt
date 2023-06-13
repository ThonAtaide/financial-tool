package com.kathon.financialtool.domain.mapper

import com.kathon.financialtool.domain.dto.FinancialAccountDto
import com.kathon.financialtool.domain.model.FinancialAccountEntity

fun FinancialAccountEntity.toFinancialAccountDto() = FinancialAccountDto(
    id,
    name,
    createdBy = createdBy?.toPersonDto(),
    createdAt,
    updatedAt
)