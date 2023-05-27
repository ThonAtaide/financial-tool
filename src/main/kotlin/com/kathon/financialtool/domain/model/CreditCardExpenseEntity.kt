package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "CREDIT_CARD_EXPENSE")
data class CreditCardExpenseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    var id: Long,

    @Column(name = "EXPENSE_DESCRIPTION")
    val description: String,

    @Column(name = "EXPENSE_DATE")
    val date: Instant,

    @ManyToOne
    @JoinColumn(name = "EXPENSE_CATEGORY", nullable = false)
    val category: ExpenseCategoryEntity,

    @ManyToOne
    @JoinColumn(name = "CREDIT_CARD", nullable = false)
    val creditCard: CreditCardEntity,

    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    val createdBy: PersonEntity,

    @CreatedDate
    @Column(name = "CREATED_AT")
    val createdAt: Instant,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    val updatedAt: Instant,
) {
}