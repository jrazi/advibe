package com.tapsell.platform.ad.contract.validation

import com.tapsell.platform.ad.contract.validation.TimestampConstants.MAX_TIMESTAMP
import com.tapsell.platform.ad.contract.validation.TimestampConstants.MIN_TIMESTAMP
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Min(value = MIN_TIMESTAMP, message = "Timestamp must be after January 1, 2000")
@Max(value = MAX_TIMESTAMP, message = "Timestamp must be before January 1, 2100")
annotation class ValidTimestamp(
    val message: String = "Invalid timestamp value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
