package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import pt.isel.markdown2slides.data.RepositoryProjectInfo
import java.time.Instant
import java.util.*


class RepositoryProjectInfoJdbi(
    private val handle: Handle,
) : RepositoryProjectInfo {
    override fun createProject(name: String, description: String, ownerId: UUID, visibility: Visibility): ProjectInfo {
        val id = UUID.randomUUID()
        handle.createUpdate(
            """
            INSERT INTO m2s.project_info(id, name, description, owner_id, created_at, updated_at, visibility)
            VALUES (:id, :name, :description, :ownerId, NOW(), NOW(), :visibility)
            """.trimIndent()
        )
            .bind("id", id)
            .bind("name", name)
            .bind("description", description)
            .bind("ownerId", ownerId)
            .bind("visibility", visibility.toString())
            .execute()

        return ProjectInfo(id, name, description, ownerId, Instant.now(), Instant.now(), "white", visibility)
    }

    /*override fun getPersonalProjects(ownerId: UUID): List<ProjectInfo> {
        return handle
            .createQuery("SELECT * FROM m2s.project_info WHERE owner_id = :ownerId")
            .bind("ownerId", ownerId)
            .mapTo(ProjectInfo::class.java)
            .list()
    }*/

    override fun getPersonalProjects(userId: UUID): List<ProjectInfo> {
        return handle
            .createQuery(
                """
            SELECT pi.* 
            FROM m2s.project_info pi
            JOIN m2s.project_collaborators pc ON pi.id = pc.project_id
            WHERE pc.user_id = :userId
            """
            )
            .bind("userId", userId)
            .mapTo(ProjectInfo::class.java)
            .list()
    }

    override fun findById(id: UUID): ProjectInfo? =
        handle
            .createQuery("SELECT * FROM m2s.project_info WHERE id = :id")
            .bind("id", id)
            .mapTo(ProjectInfo::class.java)
            .findOne()
            .orElse(null)

    override fun findAll(): List<ProjectInfo> {
        return handle
            .createQuery("SELECT * FROM m2s.project_info")
            .mapTo(ProjectInfo::class.java)
            .list()
    }

    override fun save(entity: ProjectInfo) {
        handle.createUpdate(
            """
            UPDATE m2s.project_info
            SET name = :name, description = :description, updated_at = NOW(), visibility = :visibility
            WHERE id = :id
            """.trimIndent()
        )
            .bind("id", entity.id)
            .bind("name", entity.name)
            .bind("description", entity.description)
            .bind("visibility", entity.visibility.toString())
            .execute()
    }

    override fun deleteById(id: UUID) {
        handle.createUpdate(
            """
            DELETE FROM m2s.project_info
            WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate(
            """
            DELETE FROM m2s.project_info
            """.trimIndent()
        )
            .execute()
    }


}
