package pt.isel.markdown2slides.utils

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class CollabUpdate(
    val clientID: String,
    @JsonDeserialize(contentUsing = ChangeJsonDeserializer::class)
    @JsonSerialize(contentUsing = ChangeJsonSerializer::class)
    val changes: List<ChangeJSON>,
    val cursor: CursorInfo
)

fun CollabUpdate.getLength(): Int {
    var length = 0
    this.changes.forEach {
        length += when (it) {
            is Retain -> it.length
            is Delete -> it.length
            is Replace -> it.length
        }
    }
    return length
}