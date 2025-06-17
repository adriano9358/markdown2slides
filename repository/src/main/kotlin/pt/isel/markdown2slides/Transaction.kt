package pt.isel.markdown2slides

interface Transaction {
    val repoProjectInfo: RepositoryProjectInfo
    //val repoProjectContent: RepositoryProjectContent
    val repoUser: RepositoryUser
    val repoCollaborators: RepositoryCollaborators
    val repoInvitations: RepositoryInvitations

    fun rollback()
}