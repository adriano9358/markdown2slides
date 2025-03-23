package pt.isel.markdown2slides

import jakarta.inject.Named
import java.util.*

@Named
interface RepositoryProjectContent {
    fun createProject(userId: UUID, projectId: UUID)
    fun checkProjectExists(userId: UUID, projectId: UUID): Boolean
    fun saveMarkdown(userId: UUID, projectId: UUID, content: String)
    fun getMarkdown(userId: UUID, projectId: UUID): String?
    fun deleteMarkdown(userId: UUID, projectId: UUID): Boolean
    fun saveImage(userId: UUID, projectId: UUID, image: String, extension: String, imageBytes: ByteArray)
    fun getImage(userId: UUID, projectId: UUID, image: String, extension: String): ByteArray?
    fun deleteImage(userId: UUID, projectId: UUID, image: String, extension: String): Boolean
    fun deleteProjectContent(userId: UUID, projectId: UUID): Boolean
}
