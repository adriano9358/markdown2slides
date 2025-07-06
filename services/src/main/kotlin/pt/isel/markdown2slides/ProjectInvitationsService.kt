package pt.isel.markdown2slides


import jakarta.inject.Named
import pt.isel.markdown2slides.data.TransactionManager
import java.util.*

@Named
class ProjectInvitationsService(
    private val trxManager: TransactionManager,
) {

    fun createInvitation(
        projectId: UUID,
        email: String,
        role: ProjectRole,
        invitedBy: UUID
    ): Either<ProjectError, UUID> = trxManager.run {

        val inviter = repoUser.findById(invitedBy)
            ?: return@run failure(ProjectError.InviterNotFound)

        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        if(project.ownerId != inviter.id)
            return@run failure(ProjectError.InviterNotAuthorized)

        val invited = repoUser.findByEmail(email)
        val collaborators = repoCollaborators.getCollaborators(projectId)
        if (invited != null && collaborators.any { it.user.id == invited.id })
            return@run failure(ProjectError.UserAlreadyInProject)

        val existingInvitation = repoInvitations.getInvitationByEmailAndProject(email, projectId)

        when (existingInvitation?.status) {
            InvitationStatus.PENDING -> return@run failure(ProjectError.InvitationAlreadyExists)
            InvitationStatus.DECLINED -> repoInvitations.deleteInvitation(existingInvitation.id)
            else -> {}
        }

        val invitationId = repoInvitations.createInvitation(projectId, email, role, invitedBy)

        success(invitationId)
    }

    fun getInvitationsForUser(email: String): Either<ProjectError, List<ProjectInvitationExtended>> = trxManager.run {
        val invitations = repoInvitations.getInvitationsForUser(email)
        success(invitations)
    }

    fun modifyInvitationRole(
        invitationId: UUID,
        inviterId: UUID,
        newRole: ProjectRole
    ): Either<ProjectError, Unit> = trxManager.run {
        val existing = repoInvitations.getInvitationById(invitationId)
            ?: return@run failure(ProjectError.InvitationNotFound)

        if(existing.invited_by != inviterId) {
            return@run failure(ProjectError.InviterNotAuthorized)
        }

        if (existing.status != InvitationStatus.PENDING) {
            return@run failure(ProjectError.InvitationAlreadyResponded)
        }

        if(newRole == existing.role)
            return@run success(Unit)

        repoInvitations.updateRole(invitationId, newRole)

        success(Unit)
    }

    fun getInvitationsForProject(
        projectId: UUID,
        inviterId: UUID
    ): Either<ProjectError, List<ProjectInvitation>> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        if (project.ownerId != inviterId) {
            return@run failure(ProjectError.InviterNotAuthorized)
        }

        val invitations = repoInvitations.getInvitationsForProject(projectId)
        success(invitations)
    }

    fun respondToInvitation(
        invitationId: UUID,
        email: String,
        status: InvitationStatus
    ): Either<ProjectError, Unit> = trxManager.run {
        if (status == InvitationStatus.PENDING) {
            return@run failure(ProjectError.InvalidInvitationResponse)
        }

        val existing = repoInvitations.getInvitationById(invitationId)
            ?: return@run failure(ProjectError.InvitationNotFound)

        if (existing.email != email) {
            return@run failure(ProjectError.InviterNotAuthorized)
        }

        if (existing.status != InvitationStatus.PENDING) {
            return@run failure(ProjectError.InvitationAlreadyResponded)
        }

        repoInvitations.updateStatus(invitationId, status)

        val user = repoUser.findByEmail(email)
            ?: return@run failure(ProjectError.UserNotFound)

        if (status == InvitationStatus.ACCEPTED) {
            repoCollaborators.addCollaborator(existing.project_id, user.id, existing.role)
        }
        success(Unit)
    }

    fun deleteInvitation(
        invitationId: UUID,
        inviterId: UUID,
    ): Either<ProjectError, Unit> = trxManager.run {
        val existing = repoInvitations.getInvitationById(invitationId)
            ?: return@run failure(ProjectError.InvitationNotFound)
        if (existing.status != InvitationStatus.PENDING) {
            return@run failure(ProjectError.InvitationAlreadyResponded)
        }
        if (existing.invited_by != inviterId) {
            return@run failure(ProjectError.InviterNotAuthorized)
        }

        repoInvitations.deleteInvitation(invitationId)
        success(Unit)
    }

    fun deleteAllForProject(
        projectId: UUID,
        inviterId: UUID
    ): Either<ProjectError, Unit> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        if (project.ownerId != inviterId) {
            return@run failure(ProjectError.InviterNotAuthorized)
        }

        repoInvitations.deleteAllForProject(projectId)
        success(Unit)
    }
}

