package com.kathon.financialtool.domain.port.out.repository

import com.kathon.financialtool.domain.enums.AccountMovementType
import com.kathon.financialtool.domain.model.*
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface AccountMovementRepository : JpaRepository<AccountMovementEntity, Long>,
    JpaSpecificationExecutor<AccountMovementEntity> {
    companion object {

        fun expenseGroupEquals(expenseGroupId: Long): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                val accountMovementWithFinAccountJoin = root.join(AccountMovementEntity_.account)
                val finAccountWithExpenseGroupJoin =
                    accountMovementWithFinAccountJoin.join(FinancialAccountEntity_.expenseGroupEntity)

                return@Specification criteriaBuilder.equal(
                    finAccountWithExpenseGroupJoin.get(ExpenseGroupEntity_.id), expenseGroupId
                )
            }
        }

        fun accountMovementTypeIn(
            accountMovementTypeList: List<AccountMovementType>
        ): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                Optional.ofNullable(accountMovementTypeList)
                    .filter { it.isNotEmpty() }
                    .map {
                        root.get(AccountMovementEntity_.movementType).`in`(it)
                    }.orElse(criteriaBuilder.conjunction())
            }
        }

        fun financialAccountMovementIn(
            financialAccountIdList: List<Long>
        ): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                Optional.ofNullable(financialAccountIdList)
                    .filter { it.isNotEmpty() }
                    .map {
                        root.get(AccountMovementEntity_.account)
                            .get(FinancialAccountEntity_.id).`in`(it)
                    }.orElse(criteriaBuilder.conjunction())
            }
        }

        fun accountMovementNameStartWith(movementName: String?): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                Optional.ofNullable(movementName)
                    .filter { it.isNotBlank() }
                    .map { "$it%".uppercase() }
                    .map {
                        criteriaBuilder.like(
                            criteriaBuilder.upper(root.get(AccountMovementEntity_.name)), it
                        )
                    }.orElse(criteriaBuilder.conjunction())
            }
        }

        fun accountMovementRangeBetween(oldestDate: Instant, newest: Instant): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                criteriaBuilder.between(
                    root.get(AccountMovementEntity_.movementDate), oldestDate, newest
                )
            }
        }

        fun accountMovementCreatedByEquals(createdBy: Long?): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                Optional.ofNullable(createdBy)
                    .map {
                        criteriaBuilder.equal(
                            root.get(AccountMovementEntity_.createdBy), createdBy
                        )
                    }.orElse(criteriaBuilder.conjunction())

            }
        }

        fun expenseCategoryIn(expenseCategoryList: List<Long>): Specification<AccountMovementEntity> {
            return Specification<AccountMovementEntity> { root, _, criteriaBuilder ->
                Optional.ofNullable(expenseCategoryList)
                    .filter { it.isNotEmpty() }
                    .map {
                        val accountMovementEntityExpenseMovementEntityJoin =
                            root.join(AccountMovementEntity_.expenseMovementEntity)
                        val expenseMovementEntityExpenseCategoryEntityJoin =
                            accountMovementEntityExpenseMovementEntityJoin.join(ExpenseMovementEntity_.category)
                        return@map expenseMovementEntityExpenseCategoryEntityJoin
                            .get(ExpenseCategoryEntity_.id).`in`(expenseCategoryList)
                    }.orElse(criteriaBuilder.conjunction())
            }
        }
    }
}
