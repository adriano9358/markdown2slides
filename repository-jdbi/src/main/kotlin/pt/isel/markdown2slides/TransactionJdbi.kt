package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle

class TransactionJdbi(
    private val handle: Handle,
) : Transaction {
    override val repoProjectInfo = RepositoryProjectInfoJdbi(handle)
    override val repoUser = RepositoryUserJdbi(handle)

    override fun rollback() {
        handle.rollback()
    }
}