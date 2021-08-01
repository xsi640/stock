package com.github.xsi640.entity

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.*

@Entity
@Table(indexes = [Index(name = "idx_price_code", columnList = "code")])
class Price(
    @Id
    @SequenceGenerator(name = "price_id_seq", sequenceName = "price_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_id_seq")
    @Column(name = "id")
    var id: Long = 0,
    var code: String = "",

    //现价
    var price: Float = 0f,

    //涨跌幅
    var increase: Float = 0f,

    //涨跌
    var rise: Float = 0f,

    //涨速
    var riseSpeed: Float = 0f,

    //换手
    var turnover: Float = 0f,

    //量比
    var volume: Float = 0f,

    //振幅
    var amplitude: Float = 0f,

    //成交额
    var trading: Float = 0f,

    //流通股
    var tradable: Float = 0f,

    //流通市值
    var market: Float = 0f,

    //市盈率
    var pe: Float = 0f,

    //时间
    @Temporal(TemporalType.TIMESTAMP)
    var time: Date = Date(),
)

interface PriceRepository : JpaRepository<Price, Long>