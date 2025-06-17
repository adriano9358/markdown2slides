package pt.isel.markdown2slides.model

import pt.isel.markdown2slides.ProjectRole

data class InviteUserDTO(
    val email: String,
    val role: ProjectRole
)