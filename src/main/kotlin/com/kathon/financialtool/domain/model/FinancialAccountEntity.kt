package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "FINANCIAL_ACCOUNT")
@EntityListeners(value = [AuditingEntityListener::class])
data class FinancialAccountEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "ACCOUNT_NAME")
    var name: String,

    @Column(name = "IS_ACTIVE")
    var isActive: Boolean = true,

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false, updatable = false)
    var createdBy: PersonEntity? = null,

    @ManyToOne
    @JoinColumn(name = "EXPENSE_GROUP", nullable = false, updatable = false)
    val expenseGroupEntity: ExpenseGroupEntity,

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false)
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(name = "UPDATED_AT", nullable = false)
    var updatedAt: Instant? = null,
) {
}