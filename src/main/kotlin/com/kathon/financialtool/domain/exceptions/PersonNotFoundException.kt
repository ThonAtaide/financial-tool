package com.kathon.financialtool.domain.exceptions

class PersonNotFoundException(personId: Long): RuntimeException("Person from id $personId was not found.") {

}