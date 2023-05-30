package com.kathon.financialtool.domain.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "EXPENSE_GROUP")
@EntityListeners(value = [AuditingEntityListener::class])
data class ExpenseGroupEntity(

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "GROUP_NAME")
    var name: String,

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    var createdBy: PersonEntity? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "PERSON_EXPENSE_GROUP",
        joinColumns = [JoinColumn(name = "EXPENSE_GROUP")],
        inverseJoinColumns = [JoinColumn(name = "PERSON")]
    )
    var members: MutableList<PersonEntity> = mutableListOf(),

    @CreatedDate
    @Column(name = "CREATED_AT")
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    var updatedAt: Instant? = null,
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseGroupEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "ExpenseGroupEntity(id=$id, name='$name', createdBy=$createdBy, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}