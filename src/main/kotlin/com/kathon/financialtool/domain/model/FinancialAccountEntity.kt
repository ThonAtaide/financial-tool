package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "FINANCIAL_ACCOUNT")
data class FinancialAccountEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "ACCOUNT_NAME")
    val name: String,

    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    var createdBy: PersonEntity,

    @ManyToOne
    @JoinColumn(name = "EXPENSE_GROUP", nullable = false)
    var expenseGroupEntity: ExpenseGroupEntity,

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false)
    val createdAt: Instant,

    @LastModifiedDate
    @Column(name = "UPDATED_AT", nullable = false)
    val updatedAt: Instant,
) {
}