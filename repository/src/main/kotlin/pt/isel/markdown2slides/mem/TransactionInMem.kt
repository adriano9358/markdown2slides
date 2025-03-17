package pt.isel.markdown2slides.mem

import pt.isel.markdown2slides.RepositoryProjectInfo
import pt.isel.markdown2slides.RepositoryUser
import pt.isel.markdown2slides.Transaction

class TransactionInMem(
    override val repoProjectInfo: RepositoryProjectInfo,
    override val repoUser: RepositoryUser
) : Transaction {
    override fun rollback(): Unit = throw UnsupportedOperationException()
}