package pt.isel.markdown2slides.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class ChangeJsonDeserializer : JsonDeserializer<ChangeJSON>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChangeJSON {
        return when {
            p.currentToken.isNumeric -> Retain(p.intValue)
            p.currentToken == JsonToken.START_ARRAY -> {
                val array = mutableListOf<Any>()
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    array.add(p.readValueAs(Any::class.java))
                }

                val first = array.firstOrNull()
                if (first is Int && array.size == 1) {
                    Delete(first)
                } else if (first is Int && array.size > 1) {
                    Replace(first, array.drop(1).map { it.toString() })
                } else {
                    throw IllegalArgumentException("Invalid change JSON array format")
                }
            }
            else -> throw IllegalArgumentException("Invalid change format")
        }
    }
}
