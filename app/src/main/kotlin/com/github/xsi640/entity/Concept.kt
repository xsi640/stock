package com.github.xsi640.entity

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
@Table
class Concept(
    @Id
    @SequenceGenerator(name = "concept_id_seq", sequenceName = "concept_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "concept_id_seq")
    @Column(name = "id")
    var id: Long = 0,
    var name: String = ""
)

interface ConceptRepository : JpaRepository<Concept, Long>