package pt.isel.markdown2slides.data.mem

import pt.isel.markdown2slides.data.RepositoryProjectInfo
import pt.isel.markdown2slides.data.RepositoryUser
import pt.isel.markdown2slides.data.Transaction

class TransactionInMem(
    override val repoProjectInfo: RepositoryProjectInfo,
    override val repoUser: RepositoryUser,
    override val repoCollaborators: RepositoryCollaboratorsInMem,
    override val repoInvitations: RepositoryInvitationsInMem
) : Transaction {
    override fun rollback(): Unit = throw UnsupportedOperationException()
}