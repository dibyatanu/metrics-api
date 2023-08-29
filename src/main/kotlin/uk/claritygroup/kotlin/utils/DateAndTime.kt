package uk.claritygroup.kotlin.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Long?.convertToLocalDateAndTime(): LocalDateTime? = this ?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) }

fun LocalDateTime.convertToUnixTimeStamp()= this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()