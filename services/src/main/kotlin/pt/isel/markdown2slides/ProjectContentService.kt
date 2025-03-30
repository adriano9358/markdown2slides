package pt.isel.markdown2slides

import jakarta.inject.Named
import java.util.*

@Named
class ProjectContentService(
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

    fun getProjectContent(ownerId: UUID, projectId: UUID): Either<ProjectError, String> = handleErrors {
        val content = repoProjectContent.getMarkdown(ownerId, projectId) ?: return failure(ProjectError.ProjectNotFound)
        return success(content)
    }

    fun uploadImage(
        ownerId: UUID,
        projectId: UUID,
        imageName: String,
        extension: String,
        imageBytes: ByteArray
    ): Either<ProjectError, Unit> = handleErrors {
        val projectExists = repoProjectContent.checkProjectExists(ownerId, projectId)
        if(!projectExists) return failure(ProjectError.ProjectNotFound)
        repoProjectContent.saveImage(ownerId, projectId, imageName, extension, imageBytes)
        return success(Unit)
    }

    fun deleteImage(
        ownerId: UUID,
        projectId: UUID,
        imageName: String,
        extension: String
    ): Either<ProjectError, Unit> = handleErrors {
        repoProjectContent.getImage(ownerId, projectId, imageName, extension) ?: return failure(ProjectError.ImageNotFound)
        val deleted = repoProjectContent.deleteImage(ownerId, projectId, imageName, extension)
        return if (deleted)  success(Unit)
        else failure(ProjectError.ImageDeletionError)
    }

    fun getImage(
        ownerId: UUID,
        projectId: UUID,
        imageName: String,
        extension: String
    ): Either<ProjectError, ByteArray> = handleErrors {
        val image = repoProjectContent.getImage(ownerId, projectId, imageName, extension) ?: return failure(ProjectError.ImageNotFound)
        return success(image)
    }

}