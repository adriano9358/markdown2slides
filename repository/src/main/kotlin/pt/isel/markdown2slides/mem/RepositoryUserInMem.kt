package pt.isel.markdown2slides.mem

import pt.isel.markdown2slides.RepositoryUser
import pt.isel.markdown2slides.User

class RepositoryUserInMem: RepositoryUser {
    override fun findById(id: Int): User? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun save(entity: User) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}