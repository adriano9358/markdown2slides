package pt.isel.markdown2slides

import jakarta.inject.Named
import pt.isel.markdown2slides.data.TransactionManager
import pt.isel.markdown2slides.file.RepositoryProjectContent
import java.time.Instant
import java.util.*


@Named
class ProjectInfoService(
    private val trxManager: TransactionManager /*= TransactionManagerInMem()*/,
    private val repoProjectContent: RepositoryProjectContent = RepositoryProjectContentFileSystem(),
) {

    fun createProject(
        name: String,
        description: String,
        ownerId: UUID,
        visibility: Visibility
    ): Either<ProjectError, ProjectInfo> = trxManager.run {
        val project = repoProjectInfo.createProject(name, description, ownerId, visibility)
        repoProjectContent.createProject(ownerId, project.id)
        repoCollaborators.addCollaborator(project.id, ownerId, ProjectRole.ADMIN)
        success(project)
    }

    fun deleteProject(
        ownerId: UUID,
        projectId: UUID,
    ): Either<ProjectError, Unit> = trxManager.run {
        val project = repoProjectInfo.findById(projectId) ?: return@run failure(ProjectError.ProjectNotFound)
        if (project.ownerId != ownerId) return@run failure(ProjectError.ProjectNotAuthorized)
        repoProjectInfo.deleteById(projectId)
        val deleted = repoProjectContent.deleteProjectContent(ownerId, projectId)
        if(deleted) success(Unit) else failure(ProjectError.ContentDeletionError)
    }

    fun getPersonalProjects(
        ownerId: UUID,
    ): Either<ProjectError, List<ProjectInfo>> = trxManager.run {
        val projects = repoProjectInfo.getPersonalProjects(ownerId)
        success(projects)
    }

    fun getProjectDetails(
        ownerId: UUID,
        projectId: UUID
    ): Either<ProjectError, ProjectInfo> = trxManager.run {
        val project = repoProjectInfo.findById(projectId) ?: return@run failure(ProjectError.ProjectNotFound)
        if(project.ownerId != ownerId) return@run failure(ProjectError.ProjectNotAuthorized)
        success(project)
    }

    fun editProjectDetails(
        ownerId: UUID,
        projectId: UUID,
        name: String?,
        description: String?,
        visibility: Visibility?
    ): Either<ProjectError, ProjectInfo> = trxManager.run {
        val project = repoProjectInfo.findById(projectId) ?: return@run failure(ProjectError.ProjectNotFound)
        if(project.ownerId != ownerId) return@run failure(ProjectError.ProjectNotAuthorized)


        val updated = project.copy(
            name = name ?: project.name,
            description = description ?: project.description,
            visibility = visibility ?: project.visibility,
            updatedAt = Instant.now()
        )

        repoProjectInfo.save(updated)
        success(updated)
    }


}

sealed class ProjectError {
    data object ContentDeletionError : ProjectError()
    data object ProjectNotFound : ProjectError()
    data object ProjectNotAuthorized : ProjectError()
    data object ImageDeletionError : ProjectError()
    data object ImageNotFound : ProjectError()
    data object InvalidRole : ProjectError()
    data object InviterNotFound : ProjectError()
    data object InvitationNotFound : ProjectError()
    data object UserNotFound : ProjectError()
    data object InvitationAlreadyExists : ProjectError()
    data object InviterNotAuthorized : ProjectError()
    data object InvitationAlreadyResponded : ProjectError()
    data object UserAlreadyInProject : ProjectError()
    data object InvalidInvitationResponse : ProjectError()
    data object UserNotInProject : ProjectError()

    data class DatabaseError(val message: String) : ProjectError()
    data class FileSystemError(val message: String) : ProjectError()
    data class UnknownError(val message: String) : ProjectError()
}