package com.github.xsi640

import com.github.xsi640.entity.*
import com.querydsl.jpa.impl.JPAQueryFactory
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import java.util.*


@Component
class StockScheduling {

    val StoreListUrl = "https://q.10jqka.com.cn/index/index/board/all/field/zdf/order/desc/page/{pageIndex}/ajax/1"
    val StoreDetailUrl = "https://basic.10jqka.com.cn/{code}/concept.html"

    @Autowired
    private lateinit var stockRepository: StockRepository

    @Autowired
    private lateinit var conceptRepository: ConceptRepository

    @Autowired
    private lateinit var priceRepository: PriceRepository

    @Autowired
    private lateinit var stockConceptRefRepository: StockConceptRefRepository

    @Autowired
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @Value("\${stock.selenium.driver}")
    private var seleniumDriverUrl: String = ""

    @Scheduled(cron = "0 0 22 * * ?")
    fun run() {
        if (isRestDay()) {
            return
        }
        buildStoreList()
        buildConcepts()
        log.info("finish")
    }

    private fun buildConcepts() {
        stockRepository.findAll().forEach { stock ->
            val code = stock.code
            val url = StoreDetailUrl.replace("{code}", code)
            parseConcept(url, stock.code)
        }
    }

    private fun parseConcept(url: String, code: String) {
        val concepts = mutableListOf<Concept>()
        val doc = query(url)
        val items = doc.select("#concept .gnContent tbody tr")
        val map = mutableMapOf<String, String>()
        items.forEach { element ->
            val nameElement = element.select("td.gnName")
            val name = nameElement.text()
            if (name.isEmpty())
                return@forEach
            val conceptCode = nameElement.attr("clid")
            val faucets = mutableListOf<Faucet>()
            val faucetElements = element.select("a.gnltg")
            faucetElements.forEach { link ->
                val n = link.text()
                val c = link.attr("code")
                faucets.add(Faucet(c, n))
            }
            map[conceptCode] = element.select("div.tdContent").text()

            concepts.add(
                Concept(
                    name = name,
                    code = conceptCode,
                    faucet = faucets,
                )
            )
        }
        val items0 = doc.select("#other .gnContent tbody tr")
        items0.forEach { element ->
            val name = element.select("td.gnStockList").text()
            if (name.isEmpty())
                return@forEach
            val conceptCode = element.select("td.gnStockList").attr("cid")
            map[conceptCode] = element.select("div.tdContent").text()

            concepts.add(
                Concept(
                    code = conceptCode,
                    name = name,
                )
            )
        }

        saveConcepts(concepts)
        buildStockConceptRefs(code, map)
    }

    @Transactional
    fun saveConcepts(concepts: List<Concept>) {
        concepts.forEach { c ->
            val exists =
                jpaQueryFactory.from(QConcept.concept).where(QConcept.concept.code.eq(c.code)).fetchOne() as Concept?
            if (exists != null) {
                exists.name = c.name
                exists.faucet = c.faucet
                conceptRepository.save(exists)
            } else {
                conceptRepository.save(c)
            }
        }
    }

    @Transactional
    fun buildStockConceptRefs(code: String, map: Map<String, String>) {
        val lists = jpaQueryFactory.from(QStockConceptRef.stockConceptRef)
            .where(QStockConceptRef.stockConceptRef.stockCode.eq(code)).fetch()
                as List<StockConceptRef>
        if (lists.isNotEmpty()) {
            stockConceptRefRepository.deleteAll(lists)
        }
        val list = map.map { (k, v) ->
            StockConceptRef(
                stockCode = code,
                conceptCode = k,
                summary = v
            )
        }
        if (list.isNotEmpty()) {
            stockConceptRefRepository.saveAll(list)
        }
    }

    private fun buildStoreList() {
        var url = StoreListUrl.replace("{pageIndex}", "1")
        val doc = query(url)
        val pageSize = doc.selectFirst("#m-page .page_info")!!.text().split("/")[1].toInt()
        parseStore(doc)
        for (index in 2 until pageSize + 1) {
            url = StoreListUrl.replace("{pageIndex}", index.toString())
            parseStore(query(url))
        }
    }

