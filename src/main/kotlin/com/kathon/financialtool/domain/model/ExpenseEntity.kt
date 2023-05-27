package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "EXPENSE")
data class ExpenseEntity(

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "EXPENSE_NAME", nullable = false)
    val name: String,

    @Column(name = "AMOUNT", nullable = false)
    val amount: BigDecimal,

    @Column(name = "EXPENSE_DESCRIPTION", nullable = false)
    val description: String?,

    @Column(name = "EXPENSE_DATE", nullable = false)
    val date: Instant,

    @ManyToOne
    @JoinColumn(name = "EXPENSE_CATEGORY", nullable = false)
    val category: ExpenseCategoryEntity,

    @ManyToOne
    @JoinColumn(name = "FIN_ACCOUNT", nullable = false)
    val financialAccount: FinancialAccountEntity,

    @ManyToOne
    @JoinColumn(name = "PAYMENT_TYPE", nullable = false)
    val paymentType: PaymentTypeEntity,

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