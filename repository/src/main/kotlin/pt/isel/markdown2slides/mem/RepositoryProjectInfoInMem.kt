package pt.isel.markdown2slides.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.ProjectInfo
import pt.isel.markdown2slides.RepositoryProjectInfo
import pt.isel.markdown2slides.SlideTheme
import pt.isel.markdown2slides.Visibility
import java.time.Instant

@Named
class RepositoryProjectInfoInMem: RepositoryProjectInfo{

    private val projects = mutableListOf<ProjectInfo>()

    override fun createProject(name: String, description: String, ownerId: Long, visibility: Visibility): ProjectInfo =
        ProjectInfo(projects.count().toLong(), name, description, ownerId, Instant.now(), Instant.now(), SlideTheme(), visibility)
            .also { projects.add(it) }


    override fun getPersonalProjects(ownerId: Long): List<ProjectInfo> =
        projects.filter { it.ownerId == ownerId }.toList()


    override fun findById(id: Int): ProjectInfo? = projects.firstOrNull { it.id == id.toLong() }

    override fun findAll(): List<ProjectInfo> = projects.toList()

    override fun save(entity: ProjectInfo) {
        projects.removeIf{ it.id == entity.id}
        projects.add(entity)
    }

    override fun deleteById(id: Int) {
        projects.removeIf { it.id == id.toLong() }
    }

    override fun clear() {
        projects.clear()
    }

}