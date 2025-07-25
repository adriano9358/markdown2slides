package pt.isel.markdown2slides


object Environment {
    fun getDbUrl() = System.getenv(KEY_DB_URL)
                        ?: System.getProperty(KEY_DB_URL)
                        ?: throw Exception("Missing env var $KEY_DB_URL")

    private const val KEY_DB_URL = "DB_URL"
}