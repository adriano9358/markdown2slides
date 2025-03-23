package pt.isel.markdown2slides

import jakarta.inject.Named
import pt.isel.markdown2slides.mem.TransactionManagerInMem
import java.time.Instant
import java.util.*


@Named
class ProjectInfoService(
    private val trxManager: TransactionManager = TransactionManagerInMem(),
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
        success(project)
    }

    fun deleteProject(
        ownerId: UUID,
        projectId: UUID,
    ): Either<ProjectError, Unit> = trxManager.run {
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
        projectId: UUID
    ): Either<ProjectError, ProjectInfo> = trxManager.run {
        val project = repoProjectInfo.findById(projectId) ?: return@run failure(ProjectError.ProjectNotFound)
        success(project)
    }

    fun editProjectDetails(
        projectId: UUID,
        name: String?,
        description: String?,
        visibility: Visibility?
    ): Either<ProjectError, ProjectInfo> = trxManager.run {
        val project = repoProjectInfo.findById(projectId) ?: return@run failure(ProjectError.ProjectNotFound)

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
    data object SomeError : ProjectError()
    data object ContentDeletionError : ProjectError()
    data object ProjectNotFound : ProjectError()
    data object ImageDeletionError : ProjectError()
    data object ImageNotFound : ProjectError()
}