package pt.isel.markdown2slides.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.ProjectCollaborator
import pt.isel.markdown2slides.ProjectRole
import pt.isel.markdown2slides.RepositoryCollaborators
import pt.isel.markdown2slides.UserInfo
import java.util.*

@Named
class RepositoryCollaboratorsInMem: RepositoryCollaborators {

    private val collaborators = mutableMapOf<UUID, MutableList<ProjectCollaborator>>()

    override fun addCollaborator(projectId: UUID, userId: UUID, role: ProjectRole) {
        val collaborator = ProjectCollaborator(
            project_id = projectId,
            user = UserInfo(userId, "", ""),
            role = role,
        )
        collaborators.computeIfAbsent(projectId) { mutableListOf() }.add(collaborator)
    }

    override fun getCollaborators(projectId: UUID): List<ProjectCollaborator> {
        return collaborators[projectId]?.toList() ?: emptyList()
    }

    override fun getUserRoleInProject(userId: UUID, projectId: UUID): ProjectRole? {
        return collaborators[projectId]?.find { it.user.id == userId }?.role
    }

    override fun removeCollaborator(projectId: UUID, userId: UUID) {
        collaborators[projectId]?.removeIf { it.user.id == userId }
        if (collaborators[projectId].isNullOrEmpty()) {
            collaborators.remove(projectId)
        }
    }

    override fun getUserProjects(userId: UUID): List<UUID> {
        return collaborators.filter { it.value.any { collaborator -> collaborator.user.id == userId } }
            .keys.toList()
    }


}