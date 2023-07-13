package com.kathon.financialtool.adapter.controller

import com.kathon.financialtool.adapter.mapper.toFinancialAccountDto
import com.kathon.financialtool.adapter.mapper.toFinancialAccountVo
import com.kathon.financialtool.adapter.vo.FinancialAccountVo
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnUpdate
import com.kathon.financialtool.domain.adapter.`in`.service.FinancialAccountService
import com.kathon.financialtool.domain.exceptions.FinancialAccountNotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/financial-account")
class FinancialAccountController(
    private val financialAccountService: FinancialAccountService
) {

    @ResponseStatus(HttpStatus.OK)
    @Validated(ValidationGroupOnUpdate::class)
    @PutMapping("/{financial-account-id}")
    fun updateFinancialAccount(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable(name = "financial-account-id") financialAccountId: Long,
        @Valid @RequestBody financialAccountVo: FinancialAccountVo
    ): FinancialAccountVo =
        financialAccountVo.toFinancialAccountDto()
            .let { financialAccountService.updateFinancialAccount(personId, financialAccountId, it) }
            .toFinancialAccountVo()

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{financial-account-id}")
    fun findFinancialAccountById(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable(name = "financial-account-id") financialAccountId: Long
    ): FinancialAccountVo =
        financialAccountService.findFinancialAccountsById(personId, financialAccountId)
            .map { it.toFinancialAccountVo() }
            .orElseThrow { FinancialAccountNotFoundException(financialAccountId) }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{financial-account-id}")
    fun deleteFinancialAccountById(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable(name = "financial-account-id") financialAccountId: Long
    ) = financialAccountService.deleteFinancialAccount(personId, financialAccountId)
}