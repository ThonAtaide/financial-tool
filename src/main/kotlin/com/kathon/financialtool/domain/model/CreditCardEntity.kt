package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "CREDIT_CARD")
data class CreditCardEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "CARD_NAME", nullable = false)
    val name: String,

    @Column(name = "STATEMENT_CLOSING_DAY", nullable = false)
    val statementClosingDay: Int,

    @ManyToOne
    @JoinColumn(name = "DEFAULT_FIN_ACCOUNT", nullable = false)
    var account: FinancialAccountEntity,

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