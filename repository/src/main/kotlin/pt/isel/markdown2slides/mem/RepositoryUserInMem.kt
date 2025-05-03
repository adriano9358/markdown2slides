package pt.isel.markdown2slides.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.RepositoryUser
import pt.isel.markdown2slides.User
import java.util.UUID

@Named
class RepositoryUserInMem: RepositoryUser {

    private val users = mutableMapOf<UUID, User>()
    override fun findById(id: UUID): User? {
        return users[id]
    }

    fun findByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }

    override fun findAll(): List<User> {
        return users.values.toList()
    }

    override fun save(entity: User) {
        users[entity.id] = entity
    }

    override fun deleteById(id: UUID) {
        users.remove(id)
    }

    override fun clear() {
        users.clear()
    }
}