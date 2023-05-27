package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "PERSON")
data class PersonEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "PERSON_NAME")
    val name: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "PERSON_EXPENSE_GROUP",
        joinColumns = [JoinColumn(name = "PERSON")],
        inverseJoinColumns = [JoinColumn(name= "EXPENSE_GROUP")]
    )
    var expenseGroupsPersonIsMember: List<ExpenseGroupEntity>,

    @CreatedDate
    @Column(name = "CREATED_AT")
    val createdAt: Instant,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    val updatedAt: Instant,
)