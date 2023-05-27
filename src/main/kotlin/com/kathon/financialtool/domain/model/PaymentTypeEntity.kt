package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "PAYMENT_TYPE")
data class PaymentTypeEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "PAYMENT_NAME")
    val paymentName: String,

    @CreatedDate
    @Column(name = "CREATED_AT")
    val createdAt: Instant,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    val updatedAt: Instant,
) {
}