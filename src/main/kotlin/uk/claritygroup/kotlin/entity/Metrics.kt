package uk.claritygroup.kotlin.entity

import jakarta.persistence.*
import uk.claritygroup.kotlin.utils.convertToUnixTimeStamp
import java.time.LocalDateTime
import uk.claritygroup.kotlin.model.response.Metrics as MetricsResponse
@Entity
@Table(name = "metrics", uniqueConstraints = [UniqueConstraint(columnNames = ["system", "name", "date"])])
data class Metrics(
     @Id
     @GeneratedValue(strategy= GenerationType.IDENTITY)
     @Column(name= "id", nullable = false)
     val id: Long =-1,
     @Column(name= "system", nullable = false)
     val system: String ,
     @Column(name= "name", nullable = false)
     val name: String,
     @Column(name= "date", nullable = false)
     val date: LocalDateTime,
     @Column(name= "value", nullable = false)
     val value: Int
)



