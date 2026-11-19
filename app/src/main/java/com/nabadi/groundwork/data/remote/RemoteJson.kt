package com.nabadi.groundwork.data.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * JSON settings for the sync backend's wire format.
 *
 * Unknown keys are ignored so either side can add fields without breaking the
 * other. Nulls are written explicitly (`"site_id": null`) so "unassigned" is
 * sent rather than left out.
 */
internal val RemoteJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
    encodeDefaults = true
}

/** Timestamps travel as ISO-8601 UTC strings, e.g. `2025-01-01T10:15:00Z`. */
internal object Iso8601InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Iso8601Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val text = decoder.decodeString()
        return try {
            Instant.parse(text)
        } catch (e: DateTimeParseException) {
            throw SerializationException("Invalid ISO-8601 timestamp: $text", e)
        }
    }
}
