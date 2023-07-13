package com.kathon.financialtool.adapter.controller

import com.kathon.financialtool.adapter.mapper.toFinancialAccountDto
import com.kathon.financialtool.adapter.mapper.toFinancialAccountVo
import com.kathon.financialtool.adapter.vo.FinancialAccountVo
import com.kathon.financialtool.domain.adapter.`in`.service.FinancialAccountService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/expense-groups/{expense-group-id}/financial-account")
class ExpenseGroupFinancialAccountController(
    private val financialAccountService: FinancialAccountService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createFinancialAccount(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable(name = "expense-group-id") expenseGroupId: Long,
        @Valid @RequestBody financialAccountVo: FinancialAccountVo
    ): FinancialAccountVo =
        financialAccountVo.toFinancialAccountDto()
            .let { financialAccountService.createFinancialAccount(expenseGroupId, it) }
            .toFinancialAccountVo()


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun findAllFinancialAccountsByExpenseGroup(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @RequestHeader(
            name = "page-number",
            required = false,
            defaultValue = "0"
        ) pageNumber: Int,
        @RequestHeader(
            name = "page-size",
            required = false,
            defaultValue = "10"
        ) pageSize: Int,
        @RequestHeader(
            name = "order",
            required = false,
            defaultValue = "ASC"
        ) order: Sort.Direction,
        @RequestHeader(
            name = "orderBy",
            required = false,
            defaultValue = "CREATED_AT"
        ) vararg sortFields: FinancialAccountVo.SortFields,
        @PathVariable(name = "expense-group-id") expenseGroupId: Long,
    ): Page<FinancialAccountVo> {
        val sortFieldList = sortFields
            .ifEmpty { arrayOf(FinancialAccountVo.SortFields.CREATED_AT) }
            .map { it.field }
            .toTypedArray()

        return financialAccountService
            .searchExpenseGroupFinancialAccounts(
                personId,
                expenseGroupId = expenseGroupId,
                pageable = PageRequest.of(pageNumber, pageSize, order, *sortFieldList)
            )
            .map { it.toFinancialAccountVo() }
    }
}