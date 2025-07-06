package pt.isel.markdown2slides.data.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.*
import pt.isel.markdown2slides.data.RepositoryInvitations
import java.time.Instant
import java.util.*

@Named
class RepositoryInvitationsInMem(private val projectRepo: RepositoryProjectInfoInMem) : RepositoryInvitations {

    private val invitations = mutableMapOf<UUID, ProjectInvitation>()

    override fun createInvitation(projectId: UUID, email: String, role: ProjectRole, invitedBy: UUID): UUID {
        val id = UUID.randomUUID()
        val status = InvitationStatus.PENDING
        val invitedAt = Instant.now()
        val invitation = ProjectInvitation(id, projectId, email, role, invitedBy, status, invitedAt)
        invitations[id] = invitation
        return id
    }
    override fun getInvitationsForProject(projectId: UUID): List<ProjectInvitation> {
        return invitations.values.filter { it.project_id == projectId }
    }

    override fun getInvitationsForUser(email: String): List<ProjectInvitationExtended> {
        val invite = invitations.values.filter { it.email == email }
        return invite.map { invitation ->
            val project = projectRepo.findById(invitation.project_id) ?: throw IllegalArgumentException("Project with ID ${invitation.project_id} does not exist")
            ProjectInvitationExtended(
                id = invitation.id,
                project = ProjectBasicInfo(
                    id = invitation.project_id,
                    name = project.name,
                    description = project.description,
                    updatedAt = project.updatedAt,
                ),
                email = invitation.email,
                role = invitation.role,
                invited_by = invitation.invited_by,
                status = invitation.status,
                invited_at = invitation.invited_at
            )
        }
    }

    override fun getInvitationById(id: UUID): ProjectInvitation? {
        return invitations[id]
    }

    override fun getInvitationByEmailAndProject(email: String, projectId: UUID): ProjectInvitation? {
        return invitations.values.find { it.email == email && it.project_id == projectId }
    }

    override fun updateRole(id: UUID, newRole: ProjectRole) {
        invitations[id]?.let {
            invitations[id] = it.copy(role = newRole)
        }
    }

    override fun updateStatus(id: UUID, newStatus: InvitationStatus) {
        invitations[id]?.let {
            invitations[id] = it.copy(status = newStatus)
        }
    }
    override fun deleteInvitation(id: UUID) {
        invitations.remove(id)
    }
    override fun deleteAllForProject(projectId: UUID) {
        invitations.values.removeIf { it.project_id == projectId }
    }

    override fun deleteAnsweredInvitations(projectId: UUID, email: String) {
        invitations.values.removeIf { it.project_id == projectId && it.email == email && it.status != InvitationStatus.PENDING }
    }

    override fun clear() {
        invitations.clear()
    }
}