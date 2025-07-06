package pt.isel.markdown2slides.data

import jakarta.inject.Named
import pt.isel.markdown2slides.InvitationStatus
import pt.isel.markdown2slides.ProjectInvitation
import pt.isel.markdown2slides.ProjectInvitationExtended
import pt.isel.markdown2slides.ProjectRole
import java.util.*

@Named
interface RepositoryInvitations {

    fun createInvitation(projectId: UUID, email: String, role: ProjectRole, invitedBy: UUID): UUID
    fun getInvitationsForProject(projectId: UUID): List<ProjectInvitation>
    fun getInvitationsForUser(email: String): List<ProjectInvitationExtended>
    fun getInvitationById(id: UUID): ProjectInvitation?
    fun getInvitationByEmailAndProject(email: String, projectId: UUID): ProjectInvitation?
    fun updateRole(id: UUID, newRole: ProjectRole)
    fun updateStatus(id: UUID, newStatus: InvitationStatus)
    fun deleteInvitation(id: UUID)
    fun deleteAllForProject(projectId: UUID)
    fun deleteAnsweredInvitations(projectId: UUID, email: String)
    fun clear()
}
