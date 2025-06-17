package pt.isel.markdown2slides


import jakarta.inject.Named
import java.util.*

@Named
class ProjectCollaboratorsService(
    private val trxManager: TransactionManager,
) {

    /*fun addCollaborator(
        projectId: UUID,
        userId: UUID,
        role: ProjectRole
    ): Either<ProjectError, Unit> = trxManager.run {
        if (role != ProjectRole.VIEWER && role != ProjectRole.EDITOR) {
            return@run failure(ProjectError.InvalidRole)
        }

        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        val user = repoUser.findById(userId)
            ?: return@run failure(ProjectError.UserNotFound)

        repoCollaborators.addCollaborator(projectId, userId, role)
        success(Unit)
    }*/

    fun getCollaborators(
        userId: UUID,
        projectId: UUID
    ): Either<ProjectError, List<ProjectCollaborator>> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        val collaborators = repoCollaborators.getCollaborators(projectId)

        if(project.visibility == Visibility.PUBLIC || userId == project.ownerId || collaborators.any { it.user.id == userId }) {
            success(collaborators)
        } else{
            failure(ProjectError.ProjectNotAuthorized)
        }
    }

    fun removeCollaborator(ownerId: UUID, projectId: UUID, userToRemoveId: UUID): Either<ProjectError, Unit> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        if( project.ownerId != ownerId && ownerId != userToRemoveId)
            return@run failure(ProjectError.ProjectNotAuthorized)

        val collaborators = repoCollaborators.getCollaborators(projectId)

        if (!collaborators.any { it.user.id == userToRemoveId }) {
            return@run failure(ProjectError.UserNotInProject)
        }

        repoCollaborators.removeCollaborator(projectId, userToRemoveId)

        val user = repoUser.findById(userToRemoveId)
            ?: return@run failure(ProjectError.UserNotFound)
        repoInvitations.deleteAnsweredInvitations(projectId, user.email)

        success(Unit)
    }

    fun getUserRoleInProject(userId: UUID, projectId: UUID): Either<ProjectError, ProjectRole> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        val role = repoCollaborators.getUserRoleInProject(userId, projectId)
            ?: return@run failure(ProjectError.UserNotInProject)

        success(role)
    }

}