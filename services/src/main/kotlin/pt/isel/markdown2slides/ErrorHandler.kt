package pt.isel.markdown2slides

import java.io.IOException

inline fun <T> handleErrors(block: () -> Either<ProjectError, T>): Either<ProjectError, T> {
    return try {
        block()
    } catch (ex: DatabaseException) {
        failure(ProjectError.DatabaseError(ex.message ?: "Unknown database error"))
    } catch (ex: IOException) {
        failure(ProjectError.FileSystemError(ex.message ?: "Unknown file system error"))
    } catch (ex: Exception) {
        failure(ProjectError.UnknownError("Unexpected error: ${ex.message}"))
    }
}

class DatabaseException(message: String, cause: Throwable) : RuntimeException(message, cause)
class FileStorageException(message: String, cause: Throwable) : RuntimeException(message, cause)
