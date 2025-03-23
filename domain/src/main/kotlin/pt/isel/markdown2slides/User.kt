package pt.isel.markdown2slides

import java.util.*

data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val passwordValidation: PasswordValidationInfo,
)

class PasswordValidationInfo {

}
