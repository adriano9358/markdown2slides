package pt.isel.markdown2slides.mem

import pt.isel.markdown2slides.RepositoryUser
import pt.isel.markdown2slides.User
import java.util.UUID

class RepositoryUserInMem: RepositoryUser {
    override fun findById(id: UUID): User? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun save(entity: User) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}