package pt.isel.markdown2slides.data.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.ProjectInfo
import pt.isel.markdown2slides.data.RepositoryProjectInfo
import pt.isel.markdown2slides.Visibility
import java.time.Instant
import java.util.*

const val DEFAULT_THEME = "white"
@Named
class RepositoryProjectInfoInMem(private val collaboratorsRepo: RepositoryCollaboratorsInMem): RepositoryProjectInfo {

    private val projects = mutableListOf<ProjectInfo>()

    override fun createProject(name: String, description: String, ownerId: UUID, visibility: Visibility): ProjectInfo =
        ProjectInfo(UUID.randomUUID(), name, description, ownerId, Instant.now(), Instant.now(), DEFAULT_THEME, visibility)
            .also { projects.add(it) }

    override fun getPersonalProjects(ownerId: UUID): List<ProjectInfo>{
        return collaboratorsRepo.getUserProjects(ownerId)
            .mapNotNull { findById(it) }
    } //= projects.filter { it.ownerId == ownerId }.toList()

    override fun findById(id: UUID): ProjectInfo? = projects.firstOrNull { it.id == id }

    override fun findAll(): List<ProjectInfo> = projects.toList()

    override fun save(entity: ProjectInfo) {
        projects.removeIf{ it.id == entity.id}
        projects.add(entity)
    }

    override fun deleteById(id: UUID) {
        projects.removeIf { it.id == id }
    }

    override fun clear() {
        projects.clear()
    }

}