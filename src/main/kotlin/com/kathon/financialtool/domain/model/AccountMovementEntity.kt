package com.kathon.financialtool.domain.model

import com.kathon.financialtool.domain.enums.AccountMovementType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "ACCOUNT_MOVEMENT")
@EntityListeners(value = [AuditingEntityListener::class])
data class AccountMovementEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "MOVEMENT_NAME", nullable = false)
    var name: String,

    @Column(name = "MOVEMENT_DESCRIPTION")
    var description: String? = null,

    @Column(name = "AMOUNT", nullable = false)
    var amount: BigDecimal,

    @Column(name = "MOVEMENT_DATE")
    var movementDate: Instant,

    @ManyToOne
    @JoinColumn(name = "FIN_ACCOUNT", nullable = false)
    var account: FinancialAccountEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "MOVEMENT_TYPE")
    val movementType: AccountMovementType,

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "accountMovement", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var expenseMovementEntity: ExpenseMovementEntity? = null,

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    var createdBy: PersonEntity? = null,

    @CreatedDate
    @Column(name = "CREATED_AT")
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    var updatedAt: Instant? = null,
) {


    override fun toString(): String {
        return "AccountMovementEntity(id=$id, name='$name', description=$description, amount=$amount, movementDate=$movementDate, account=$account, movementType=$movementType, createdBy=$createdBy, createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountMovementEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}