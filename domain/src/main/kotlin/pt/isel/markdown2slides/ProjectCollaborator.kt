package pt.isel.markdown2slides

import java.util.*

data class ProjectCollaborator(
    val project_id: UUID,
    val user: UserInfo,
    val role: ProjectRole
)
