package pt.isel.markdown2slides.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI


private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH = "placeholder/problems"

sealed class Problem(
    val type: URI,
    val title: String,
    val status: HttpStatus,
    val detail: String
) {
    fun response(): ResponseEntity<Any> = ResponseEntity
        .status(status)
        .header(CONTENT_TYPE_HEADER, MEDIA_TYPE)
        .body(this)

    data object ConversionProcessFailure : Problem(
        URI("$PROBLEM_URI_PATH/conversion-process-failed"),
        "Conversion Process Failed",
        HttpStatus.SERVICE_UNAVAILABLE,
        "There was an error in the conversion process"
    )

    data object ContentDeletionError : Problem(
        URI("$PROBLEM_URI_PATH/content-deletion-error"),
        "Content Deletion Error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "There was an error deleting the content."
    )

    data object ImageDeletionError : Problem(
        URI("$PROBLEM_URI_PATH/image-deletion-error"),
        "Image Deletion Error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "There was an error deleting the image."
    )

    data object ImageNotFound : Problem(
        URI("$PROBLEM_URI_PATH/image-not-found"),
        "Image Not Found",
        HttpStatus.NOT_FOUND,
        "The requested image was not found."
    )

    data object ProjectNotFound : Problem(
        URI("$PROBLEM_URI_PATH/project-not-found"),
        "Project Not Found",
        HttpStatus.NOT_FOUND,
        "The requested project was not found."
    )

    data object ProjectNotAuthorized : Problem(
        URI("$PROBLEM_URI_PATH/project-not-authorized"),
        "Project Not Authorized",
        HttpStatus.FORBIDDEN,
        "You are not authorized to access this project."
    )

    data object DatabaseError : Problem(
        URI("$PROBLEM_URI_PATH/database-error"),
        "Database Error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "There was an error accessing the database."
    )

    data object FileSystemError : Problem(
        URI("$PROBLEM_URI_PATH/file-system-error"),
        "File System Error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "There was an error accessing the file system."
    )

    data object UnknownError : Problem(
        URI("$PROBLEM_URI_PATH/unknown-error"),
        "Unknown Error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unknown error occurred. Please try again later."
    )

    data object InvalidInvitationResponse : Problem(
        URI("$PROBLEM_URI_PATH/invalid-invitation-response"),
        "Invalid Invitation Response",
        HttpStatus.BAD_REQUEST,
        "The invitation response is invalid."
    )

    data object InvalidRole : Problem(
        URI("$PROBLEM_URI_PATH/invalid-role"),
        "Invalid Role",
        HttpStatus.BAD_REQUEST,
        "The specified role is invalid."
    )

    data object InvitationAlreadyExists : Problem(
        URI("$PROBLEM_URI_PATH/invitation-already-exists"),
        "Invitation Already Exists",
        HttpStatus.CONFLICT,
        "An invitation with the same parameters already exists."
    )

    data object InvitationAlreadyResponded : Problem(
        URI("$PROBLEM_URI_PATH/invitation-already-responded"),
        "Invitation Already Responded",
        HttpStatus.CONFLICT,
        "You have already responded to this invitation."
    )

    data object InvitationNotFound : Problem(
        URI("$PROBLEM_URI_PATH/invitation-not-found"),
        "Invitation Not Found",
        HttpStatus.NOT_FOUND,
        "The requested invitation was not found."
    )

    data object InviterNotAuthorized : Problem(
        URI("$PROBLEM_URI_PATH/inviter-not-authorized"),
        "Inviter Not Authorized",
        HttpStatus.FORBIDDEN,
        "The inviter is not authorized to perform this action."
    )

    data object InviterNotFound : Problem(
        URI("$PROBLEM_URI_PATH/inviter-not-found"),
        "Inviter Not Found",
        HttpStatus.NOT_FOUND,
        "The inviter was not found."
    )

    data object UserAlreadyInProject : Problem(
        URI("$PROBLEM_URI_PATH/user-already-in-project"),
        "User Already In Project",
        HttpStatus.CONFLICT,
        "The user is already a member of this project."
    )

    data object UserNotFound : Problem(
        URI("$PROBLEM_URI_PATH/user-not-found"),
        "User Not Found",
        HttpStatus.NOT_FOUND,
        "The requested user was not found."
    )

    data object UserNotInProject : Problem(
        URI("$PROBLEM_URI_PATH/user-not-in-project"),
        "User Not In Project",
        HttpStatus.NOT_FOUND,
        "The user is not a member of this project."
    )

    data object PandocError : Problem(
        URI("$PROBLEM_URI_PATH/pandoc-error"),
        "Pandoc Error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "There was an error processing the document with Pandoc."
    )

    data object InvalidUpdates : Problem(
        URI("$PROBLEM_URI_PATH/invalid-updates"),
        "Invalid Updates",
        HttpStatus.BAD_REQUEST,
        "The updates provided are invalid."
    )

    data object VersionMismatch : Problem(
        URI("$PROBLEM_URI_PATH/version-mismatch"),
        "Version Mismatch",
        HttpStatus.CONFLICT,
        "The version of the updates does not match the current version."
    )


    data class ValidationFailure(val errors: List<String>): Problem(
        URI("$PROBLEM_URI_PATH/validation-error"),
        "Invalid Parameters",
        HttpStatus.BAD_REQUEST,
        "One or more input values are invalid. Please check your request and try again."
    )
}
