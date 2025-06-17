package pt.isel.markdown2slides.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.Transaction
import pt.isel.markdown2slides.TransactionManager

@Named
class TransactionManagerInMem : TransactionManager {
    private val repoProjectInfo = RepositoryProjectInfoInMem()
    private val repoUsers = RepositoryUserInMem()
    private val repoCollaborators = RepositoryCollaboratorsInMem()
    private val repoInvitations = RepositoryInvitationsInMem()


    override fun <R> run(block: Transaction.() -> R): R = block(TransactionInMem(repoProjectInfo, repoUsers, repoCollaborators, repoInvitations))
}