package com.kathon.financialtool.adapter.controller

import com.kathon.financialtool.domain.port.repository.ExpenseGroupRepository
import com.kathon.financialtool.domain.port.repository.PersonRepository
import com.kathon.financialtool.domain.port.service.ExpenseGroupServiceI
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class HomeController(
    val personRepository: PersonRepository,
    val expenseGroupRepository: ExpenseGroupRepository,
    val expenseGroupService: ExpenseGroupServiceI
) {


    @GetMapping("/home")
    fun home(model: Model): String {
        return "/home/index"
    }

}