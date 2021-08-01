package com.github.xsi640.entity

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.*

@Entity
@Table(
    indexes = [Index(name = "idx_stock_code", columnList = "code"),
        Index(name = "idx_stock_name", columnList = "name")]
)
class Stock(
    @Id
    @SequenceGenerator(name = "stock_id_seq", sequenceName = "stock_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_id_seq")
    @Column(name = "id")
    var id: Long = 0,
    var code: String = "",
    var name: String = ""
)

interface StockRepository : JpaRepository<Stock, Long> {
    fun findByCode(code: String): Optional<Stock>
}