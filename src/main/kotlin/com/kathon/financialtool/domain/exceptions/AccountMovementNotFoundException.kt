package com.kathon.financialtool.domain.exceptions

class AccountMovementNotFoundException(expenseGroupId: Long): RuntimeException("Account movement from id $expenseGroupId was not found.") {

}