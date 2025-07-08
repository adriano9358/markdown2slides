package pt.isel.markdown2slides

import jakarta.inject.Named
import pt.isel.markdown2slides.data.TransactionManager
import pt.isel.markdown2slides.file.RepositoryProjectContent
import java.util.*

@Named
class ProjectContentService(
    private val trxManager: TransactionManager,
    private val repoProjectContent: RepositoryProjectContent = RepositoryProjectContentFileSystem()
) {

    fun updateProjectContent(
        ownerId: UUID,
        projectId: UUID,
        markdown: String,
    ): Either<ProjectError, Unit> = handleErrors {
        val projectExists = repoProjectContent.checkProjectExists(ownerId, projectId)
        if(!projectExists) return failure(ProjectError.ProjectNotFound)
        repoProjectContent.saveMarkdown(ownerId, projectId, markdown)
        return success(Unit)
    }
    data class ProjectDetails(val content: String, val ownerId: UUID)
    fun getProjectContent(userId: UUID, projectId: UUID): Either<ProjectError, ProjectDetails> = handleErrors {
        return when(val ownerId = isOwnerOrCollaborator(userId, projectId)) {
            is Failure -> failure(ProjectError.UserNotInProject)
            is Success -> {
                val content = repoProjectContent.getMarkdown(ownerId.value, projectId)
                    ?: return failure(ProjectError.ProjectNotFound)
                success(ProjectDetails(content, ownerId.value))
            }
        }

    }

    private fun isOwnerOrCollaborator(
        ownerId: UUID,
        projectId: UUID
    ): Either<ProjectError, UUID> = trxManager.run {
        val project = repoProjectInfo.findById(projectId)
            ?: return@run failure(ProjectError.ProjectNotFound)

        if(project.ownerId != ownerId){
            val collaborators = repoCollaborators.getCollaborators(projectId)
            if(collaborators.none { it.user.id == ownerId }) {
                return@run failure(ProjectError.ProjectNotAuthorized)
            }
        }
        success(project.ownerId)
    }

    fun uploadImage(
        userId: UUID,
        projectId: UUID,
        imageName: String,
        extension: String,
        imageBytes: ByteArray
    ): Either<ProjectError, Unit> = handleErrors {
        val projectResult = trxManager.run {
            val project = repoProjectInfo.findById(projectId)
                ?: return@run failure(ProjectError.ProjectNotFound)
            val collaborators = repoCollaborators.getCollaborators(projectId)
            if(collaborators.none { it.user.id == userId }) {
                return@run failure(ProjectError.ProjectNotAuthorized)
            }
            if(collaborators.any{ it.user.id == userId && it.role == ProjectRole.VIEWER})
                return@run failure(ProjectError.ProjectNotAuthorized)

            success(project)
        }
        when(projectResult) {
            is Failure -> return projectResult
            is Success -> {
                val projectExists = repoProjectContent.checkProjectExists(projectResult.value.ownerId , projectId)
                if(!projectExists) return failure(ProjectError.ProjectNotFound)
                repoProjectContent.saveImage(projectResult.value.ownerId, projectId, imageName, extension, imageBytes)
                return success(Unit)
            }
        }
    }

    fun deleteImage(
        ownerId: UUID,
        projectId: UUID,
        imageName: String,
        extension: String
    ): Either<ProjectError, Unit> = handleErrors {
        val projectResult = trxManager.run {
            val project = repoProjectInfo.findById(projectId)
                ?: return@run failure(ProjectError.ProjectNotFound)
            if(project.ownerId != ownerId) {
                    return@run failure(ProjectError.ProjectNotAuthorized)
            }
            success(project)
        }.let { projectResult ->
            when(projectResult) {
                is Failure -> return projectResult
                is Success -> projectResult.value
            }
        }
        repoProjectContent.getImage(projectResult.ownerId, projectId, imageName, extension) ?: return failure(ProjectError.ImageNotFound)
        val deleted = repoProjectContent.deleteImage(ownerId, projectId, imageName, extension)
        return if (deleted)  success(Unit)
        else failure(ProjectError.ImageDeletionError)
    }

    fun getImage(
        userId: UUID,
        projectId: UUID,
        imageName: String,
        extension: String
    ): Either<ProjectError, ByteArray> = handleErrors {

        val projectResult = trxManager.run {
            val project = repoProjectInfo.findById(projectId)
                ?: return@run failure(ProjectError.ProjectNotFound)
            val collaborators = repoCollaborators.getCollaborators(projectId)
            if(collaborators.none { it.user.id == userId }) {
                return@run failure(ProjectError.ProjectNotAuthorized)
            }
            success(project)
        }
        when(projectResult) {
            is Failure -> return projectResult
            is Success -> {
                val projectExists = repoProjectContent.checkProjectExists(projectResult.value.ownerId , projectId)
                if(!projectExists) return failure(ProjectError.ProjectNotFound)
                val image = repoProjectContent.getImage(projectResult.value.ownerId, projectId, imageName, extension) ?: return failure(ProjectError.ImageNotFound)
                return success(image)
            }
        }

    }

}