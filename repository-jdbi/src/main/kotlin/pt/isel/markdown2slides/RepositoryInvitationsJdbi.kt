package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import java.util.*

class RepositoryInvitationsJdbi(
    private val handle: Handle
):RepositoryInvitations {
    override fun createInvitation(
        projectId: UUID,
        email: String,
        role: ProjectRole,
        invitedBy: UUID
    ): UUID {
        val invitationId = UUID.randomUUID()
        val status = InvitationStatus.PENDING
        handle.createUpdate("""
            INSERT INTO m2s.project_invitations(id, project_id, email, role, invited_by, status, invited_at)
            VALUES (:id, :projectId, :email, :role, :invitedBy, :status, NOW())
        """)
            .bind("id", invitationId)
            .bind("projectId", projectId)
            .bind("email", email)
            .bind("role", role.toString())
            .bind("invitedBy", invitedBy)
            .bind("status", status.toString())
            .execute()
        return invitationId
    }

    override fun getInvitationsForProject(projectId: UUID): List<ProjectInvitation> {
        return handle.createQuery("""
            SELECT * FROM m2s.project_invitations WHERE project_id = :projectId
        """)
            .bind("projectId", projectId)
            .mapTo(ProjectInvitation::class.java)
            .list()
    }

    override fun getInvitationsForUser(email: String): List<ProjectInvitationExtended> {
        return handle.createQuery("""
        SELECT 
            pi.id AS invitation_id,
            pi.project_id,
            p.name AS project_name,
            p.description AS project_description,
            p.updated_at AS project_updated_at,
            pi.email,
            pi.role,
            pi.invited_by,
            pi.status,
            pi.invited_at
        FROM m2s.project_invitations pi
        JOIN m2s.project_info p ON pi.project_id = p.id
        WHERE pi.email = :email
    """)
            .bind("email", email)
            .map { rs, _ ->
                ProjectInvitationExtended(
                    id = rs.getObject("invitation_id", UUID::class.java),
                    project = ProjectBasicInfo(
                        id = rs.getObject("project_id", UUID::class.java),
                        name = rs.getString("project_name"),
                        description = rs.getString("project_description"),
                        updatedAt = rs.getTimestamp("project_updated_at").toInstant()
                    ),
                    email = rs.getString("email"),
                    role = ProjectRole.valueOf(rs.getString("role")),
                    invited_by = rs.getObject("invited_by", UUID::class.java),
                    status = InvitationStatus.valueOf(rs.getString("status")),
                    invited_at = rs.getTimestamp("invited_at").toInstant()
                )
            }
            .list()
    }

    override fun getInvitationById(id: UUID): ProjectInvitation? {
        return handle.createQuery("""
            SELECT * FROM m2s.project_invitations WHERE id = :id
        """)
            .bind("id", id)
            .mapTo(ProjectInvitation::class.java)
            .findOne()
            .orElse(null)
    }

    override fun getInvitationByEmailAndProject(email: String, projectId: UUID): ProjectInvitation? {
        return handle.createQuery("""
            SELECT * FROM m2s.project_invitations 
            WHERE email = :email AND project_id = :projectId
        """)
            .bind("email", email)
            .bind("projectId", projectId)
            .mapTo(ProjectInvitation::class.java)
            .findOne()
            .orElse(null)
    }

    override fun updateRole(id: UUID, newRole: ProjectRole) {
        handle.createUpdate("""
            UPDATE m2s.project_invitations
            SET role = :role
            WHERE id = :id
        """)
            .bind("id", id)
            .bind("role", newRole.toString())
            .execute()
    }

    override fun updateStatus(id: UUID, newStatus: InvitationStatus) {
        handle.createUpdate("""
            UPDATE m2s.project_invitations
            SET status = :status
            WHERE id = :id
        """)
            .bind("id", id)
            .bind("status", newStatus.toString())
            .execute()
    }

    override fun deleteInvitation(id: UUID) {
        handle.createUpdate("""
            DELETE FROM m2s.project_invitations WHERE id = :id
        """)
            .bind("id", id)
            .execute()
    }

    override fun deleteAnsweredInvitations(projectId: UUID, email: String) {
        handle.createUpdate("""
            DELETE FROM m2s.project_invitations 
            WHERE project_id = :projectId AND email = :email AND status IN ('ACCEPTED', 'DECLINED')
        """)
            .bind("projectId", projectId)
            .bind("email", email)
            .execute()
    }

    override fun deleteAllForProject(projectId: UUID) {
        handle.createUpdate("""
            DELETE FROM m2s.project_invitations WHERE project_id = :projectId AND status = 'PENDING'
        """)
            .bind("projectId", projectId)
            .execute()
    }
}
