package pt.isel.markdown2slides

import java.time.Instant


enum class Visibility {
    PUBLIC, PRIVATE
}

data class SlideTheme(
    val font: String = "Arial",
    val backgroundColor: String = "#FFFFFF",
    val textColor: String = "#000000"
)

data class ProjectInfo(
    val id: Long,
    val name: String,
    val description: String,
    val ownerId: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val theme: SlideTheme,
    val visibility: Visibility,
)