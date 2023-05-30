package com.kathon.financialtool.domain.mapper

import com.kathon.financialtool.domain.dto.PersonDto
import com.kathon.financialtool.domain.model.PersonEntity

fun PersonEntity.toPersonDto() = PersonDto(
    id, name, createdAt, updatedAt
)
