package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "CREDIT_CARD_EXPENSE_INSTALLMENT")
data class CreditCardExpenseInstallmentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    var id: Long,

    @ManyToOne
    @JoinColumn(name = "CREDIT_CARD_EXPENSE", nullable = false)
    val creditCardExpenseOrigin: CreditCardExpenseEntity,

    @Column(name = "INSTALLMENT_DATE", nullable = false)
    val installmentDate: Instant,

    @Column(name = "INSTALLMENT_NUMBER", nullable = false)
    val installmentNumber: Int,

    @Column(name = "AMOUNT", nullable = false)
    val amount: BigDecimal,

    @CreatedDate
    @Column(name = "CREATED_AT")
    val createdAt: Instant,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    val updatedAt: Instant,
) {
}