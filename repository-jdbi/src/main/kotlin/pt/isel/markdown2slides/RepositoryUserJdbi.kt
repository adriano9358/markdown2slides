package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import java.util.*


class RepositoryUserJdbi(
    private val handle: Handle,
) : RepositoryUser {
    override fun findById(id: UUID): User? {
        return handle
            .createQuery("SELECT * FROM m2s.users WHERE id = :id")
            .bind("id", id)
            .mapTo(User::class.java)
            .findOne()
            .orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return handle
            .createQuery("SELECT * FROM m2s.users WHERE email = :email")
            .bind("email", email)
            .mapTo(User::class.java)
            .findOne()
            .orElse(null)
    }

    override fun findAll(): List<User> {
        return handle
            .createQuery("SELECT * FROM m2s.users")
            .mapTo(User::class.java)
            .list()
    }

    override fun save(entity: User) {
        handle.createUpdate(
            """
            INSERT INTO m2s.users(id, name, email)
            VALUES (:id, :name, :email)
            ON CONFLICT (id) DO UPDATE
            SET name = :name, email = :email
            """.trimIndent()
        )
            .bind("id", entity.id)
            .bind("name", entity.name)
            .bind("email", entity.email)
            .execute()
    }

    override fun deleteById(id: UUID) {
        handle.createUpdate(
            """
            DELETE FROM m2s.users WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate(
            """
            DELETE FROM m2s.users
            """.trimIndent()
        )
            .execute()
    }


}
