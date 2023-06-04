package com.kathon.financialtool.domain.exceptions

class FinancialAccountNotFoundException(personId: Long): RuntimeException("Financial account from id $personId was not found.") {

}