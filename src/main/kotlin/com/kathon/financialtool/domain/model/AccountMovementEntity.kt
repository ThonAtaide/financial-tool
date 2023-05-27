package com.kathon.financialtool.domain.model

import com.kathon.financialtool.domain.enums.AccountMovementDirectionEnum
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "ACCOUNT_MOVEMENT")
data class AccountMovementEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "DESCRIPTION")
    val description: String,

    @Column(name = "AMOUNT", nullable = false)
    val amount: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "ACCOUNT", nullable = false)
    var account: FinancialAccountEntity,

    @Column(name = "REAL_DATE")
    val realDate: Instant,

    @Enumerated(EnumType.STRING)
    @Column(name = "MOVEMENT_DIRECTION")
    val movementDirection: AccountMovementDirectionEnum,

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