package pt.isel.markdown2slides.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI


private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH = "placeholder/problems"
/*
sealed class Problem(
    typeUri: URI,
    detail: String,
    status: HttpStatus
) {
    val type = typeUri.toString()
    val title = typeUri.toString().split("/").last()
    val status = status.value()
    val detail = detail.trim()

    fun response(status: HttpStatus): ResponseEntity<Any> =
        ResponseEntity
            .status(status)
            .header(CONTENT_TYPE_HEADER, MEDIA_TYPE)
            .body(this)

    data object ConversionProcessFailure : Problem(URI("$PROBLEM_URI_PATH/conversion-process-failed"), "details", HttpStatus.SERVICE_UNAVAILABLE)

}*/

sealed class Problem(
    val type: URI,
    val title: String,
    val status: HttpStatus,
    val detail: String
) {
    fun response(): ResponseEntity<Any> = ResponseEntity
        .status(status)
        .header("Content-Type", "application/problem+json")
        .body(this)

    data object ConversionProcessFailure : Problem(
        URI("$PROBLEM_URI_PATH/conversion-process-failed"),
        "Conversion Process Failed",
        HttpStatus.SERVICE_UNAVAILABLE,
        "There was an error in the conversion process"
    )

    data class ValidationFailure(val errors: List<String>): Problem(
        URI("$PROBLEM_URI_PATH/validation-error"),
        "Invalid Parameters",
        HttpStatus.BAD_REQUEST,
        "One or more input values are invalid. Please check your request and try again."
    )
}
