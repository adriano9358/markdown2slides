package pt.isel.markdown2slides

import java.util.UUID

interface Repository<T> {
    fun findById(id: UUID): T?

    fun findAll(): List<T>

    fun save(entity: T)

    fun deleteById(id: UUID)

    fun clear()
}