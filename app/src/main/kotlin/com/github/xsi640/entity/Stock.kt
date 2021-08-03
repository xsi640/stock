package com.github.xsi640.entity

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.*

@Entity
@Table
class Stock(
    @Id
    var code: String = "",
    var name: String = ""
)

interface StockRepository : JpaRepository<Stock, Long>