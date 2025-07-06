package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import pt.isel.markdown2slides.data.RepositoryCollaborators
import java.util.*


class RepositoryCollaboratorsJdbi(
    private val handle: Handle
): RepositoryCollaborators {

    override fun addCollaborator(projectId: UUID, userId: UUID, role: ProjectRole) {
        handle.createUpdate("""
            INSERT INTO m2s.project_collaborators(project_id, user_id, role)
            VALUES (:projectId, :userId, :role)
            ON CONFLICT (project_id, user_id) DO UPDATE SET role = :role
        """)
            .bind("projectId", projectId)
            .bind("userId", userId)
            .bind("role", role.toString())
            .execute()
    }

    override fun getCollaborators(projectId: UUID): List<ProjectCollaborator> {
        return handle.createQuery("""
        SELECT 
            pc.project_id,
            u.id AS user_id,
            u.name AS user_name,
            u.email AS user_email,
            pc.role
        FROM m2s.project_collaborators pc
        JOIN m2s.users u ON pc.user_id = u.id
        WHERE pc.project_id = :projectId
    """)
            .bind("projectId", projectId)
            .map { rs, _ ->
                ProjectCollaborator(
                    project_id = rs.getObject("project_id", UUID::class.java),
                    user = UserInfo(
                        id = rs.getObject("user_id", UUID::class.java),
                        name = rs.getString("user_name"),
                        email = rs.getString("user_email")
                    ),
                    role = ProjectRole.valueOf(rs.getString("role"))
                )
            }
            .list()
    }

    override fun getUserRoleInProject(userId: UUID, projectId: UUID): ProjectRole? {
        return handle.createQuery("""
            SELECT role FROM m2s.project_collaborators
            WHERE user_id = :userId AND project_id = :projectId
        """)
            .bind("userId", userId)
            .bind("projectId", projectId)
            .mapTo(ProjectRole::class.java)
            .findOne()
            .orElse(null)
    }

    override fun removeCollaborator(projectId: UUID, userId: UUID) {
        handle.createUpdate("""
            DELETE FROM m2s.project_collaborators
            WHERE project_id = :projectId AND user_id = :userId
        """)
            .bind("projectId", projectId)
            .bind("userId", userId)
            .execute()
    }

    override fun getUserProjects(userId: UUID): List<UUID> {
        return handle.createQuery("""
            SELECT project_id FROM m2s.project_collaborators
            WHERE user_id = :userId
        """)
            .bind("userId", userId)
            .mapTo(UUID::class.java)
            .list()
    }

    override fun clear() {
        handle.createUpdate("""
            DELETE FROM m2s.project_collaborators
        """).execute()
    }
}
