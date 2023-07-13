package com.kathon.financialtool.adapter.controller

import com.kathon.financialtool.adapter.mapper.toExpenseGroupDto
import com.kathon.financialtool.adapter.mapper.toExpenseGroupVo
import com.kathon.financialtool.adapter.mapper.toFinancialAccountDto
import com.kathon.financialtool.adapter.mapper.toFinancialAccountVo
import com.kathon.financialtool.adapter.vo.ExpenseGroupVo
import com.kathon.financialtool.adapter.vo.FinancialAccountVo
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnCreate
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnUpdate
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.port.`in`.service.ExpenseGroupServiceI
import com.kathon.financialtool.domain.port.`in`.service.FinancialAccountServiceI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/expense-group")
@Tag(
    name = "Expense group",
    description = "Resource to manage users expense groups and nested resources"
)
class ExpenseGroupController(
    private val expenseGroupServiceI: ExpenseGroupServiceI,
    private val financialAccountService: FinancialAccountServiceI
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroupOnUpdate::class)
    @Operation(
        summary = "Create user expense group.",
        description = "Create user expense group with a default financial account."
    )
    fun createExpenseGroup(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @Valid @RequestBody expenseGroupVo: ExpenseGroupVo
    ): ExpenseGroupVo =
        expenseGroupVo
            .toExpenseGroupDto()
            .let { expenseGroupServiceI.createExpenseGroup(personId, it) }
            .toExpenseGroupVo()

    @Operation(
        summary = "Update user expense group.",
        description = "Update user expense group infos as members."
    )
    @Validated(ValidationGroupOnUpdate::class)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{expense-group-id}")
    fun updateExpenseGroup(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable("expense-group-id") expenseGroupId: Long,
        @Valid @RequestBody expenseGroupVo: ExpenseGroupVo
    ): ExpenseGroupVo = expenseGroupVo.toExpenseGroupDto()
        .let { expenseGroupServiceI.updateExpenseGroup(personId, expenseGroupId, it) }
        .toExpenseGroupVo()

    @Operation(
        summary = "Find user expense group.",
        description = "Find user expense group by id."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{expense-group-id}")
    fun findExpenseGroupById(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable("expense-group-id") expenseGroupId: Long
    ): ExpenseGroupVo = expenseGroupServiceI.findUserExpenseGroupById(personId, expenseGroupId)
        .map { it.toExpenseGroupVo() }
        .orElseThrow { ExpenseGroupNotFoundException(expenseGroupId) }

    @Operation(
        summary = "Retrieve user expense groups.",
        description = "Retrieve user expense groups with pagination."
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAllUserExpenseGroups(
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
        ) vararg sortFields: ExpenseGroupVo.SortFields,
    ): Page<ExpenseGroupVo> {

        val sortFieldList = sortFields
            .ifEmpty { arrayOf(ExpenseGroupVo.SortFields.CREATED_AT) }
            .map { it.field }.toTypedArray()
        return expenseGroupServiceI
            .findAllExpenseGroupsByPerson(personId, PageRequest.of(pageNumber, pageSize, order, *sortFieldList))
            .map { it.toExpenseGroupVo() }
    }

    @Operation(
        summary = "Delete user expense groups.",
        description = "Delete user expense groups through user id."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{expense-group-id}")
    fun deleteExpenseGroup(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable("expense-group-id") expenseGroupId: Long
    ) =
        expenseGroupServiceI.deleteUserExpenseGroup(personId, expenseGroupId)

    @Operation(
        summary = "Create financial account.",
        description = "Create user financial account into expense group."
    )
    @Validated(ValidationGroupOnCreate::class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{expense-group-id}/financial-account")
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

    @Operation(
        summary = "Retrieve financial accounts from expense-group.",
        description = "Retrieve financial accounts from expense-group through filters and pagination."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{expense-group-id}/financial-account")
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
        @RequestParam(name = "created-by") createdBy: Long? = null
    ): Page<FinancialAccountVo> {
        val sortFieldList = sortFields
            .ifEmpty { arrayOf(FinancialAccountVo.SortFields.CREATED_AT) }
            .map { it.field }
            .toTypedArray()

        return financialAccountService
            .searchExpenseGroupFinancialAccounts(
                personId,
                expenseGroupId = expenseGroupId,
                createdBy = createdBy,
                pageable = PageRequest.of(pageNumber, pageSize, order, *sortFieldList)
            )
            .map { it.toFinancialAccountVo() }
    }

}