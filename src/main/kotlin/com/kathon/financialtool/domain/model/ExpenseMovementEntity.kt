package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "EXPENSE_MOVEMENT")
@EntityListeners(value = [AuditingEntityListener::class])
data class ExpenseMovementEntity(

    @Id
    var id: Long? = null,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID", referencedColumnName = "ID", nullable = false)
    val accountMovement: AccountMovementEntity? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EXPENSE_CATEGORY", nullable = false)
    var category: ExpenseCategoryEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYMENT_TYPE", nullable = false)
    var paymentType: PaymentTypeEntity,

    @CreatedDate
    @Column(name = "CREATED_AT")
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    var updatedAt: Instant? = null,
) {

    override fun toString(): String {
        return "ExpenseMovementEntity(id=$id, category=$category, paymentType=$paymentType, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}