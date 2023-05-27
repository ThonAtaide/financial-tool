package com.kathon.financialtool.domain.model

import com.kathon.financialtool.domain.enums.AccountMovementDirectionEnum
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "FIN_ACCOUNT_TRANSFER")
data class FinAccountTransferEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @ManyToOne
    @JoinColumn(name = "INPUT_MOVEMENT", nullable = false)
    var accountMovementInput: AccountMovementEntity,

    @ManyToOne
    @JoinColumn(name = "OUTPUT_MOVEMENT", nullable = false)
    var accountMovementOutput: AccountMovementEntity,

    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    val createdBy: PersonEntity,

    @CreatedDate
    @Column(name = "CREATED_AT")
    val createdAt: Instant,
) {
}