package pt.isel.markdown2slides

interface Repository<T> {
    fun findById(id: Int): T?

    fun findAll(): List<T>

    fun save(entity: T)

    fun deleteById(id: Int)

    fun clear()
}