    @Transactional
    fun parseStore(doc: Document) {
        val items = doc.select(".m-table tbody tr")
        val stocks = items.map { element ->
            val tds = element.select("td")
            val code = tds[1].text()
            val name = tds[2].text()
            val exists = jpaQueryFactory.from(QStock.stock).where(QStock.stock.code.eq(code)).fetchOne() as Stock?
            if (exists != null) {
                exists.name = name
                exists
            } else {
                Stock(
                    code = code,
                    name = name
                )
            }
        }
        stockRepository.saveAll(stocks)

        val prices = items.map { element ->
            val tds = element.select("td")
            val price = Price(
                code = tds[1].text(),
                price = tds[3].text().toPrice(),
                increase = tds[4].text().toPrice(),
                rise = tds[5].text().toPrice(),
                riseSpeed = tds[6].text().toPrice(),
                turnover = tds[7].text().toPrice(),
                volume = tds[8].text().toPrice(),
                amplitude = tds[9].text().toPrice(),
                trading = tds[10].text().toPrice(),
                tradable = tds[11].text().toPrice(),
                market = tds[12].text().toPrice(),
                pe = tds[13].text().toPrice(),
                time = Date()
            )
            price
        }
        priceRepository.saveAll(prices)
    }

    fun query(url: String): Document {
        val result = mutableListOf<Document>()
        while (result.isEmpty()) {
            try {
                val s = request(url)
                return Jsoup.parse(s)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        return result[0]
    }

    fun String.toPrice(): Float {
        return try {
            if (this.contains("亿")) {
                this.replace("亿", "").toFloat() * 10000
            } else if (this.contains("万")) {
                this.replace("万", "").toFloat()
            } else if (this == "--") {
                0f
            } else if (this == "亏损") {
                -1f
            } else {
                this.toFloat()
            }
        } catch (ignore: NumberFormatException) {
            0f
        }
    }

    fun isWeekend(): Boolean {
        val c = Calendar.getInstance()
        c.time = Date()
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) {
            return true
        }
        return false
    }

    fun request(url: String): String {
        if (seleniumDriverUrl.isEmpty()) {
            throw IllegalArgumentException("can't found selenium driver url.")
        }
        val options = ChromeOptions()
        options.addArguments("--incognito")
        options.addArguments("--disable-blink-features=AutomationControlled")
        options.addArguments("--disable-extensions")
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
        options.addArguments("blink-settings=imagesEnabled=false")
        options.addArguments("--headless")
        val driver = RemoteWebDriver(URL(seleniumDriverUrl), options)
        driver.get(url)
        val result = driver.pageSource
        driver.quit()
        return result
    }

    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
        private val OS_NAME = System.getProperty("os.name").lowercase()

        fun md5(s: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(s.toByteArray(Charsets.UTF_8)))
                .toString(16).padStart(32, '0')
        }
    }

    private fun isRestDay(): Boolean {
        val f = File("latest")
        val url = StoreListUrl.replace("{pageIndex}", "1")
        val req = request(url)
        val md5 = md5(req)
        val flag = if (f.exists()) {
            val text = FileUtils.readFileToString(f, Charsets.UTF_8)
            f.delete()
            md5 == text
        } else {
            return true
        }
        FileUtils.writeStringToFile(f, md5, Charsets.UTF_8)
        return flag
    }

    fun getOSName(): EPlatform {
        if (OS_NAME.contains("windows")) {
            return EPlatform.WINDOWS
        } else if (OS_NAME.contains("mac")) {
            return EPlatform.MAC
        } else if (OS_NAME.contains("linux")) {
            return EPlatform.LINUX
        }
        return EPlatform.OTHERS
    }

    enum class EPlatform {
        WINDOWS, MAC, LINUX, OTHERS
    }
}