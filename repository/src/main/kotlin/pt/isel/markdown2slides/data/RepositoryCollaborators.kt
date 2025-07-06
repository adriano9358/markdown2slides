package pt.isel.markdown2slides.data

import jakarta.inject.Named
import pt.isel.markdown2slides.ProjectCollaborator
import pt.isel.markdown2slides.ProjectRole
import java.util.*

@Named
interface RepositoryCollaborators {
    fun addCollaborator(projectId: UUID, userId: UUID, role: ProjectRole)
    fun getCollaborators(projectId: UUID): List<ProjectCollaborator>
    fun getUserRoleInProject(userId: UUID, projectId: UUID): ProjectRole?
    fun removeCollaborator(projectId: UUID, userId: UUID)
    fun getUserProjects(userId: UUID): List<UUID>
    fun clear()
}
