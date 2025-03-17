package pt.isel.markdown2slides

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val passwordValidation: PasswordValidationInfo,
)

class PasswordValidationInfo {

}
