package com.github.xsi640.entity

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
@Table(indexes = [Index(name = "idx_stock_concept_ref_stock_id", columnList = "stockId"),Index(name = "idx_stock_concept_ref_concept_id", columnList = "conceptId")])
class StockConceptRef(
    @Id
    @SequenceGenerator(name = "stock_concept_ref_id_seq", sequenceName = "stock_concept_ref_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_concept_ref_id_seq")
    @Column(name = "id")
    var id: Long = 0,
    var stockId: Long = 0,
    var conceptId: Long = 0,
    @Lob
    @Column(columnDefinition = "TEXT")
    var summary: String = ""
)

interface StockConceptRefRepository : JpaRepository<StockConceptRef, Long>
