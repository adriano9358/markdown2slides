package pt.isel.markdown2slides.data

import pt.isel.markdown2slides.User

interface RepositoryUser : Repository<User> {
    fun findByEmail(email: String): User?
}