package com.tapsell.platform.ad.adstream.interaction.props

import com.tapsell.platform.ad.adstream.interaction.props.AdInteractionModelingProperties
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

@SpringBootTest
class AdInteractionModelingPropertiesTest(
    @Autowired private val properties: AdInteractionModelingProperties
) {

    @Test
    fun `given existing properties in yaml file, they must be loaded in props class`() {
        assertNotNull(properties.clickThroughRate)
        assertNotNull(properties.meanClickTime)
        assertNotNull(properties.stdClickTime)
        assertNotNull(properties.clickTimeShape)
        assertNotNull(properties.clickTimeScale)
        assertNotNull(properties.impressionArrivalRatio)
        assertNotNull(properties.clickArrivalRatio)
        assertTrue(properties.impressionArrivalRatio > 0F)
        assertTrue(properties.meanClickTime > 500F) // Mean click time must be greater than 500ms
    }
}
