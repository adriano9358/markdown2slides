package pt.isel.markdown2slides

import org.jdbi.v3.core.Jdbi
import pt.isel.markdown2slides.data.Transaction
import pt.isel.markdown2slides.data.TransactionManager

class TransactionManagerJdbi(
    private val jdbi: Jdbi,
) : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = TransactionJdbi(handle)
            block(transaction)
        }
}
