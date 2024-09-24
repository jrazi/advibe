package com.tapsell.recruitment.advibe
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@SpringBootApplication
open class AdvibeApplication

fun main(args: Array<String>) {
    runApplication<AdvibeApplication>(*args)
}
    