package pt.isel.markdown2slides

import java.time.Instant
import java.util.*


enum class Visibility {
    PUBLIC, PRIVATE
}


enum class SlideTheme{
    WHITE, BLACK, BEIGE, BLOOD, LEAGUE, MOON, NIGHT, SERIF, SIMPLE, SKY, SOLARIZED, DRACULA
}

data class ProjectInfo(
    val id: UUID,
    val name: String,
    val description: String,
    val ownerId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val theme: String,
    val visibility: Visibility,
)