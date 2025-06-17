package pt.isel.markdown2slides

import java.time.Instant
import java.util.*

data class ProjectInvitationExtended(
    val id: UUID,
    val project: ProjectBasicInfo,
    val email: String,
    val role: ProjectRole,
    val invited_by: UUID?,
    val status: InvitationStatus,
    val invited_at: Instant
)

data class ProjectBasicInfo(
    val id: UUID,
    val name: String,
    val description: String?,
    val updatedAt: Instant
)