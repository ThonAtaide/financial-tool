package com.kathon.financialtool.domain.port.out.repository

import com.kathon.financialtool.domain.model.ExpenseGroupEntity_
import com.kathon.financialtool.domain.model.FinancialAccountEntity
import com.kathon.financialtool.domain.model.FinancialAccountEntity_
import com.kathon.financialtool.domain.model.PersonEntity_
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface FinancialAccountRepository : JpaRepository<FinancialAccountEntity, Long>,
    JpaSpecificationExecutor<FinancialAccountEntity> {

    companion object {

        fun expenseGroupEquals(expenseGroupId: Long): Specification<FinancialAccountEntity> {
            return Specification<FinancialAccountEntity> { root, _, criteriaBuilder ->
                criteriaBuilder.equal(
                    root.get(FinancialAccountEntity_.expenseGroupEntity)
                        .get(ExpenseGroupEntity_.id), expenseGroupId
                )
            }
        }

        fun createdByEquals(createdBy: Long?): Specification<FinancialAccountEntity> {
            return Specification<FinancialAccountEntity> { root, _, criteriaBuilder ->
                Optional.ofNullable(createdBy)
                    .map {
                        criteriaBuilder.equal(
                            root.get(FinancialAccountEntity_.createdBy)
                                .get(PersonEntity_.id), createdBy
                        )
                    }
                    .orElse(criteriaBuilder.conjunction())
            }
        }
    }

    @Query(
        "SELECT FA FROM FinancialAccountEntity FA " +
                "INNER JOIN ExpenseGroupEntity EG ON EG.id=FA.expenseGroupEntity.id " +
                "WHERE EG.id = :expenseGroupId AND FA.isActive = true"
    )
    fun findFinancialAccountEntitiesByExpenseGroupEntity(
        expenseGroupId: Long,
        pageable: Pageable
    ): Page<FinancialAccountEntity>

    @Query(
        "SELECT FA FROM FinancialAccountEntity FA " +
                "INNER JOIN ExpenseGroupEntity EG ON EG.id=FA.expenseGroupEntity.id " +
                "WHERE EG.id = :expenseGroupId AND FA.createdBy.id = :personId AND FA.isActive = true"
    )
    fun findFinancialAccountEntitiesByExpenseGroupEntityAndCreatedByPerson(
        expenseGroupId: Long,
        personId: Long,
        pageable: Pageable
    ): Page<FinancialAccountEntity>
}