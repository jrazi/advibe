package com.tapsell.platform.ad
import com.tapsell.platform.ad.adstream.ctr.AdInteractionModelingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AdInteractionModelingProperties::class)
@ConfigurationPropertiesScan
open class AdvibeApplication

fun main(args: Array<String>) {
    runApplication<AdvibeApplication>(*args)
}
    