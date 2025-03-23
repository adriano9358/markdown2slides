package pt.isel.markdown2slides

import java.time.Instant
import java.util.*


enum class Visibility {
    PUBLIC, PRIVATE
}

data class SlideTheme(
    val font: String = "Arial",
    val backgroundColor: String = "#FFFFFF",
    val textColor: String = "#000000"
)

data class ProjectInfo(
    val id: UUID,
    val name: String,
    val description: String,
    val ownerId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val theme: SlideTheme,
    val visibility: Visibility,
)