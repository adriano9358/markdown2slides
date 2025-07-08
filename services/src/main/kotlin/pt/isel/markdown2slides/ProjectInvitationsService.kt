package pt.isel.markdown2slides


import jakarta.inject.Named
import pt.isel.markdown2slides.data.TransactionManager
import java.util.*

@Named
class ProjectInvitationsService(
    private val trxManager: TransactionManager,
) {

    /**
     * Creates a new project invitation.
     * @param projectId The ID of the project to invite the user to.
     * @param email The email of the user to invite.
     * @param role The role to assign to the invited user.
     * @param invitedBy The ID of the user who is inviting.
     * @return Either a ProjectError or the ID of the created invitation.
     */
    fun createInvitation(
        projectId: UUID,
        email: String,
        role: ProjectRole,
        invitedBy: UUID
    ): Either<ProjectError, UUID> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        if(project.ownerId != invitedBy)
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

    /**
     * Retrieves all invitations for a specific user by their email.
     * @param email The email of the user to retrieve invitations for.
     * @return Either a ProjectError or a list of ProjectInvitationExtended.
     */
    fun getInvitationsForUser(email: String): Either<ProjectError, List<ProjectInvitationExtended>> = trxManager.run {
        val invitations = repoInvitations.getInvitationsForUser(email)
        success(invitations)
    }

    /**
     * Modifies the role of an existing pending invitation.
     * @param invitationId The ID of the invitation to modify.
     * @param inviterId The ID of the user who is modifying the invitation.
     * @param newRole The new role to assign to the invitation.
     * @return Either a ProjectError or Unit on success.
     */
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

    /** Retrieves all invitations for a specific project.
     * @param projectId The ID of the project to retrieve invitations for.
     * @param inviterId The ID of the user who is inviting.
     * @return Either a ProjectError or a list of ProjectInvitation.
     */
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

    /**
     * Responds to an invitation by updating its status.
     * @param invitationId The ID of the invitation to respond to.
     * @param email The email of the user responding to the invitation.
     * @param status The new status of the invitation (ACCEPTED or DECLINED).
     * @return Either a ProjectError or Unit on success.
     */
    fun respondToInvitation(
        invitationId: UUID,
        email: String,
        status: InvitationStatus
    ): Either<ProjectError, Unit> = trxManager.run {
        if (status == InvitationStatus.PENDING)
            return@run failure(ProjectError.InvalidInvitationResponse)

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

    /**
     * Deletes a pending invitation by its ID.
     * @param invitationId The ID of the invitation to delete.
     * @param inviterId The ID of the user who is deleting the invitation.
     * @return Either a ProjectError or Unit on success.
     */
    fun deleteInvitation(
        invitationId: UUID,
        inviterId: UUID,
    ): Either<ProjectError, Unit> = trxManager.run {
        val existing = repoInvitations.getInvitationById(invitationId)
            ?: return@run failure(ProjectError.InvitationNotFound)

        if (existing.status != InvitationStatus.PENDING)
            return@run failure(ProjectError.InvitationAlreadyResponded)

        if (existing.invited_by != inviterId)
            return@run failure(ProjectError.InviterNotAuthorized)

        repoInvitations.deleteInvitation(invitationId)
        success(Unit)
    }

    /**
     * Deletes all pending invitations for a specific project.
     * @param projectId The ID of the project to delete invitations for.
     * @param inviterId The ID of the user who is deleting the invitations.
     * @return Either a ProjectError or Unit on success.
     */
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

