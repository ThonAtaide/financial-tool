package com.kathon.financialtool.adapter.mapper

import com.kathon.financialtool.adapter.vo.FinancialAccountVo
import com.kathon.financialtool.domain.dto.FinancialAccountDto

fun FinancialAccountDto.toFinancialAccountVo() = FinancialAccountVo(
    id,
    name,
    createdBy = createdBy!!.toPersonVo(),
    createdAt,
    updatedAt
)

fun FinancialAccountVo.toFinancialAccountDto() = FinancialAccountDto(
    id,
    name
)