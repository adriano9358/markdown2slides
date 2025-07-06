package pt.isel.markdown2slides.data.mem

import jakarta.inject.Named
import pt.isel.markdown2slides.data.Transaction
import pt.isel.markdown2slides.data.TransactionManager

@Named
class TransactionManagerInMem : TransactionManager {
    private val repoUsers = RepositoryUserInMem()
    private val repoCollaborators = RepositoryCollaboratorsInMem(repoUsers)
    private val repoProjectInfo = RepositoryProjectInfoInMem(repoCollaborators)
    private val repoInvitations = RepositoryInvitationsInMem(repoProjectInfo)


    override fun <R> run(block: Transaction.() -> R): R = block(TransactionInMem(repoProjectInfo, repoUsers, repoCollaborators, repoInvitations))
}