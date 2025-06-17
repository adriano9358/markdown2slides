package pt.isel.markdown2slides

import java.time.Instant
import java.util.*

data class ProjectInvitation(
    val id: UUID,
    val project_id: UUID,
    val email: String,
    val role: ProjectRole,
    val invited_by: UUID?,
    val status: InvitationStatus,
    val invited_at: Instant
)