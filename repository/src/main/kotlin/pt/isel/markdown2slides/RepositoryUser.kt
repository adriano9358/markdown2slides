package pt.isel.markdown2slides

interface RepositoryUser : Repository<User> {
    fun findByEmail(email: String): User?
}