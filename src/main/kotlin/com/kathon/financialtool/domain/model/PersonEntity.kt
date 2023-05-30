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
    var id: Long? = null,

    @Column(name = "PERSON_NAME")
    var name: String,

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "members" )
    var expenseGroupsPersonIsMember: MutableList<ExpenseGroupEntity>? = mutableListOf(),

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

        other as PersonEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "PersonEntity(id=$id, name='$name', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}