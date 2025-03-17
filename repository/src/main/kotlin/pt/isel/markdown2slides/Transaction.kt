package pt.isel.markdown2slides

interface Transaction {
    val repoProjectInfo: RepositoryProjectInfo
    //val repoProjectContent: RepositoryProjectContent
    val repoUser: RepositoryUser

    fun rollback()
}