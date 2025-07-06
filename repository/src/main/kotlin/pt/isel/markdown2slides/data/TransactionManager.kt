package pt.isel.markdown2slides.data

interface TransactionManager {
    fun <R> run(block: Transaction.() -> R): R
}