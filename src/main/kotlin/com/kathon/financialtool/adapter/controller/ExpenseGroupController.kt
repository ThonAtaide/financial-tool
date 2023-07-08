package com.kathon.financialtool.adapter.controller

import com.kathon.financialtool.adapter.mapper.toExpenseGroupDto
import com.kathon.financialtool.adapter.mapper.toExpenseGroupVo
import com.kathon.financialtool.adapter.vo.ExpenseGroupVo
import com.kathon.financialtool.adapter.vo.validation.ValidationGroupOnUpdate
import com.kathon.financialtool.domain.exceptions.ExpenseGroupNotFoundException
import com.kathon.financialtool.domain.port.`in`.service.ExpenseGroupServiceI
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/expense-groups")
class ExpenseGroupController(
    private val expenseGroupServiceI: ExpenseGroupServiceI
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroupOnUpdate::class)
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

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{expense-group-id}")
    fun updateExpenseGroup(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable("expense-group-id") expenseGroupId: Long,
        @Valid @RequestBody expenseGroupVo: ExpenseGroupVo
    ) : ExpenseGroupVo = expenseGroupVo.toExpenseGroupDto()
        .let { expenseGroupServiceI.updateExpenseGroup(personId, expenseGroupId, it) }
        .toExpenseGroupVo()

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
        ) pageNumber: Int = 1,
        @RequestHeader(
            name = "page-size",
            required = false,
            defaultValue = "10"
        ) pageSize: Int = 1,
    ): Page<ExpenseGroupVo> =
        expenseGroupServiceI
            .findAllExpenseGroupsByPerson(personId, PageRequest.of(pageNumber, pageSize, ASC, "createdAt"))
            .map { it.toExpenseGroupVo() }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{expense-group-id}")
    fun deleteExpenseGroup(
        @RequestHeader(
            name = "person-id",
        ) personId: Long,
        @PathVariable("expense-group-id") expenseGroupId: Long
    ) =
        expenseGroupServiceI.deleteUserExpenseGroup(personId, expenseGroupId)

}