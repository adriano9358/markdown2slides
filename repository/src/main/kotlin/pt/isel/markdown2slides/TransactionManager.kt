package pt.isel.markdown2slides

interface TransactionManager {
    fun <R> run(block: Transaction.() -> R): R
}