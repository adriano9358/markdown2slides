package pt.isel.markdown2slides.model

import java.util.*

data class AddCollaboratorDTO(
    val userId: UUID,
    val role: String
)