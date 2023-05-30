package com.kathon.financialtool.domain.exceptions

class ExpenseGroupNotFoundException(expenseGroupId: Long): RuntimeException("Expense group from id $expenseGroupId was not found.") {

}