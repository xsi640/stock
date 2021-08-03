package com.github.xsi640.entity

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.*

@Entity
@Table
class Concept(
    @Id
    @SequenceGenerator(name = "concept_id_seq", sequenceName = "concept_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "concept_id_seq")
    @Column(name = "id")
    var id: Long = 0,
    var name: String = "",
    var code: String = "",
    @Convert(converter = FaucetConverter::class)
    var faucet: List<Faucet> = emptyList(),
    @Column(columnDefinition = "TEXT")
    var summary: String = ""
)

class Faucet(
    var code: String,
    var name: String
)

@Converter(autoApply = true)
class FaucetConverter : AttributeConverter<List<Faucet>, String> {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    override fun convertToDatabaseColumn(attribute: List<Faucet>): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String): List<Faucet> {
        if (dbData.isEmpty()) {
            return emptyList()
        }
        return objectMapper.readValue(dbData, object : TypeReference<List<Faucet>>() {})
    }
}

interface ConceptRepository : JpaRepository<Concept, Long> {
    fun findByCode(code: String): Optional<Concept>
}