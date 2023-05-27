package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "CREDIT_CARD_BILL")
data class CreditCardBillEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "REFERENCE_MONTH", nullable = false)
    val referenceMonth: Instant,

    @ManyToOne
    @JoinColumn(name = "CREDIT_CARD", nullable = false)
    val creditCard: CreditCardEntity,

    @ManyToOne
    @JoinColumn(name = "EXPENSE", nullable = false)
    var expense: ExpenseEntity,

    @Column(name = "PAID_IN_FULL", nullable = false)
    val paidInFull: Boolean,

    @CreatedDate
    @Column(name = "CREATED_AT")
    val createdAt: Instant,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    val updatedAt: Instant,
) {
}