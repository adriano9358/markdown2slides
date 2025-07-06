package pt.isel.markdown2slides.data

interface Transaction {
    val repoProjectInfo: RepositoryProjectInfo
    val repoUser: RepositoryUser
    val repoCollaborators: RepositoryCollaborators
    val repoInvitations: RepositoryInvitations

    fun rollback()
}