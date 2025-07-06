package pt.isel.markdown2slides

import org.jdbi.v3.core.Handle
import pt.isel.markdown2slides.data.Transaction

class TransactionJdbi(
    private val handle: Handle,
) : Transaction {
    override val repoProjectInfo = RepositoryProjectInfoJdbi(handle)
    override val repoUser = RepositoryUserJdbi(handle)
    override val repoCollaborators = RepositoryCollaboratorsJdbi(handle)
    override val repoInvitations = RepositoryInvitationsJdbi(handle)

    override fun rollback() {
        handle.rollback()
    }
}
