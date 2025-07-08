package pt.isel.markdown2slides.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class ChangeJsonSerializer : JsonSerializer<ChangeJSON>() {
    override fun serialize(
        value: ChangeJSON,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        when (value) {
            is Retain -> gen.writeNumber(value.length)

            is Delete -> {
                gen.writeStartArray()
                gen.writeNumber(value.length)
                gen.writeEndArray()
            }

            is Replace -> {
                gen.writeStartArray()
                gen.writeNumber(value.length)
                for (str in value.insert) {
                    gen.writeString(str)
                }
                gen.writeEndArray()
            }
        }
    }
}